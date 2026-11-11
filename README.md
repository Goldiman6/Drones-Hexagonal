# 🚁 Gestor de Drones — Arquitectura Hexagonal & Patrones de Diseño

> **Proyecto de Ingeniería de Software II**  
> Sistema de gestión y control de vehículos aéreos no tripulados (drones de agricultura y vigilancia), diseñado bajo **Arquitectura Hexagonal (Puertos y Adaptadores)** y principios de diseño **SOLID**, con persistencia en **PostgreSQL**, interfaz reactiva en **JavaFX** y análisis continuo de calidad en **SonarQube**.

---

## 🏆 Métricas de Calidad de Código (SonarQube)

El proyecto cuenta con verificación estricta en SonarQube cumpliendo el **Quality Gate**:

| Métrica | Calificación | Estado | Detalle |
| :--- | :---: | :---: | :--- |
| **Quality Gate** | **Passed** | 🟢 Aprobado | Cumplimiento total de condiciones de entrega |
| **Security** | **Grado A** | 🟢 0 Issues | Sin vulnerabilidades ni fugas de información sensible |
| **Reliability** | **Grado A** | 🟢 0 Bugs | Código libre de errores de ejecución o concurrencia |
| **Maintainability** | **Grado A** | 🟢 0 Code Smells | Alta legibilidad y modularidad |
| **Duplications** | **0.0%** | 🟢 0 Líneas | Principio DRY aplicado en todas las capas |

---

## 🛠️ Stack Tecnológico

* **Lenguaje:** Java 11 / Java 17+
* **Interfaz de Usuario:** JavaFX 13 (Controles y layouts reactivos)
* **Persistencia:** PostgreSQL 14+ (Driver JDBC `postgresql-42.7.3`)
* **Gestor de Construcción:** Apache Maven 3.9+
* **Serialización / APIs:** Jackson Databind 2.15.2, `java.net.http` (APIs meteorológicas)
* **Calidad y Análisis Estático:** SonarQube Community Edition & JaCoCo

---

## 🏛️ Arquitectura Hexagonal (Ports & Adapters)

El sistema aísla por completo el núcleo del negocio de los frameworks, la base de datos y la interfaz visual:

```text
src/main/java/com/drone/
├── dominio/                            # [HEXÁGONO CENTRAL - NÚCLEO PURO]
│   ├── modelo/                         # Entidades de negocio
│   │   ├── Drone.java                  # Clase base abstracta
│   │   ├── Agricultura.java            # Dron con capacidad de tanque (L)
│   │   └── Vigilancia.java             # Dron con cámara de detección térmica
│   ├── state/                          # Patrón State (Ciclo operativo de vuelo)
│   │   ├── DroneState.java             # Interfaz de estados con métodos default
│   │   ├── DroneWrapper.java           # Contexto del patrón State
│   │   ├── OffState.java               # Estado APAGADO
│   │   ├── ReadyState.java             # Estado EN_ESPERA
│   │   ├── FlyingState.java            # Estado VOLANDO
│   │   └── LandingState.java           # Estado ATERRIZANDO
│   └── strategy/                       # Patrón Strategy (Consumo energético)
│       ├── ConsumoStrategy.java        # Contrato de estrategia
│       ├── BaseConsumoStrategy.java    # Clase base abstracta (Cálculo ponderado)
│       ├── AhorroStrategy.java         # Modo Ahorro (Factor 0.5x)
│       ├── NormalStrategy.java         # Modo Normal (Factor 1.0x)
│       └── AltoRendimientoStrategy.java# Modo Rendimiento (Factor 1.5x)
│
├── aplicacion/                         # [CAPA DE APLICACIÓN - CASOS DE USO]
│   ├── puerto/                         # Interfaces de comunicación
│   │   ├── entrada/                    # Casos de uso de entrada
│   │   └── salida/                     # Puertos secundarios (DroneRepository)
│   ├── mediator/                       # Patrón Mediator (Coordinación de misiones)
│   │   ├── DroneMediator.java          # Interfaz del mediador
│   │   ├── MissionCoordinator.java     # Mediador concreto
│   │   ├── MissionExecutionResult.java # DTO con telemetría de misión
│   │   └── componentes/                # Componentes desacoplados (Batería, Comms, GPS, Sensores)
│   ├── chain/                          # Patrón Chain of Responsibility (Validación climática)
│   ├── command/                        # Patrón Command (Tareas y secuencias de misión)
│   └── observer/                       # Patrón Observer (Alertas climáticas en vivo)
│
└── infraestructura/                    # [ADAPTADORES EXTERNOS]
    ├── persistencia/                   # Adaptador de Base de Datos
    │   ├── Singleton.java              # Conexión única a PostgreSQL
    │   └── PostgresDroneRepository.java# Implementación JDBC transaccional
    ├── controlador/                    # Controladores de interfaz de usuario
    │   └── DroneController.java        # Orquestación de eventos UI
    └── vista/                          # Vistas en JavaFX
        └── DroneView.java              # Interfaz gráfica de usuario
```

---

## 🧩 Patrones de Diseño Implementados

### 1. Patrón State (Ciclo de Vida del Dron)
Controla el flujo operacional del dron previniendo transiciones ilícitas mediante polimorfismo puro (sin estructuras `if/switch`):
* `APAGADO` $\rightarrow$ `EN_ESPERA` $\rightarrow$ `VOLANDO` $\rightarrow$ `ATERRIZANDO` $\rightarrow$ `EN_ESPERA`
* Transiciones seguras: no se puede despegar estando apagado ni apagar los motores en pleno vuelo.

### 2. Patrón Mediator (Coordinación de Misiones)
Centraliza la comunicación entre 4 subsistemas críticos sin acoplamiento directo entre ellos:
* **`BatterySystem`:** Monitoreo y descuento de carga de batería.
* **`NavigationSystem`:** Gestión de planes de vuelo para los sectores **Alfa**, **Bravo** y **Charlie**.
* **`CommunicationSystem`:** Enlace de telemetría y estado de enlace satelital.
* **`SensorSystem`:** Diagnóstico de sensores térmicos y de aspersión agrícola.

### 3. Patrón Strategy (Consumo Energético Ponderado)
Calcula el consumo de batería en función de la distancia y el peso del dron:
$$\text{Consumo} = \text{Distancia} \times \text{FactorBase} \times (1 + (\text{Peso} \times \text{ImpactoPorKg}))$$
* **Modo Ahorro:** Factor 0.5x (+1.0% de impacto por kg).
* **Modo Normal:** Factor 1.0x (+1.5% de impacto por kg).
* **Modo Alto Rendimiento:** Factor 1.5x (+2.0% de impacto por kg).

### 4. Patrón Chain of Responsibility (Viabilidad de Vuelo)
Evalúa secuencialmente 5 condiciones meteorológicas antes del despegue:
1. `TemperatureHandler`: Rango permitido de 0 °C a 40 °C.
2. `HumidityHandler`: Humedad relativa $\le 85\%$.
3. `WindHandler`: Velocidad del viento $\le 40\text{ km/h}$.
4. `PressureHandler`: Presión atmosférica entre 950 y 1050 hPa.
5. `VisibilityHandler`: Visibilidad horizontal $\ge 5\text{ km}$.

### 5. Patrón Observer (Alertas Meteorológicas)
Permite la suscripción dinámica de drones para recibir notificaciones automáticas ante variaciones del clima provistas por la API de Open-Meteo.

### 6. Patrón Command (Secuenciamiento de Misiones)
Encapsula órdenes de vuelo (despegue, patrullaje, retorno a base) en objetos ejecutables para soportar historial y encolado de tareas.

### 7. Patrón Singleton (Gestión de Conexión a Base de Datos)
Centraliza la conexión a PostgreSQL con carga desacoplada de credenciales externas y logging seguro con `java.util.logging.Logger`.

---

## 🗄️ Modelo de Base de Datos (PostgreSQL)

Mapeo objeto-relacional de herencia implementado en PostgreSQL:

```sql
-- Tabla principal de Drones
CREATE TABLE IF NOT EXISTS drones (
    id VARCHAR(50) PRIMARY KEY,
    serial VARCHAR(100) UNIQUE NOT NULL,
    modelo VARCHAR(100) NOT NULL,
    fabricante VARCHAR(100) NOT NULL,
    peso DOUBLE PRECISION NOT NULL
);

-- Subtipo: Drones de Agricultura
CREATE TABLE IF NOT EXISTS drones_agricultura (
    id_drone VARCHAR(50) PRIMARY KEY REFERENCES drones(id) ON DELETE CASCADE,
    capacidad_tanque DOUBLE PRECISION NOT NULL
);

-- Subtipo: Drones de Vigilancia
CREATE TABLE IF NOT EXISTS drones_vigilancia (
    id_drone VARCHAR(50) PRIMARY KEY REFERENCES drones(id) ON DELETE CASCADE,
    deteccion_termica BOOLEAN NOT NULL
);
```

---

## ⚙️ Configuración y Puesta en Marcha

### Prerrequisitos
* **Java Development Kit (JDK):** Versión 11 o superior (recomendado JDK 17 o 21).
* **Apache Maven:** Versión 3.8 o superior.
* **PostgreSQL:** Servidor activo con la base de datos configurada.

### 1. Configuración de Credenciales
Crea o edita el archivo `src/main/resources/database.properties`:

```properties
db.url=jdbc:postgresql://localhost:5432/drones_db
db.user=tu_usuario
db.password=tu_contraseña
```

### 2. Compilación y Ejecución
Para compilar y lanzar la interfaz gráfica JavaFX:

```bash
# Compilar el proyecto
mvn clean compile

# Ejecutar la aplicación JavaFX
mvn javafx:run
```

### 3. Ejecución del Escáner de Calidad (SonarQube)
Para re-analizar el proyecto en tu servidor local de SonarQube:

```bash
mvn clean verify org.sonarsource.scanner.maven:sonar-maven-plugin:sonar \
  -Dsonar.projectKey=Drones \
  -Dsonar.projectName=Drones \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.token=tu_token_aqui
```
