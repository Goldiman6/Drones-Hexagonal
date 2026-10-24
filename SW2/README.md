# Gestor de Drones - Arquitectura Hexagonal & Patrones de Comportamiento

Aplicación para la gestión de vehículos aéreos no tripulados (Drones), desarrollada bajo una estricta **Arquitectura Hexagonal (Ports & Adapters)** e integrando los patrones de diseño de comportamiento **Observer** y **Chain of Responsibility** para el monitoreo meteorológico y la evaluación de viabilidad de vuelo en tiempo real.

---

## 🚀 Funcionalidades del Sistema

### 1. Gestión Integral de Drones (CRUD)
- **Drones de Agricultura:** Registro, listado, actualización y eliminación de drones con atributos especializados como la **capacidad del tanque de fumigación (L)**.
- **Drones de Vigilancia:** Administración de drones de seguridad con configuración de **detección térmica**.
- **Persistencia en PostgreSQL:** Almacenamiento relacional robusto con validación de restricciones únicas (`id` y `serial`) y manejo amigable de errores mediante pop-ups JavaFX.

### 2. Monitoreo Climático en Tiempo Real (Patrón Observer)
- **Suscripción Dinámica en Memoria:** Permite seleccionar cualquier dron de la tabla y suscribirlo/desuscribirlo del sistema de alertas climáticas mediante los botones **Suscribir** y **Desuscribir** (sin alterar la base de datos).
- **Notificación Multicast:** Al consultar las condiciones climáticas de una ciudad, el `WeatherSubject` notifica automáticamente a todos los drones observadores suscritos con los datos meteorológicos actualizados.

### 3. Evaluación de Viabilidad de Vuelo (Patrón Chain of Responsibility)
- **Cadena de Validación Estricta:** Evalúa secuencialmente las condiciones climáticas actuales obtenidas desde APIs externas para determinar si la flota de drones puede operar de manera segura.
- **Reglas de Operación:**
  1. `TemperatureHandler`: Temperatura entre **0°C y 40°C**.
  2. `HumidityHandler`: Humedad relativa **≤ 85%**.
  3. `WindHandler`: Velocidad del viento **≤ 40 km/h**.
  4. `PressureHandler`: Presión atmosférica entre **950 y 1050 hPa**.
  5. `VisibilityHandler`: Visibilidad **≥ 5 km**.
- **Detención Inmediata:** Si alguna condición no se cumple, la cadena se detiene en ese eslabón y genera un informe de rechazo indicando la regla exacta violada y el valor registrado. Si todas se cumplen, emite el veredicto: `CLIMA APTO PARA OPERACIÓN DE DRONES`.

### 4. Integración con APIs Meteorológicas Externas
- **API de Geocodificación (Open-Meteo):** Convierte el nombre de la ciudad seleccionada (`Bogota` o `Brujas`) en coordenadas geográficas (`latitud` y `longitud`).
- **API de Pronóstico / Clima Actual (Open-Meteo):** Consulta en tiempo real los 5 aspectos meteorológicos clave mediante peticiones HTTP asíncronas con Java 11 `HttpClient` y parseo JSON con Jackson.

---

## 🛠️ Tecnologías Utilizadas

- **Lenguaje:** Java 11 / Java 21
- **Interfaz Gráfica:** JavaFX 13 (`javafx.controls`, `javafx.fxml`)
- **Base de Datos:** PostgreSQL 42.7.3
- **Conexión a BD:** JDBC (`java.sql`) con Pool/Singleton
- **Cliente HTTP:** `java.net.http.HttpClient`
- **Procesamiento JSON:** Jackson Databind 2.15.2
- **Gestor de Construcción:** Apache Maven
- **Estilo Arquitectónico:** Arquitectura Hexagonal (Puertos y Adaptadores)
- **Patrones de Diseño Implementados:**
  - *Arquitectónico:* Hexagonal (Ports & Adapters)
  - *Creacional:* Singleton (Conexión BD), Factory Method (Cadena de validación)
  - *Comportamiento:* Observer (Suscripción climática), Chain of Responsibility (Viabilidad de vuelo)

---

## 🏛️ Arquitectura Hexagonal del Sistema

```text
 ---------------------------------------------------------------------------------------------------------
|                                                HEXÁGONO                                                 |
|                                                                                                         |
|   ┌────────────────────────┐         ┌───────────────────────────────┐         ┌────────────────────┐   |
|   │  ADAPTADOR DE ENTRADA  │         │          APLICACIÓN           │         │ADAPTADORES DE SALID│   |
|   │                        │         │        (Casos de Uso)         │         │                    │   |
|   │  DroneController.java  │ ------> │  CrearDroneServicio           │ ------> │PostgresDroneRepo   │   |
|   │  (JavaFX UI:           │         │  ListarDronesServicio         │         │(PostgreSQL BD)     │   |
|   │   DroneView.java)      │         │  ActualizarDroneServicio      │         ├────────────────────┤   |
|   │                        │         │  EliminarDroneServicio        │         │OpenMeteoAdapter    │   |
|   │                        │         │  ConsultarClimaServicio       │ ------> │(Open-Meteo APIs:   │   |
|   │                        │         │                               │         │ Geocoding + Clima) │   |
|   └────────────────────────┘         └───────────────────────────────┘         └────────────────────┘   |
|                                                      │                                                  |
|                                                      ▼                                                  |
|                                                   DOMINIO                                               |
|               Modelos: Drone · Agricultura · Vigilancia · Weather · FlightEvaluation                    |
|               Patrón Observer: WeatherObserver · WeatherSubject                                         |
|               Patrón Chain: WeatherHandler · Temp · Hum · Wind · Press · Vis · Factory                 |
 ---------------------------------------------------------------------------------------------------------
```

### Flujo de Interacción entre Capas

```
[UI: DroneView] ──> [DroneController]
                         │ (invoca puertos de entrada)
                         ▼
             [ConsultarClimaUseCase]
                         │ (implementado por)
                         ▼
             [ConsultarClimaServicio]
                ├──> [WeatherApiClientPort] ──> [OpenMeteoAdapter] ──> (APIs Externas)
                ├──> [WeatherHandler (Chain)] ──> [FlightEvaluation] (Evalúa 5 condiciones)
                └──> [WeatherSubject (Observer)] ──> Notifica a Drones Suscritos
```

---

## 🔌 Adaptadores del Sistema

| Adaptador | Tipo | Puerto que Implementa / Usa | Tecnología / Responsabilidad |
| :--- | :--- | :--- | :--- |
| **`DroneController`** | Entrada | Invoca `*UseCase` | Adaptador UI desacoplado de JavaFX; recibe eventos del usuario y delega a la aplicación. |
| **`DroneView`** | Entrada (Vista) | Usa `DroneController` | Interfaz gráfica JavaFX con tabla, formulario CRUD, controles de suscripción y diálogo emergente. |
| **`PostgresDroneRepository`**| Salida | `DroneRepository` | Persistencia relacional de drones en base de datos PostgreSQL mediante JDBC y manejo de transacciones. |
| **`OpenMeteoAdapter`** | Salida | `WeatherApiClientPort` | Cliente HTTP que consume las APIs REST de Open-Meteo (Geocoding + Forecast) y retorna el modelo `Weather`. |

---

## 📂 Estructura Completa del Proyecto

```text
SW2/
├── pom.xml
├── README.md
└── src/
    └── main/
        ├── resources/
        │   └── database.properties
        └── java/
            ├── module-info.java
            │
            ├── co/edu/poli/SW2/
            │   └── App.java                                      # Composition Root / Wiring central
            │
            └── com/drone/
                ├── dominio/
                │   ├── modelo/                                   # Entidades puras de negocio
                │   │   ├── Drone.java
                │   │   ├── Agricultura.java
                │   │   ├── Vigilancia.java
                │   │   ├── Piloto.java
                │   │   ├── Sensor.java
                │   │   └── Mision.java
                │   │
                │   └── weather/                                  # Dominio climático y patrones
                │       ├── Weather.java                          # Modelo con 5 aspectos meteorológicos
                │       ├── WeatherObserver.java                  # Interfaz del observador
                │       ├── WeatherSubject.java                   # Sujeto / Observable en memoria
                │       │
                │       └── chain/                                # Patrón Chain of Responsibility
                │           ├── FlightEvaluation.java             # Objeto de valor con el veredicto
                │           ├── WeatherHandler.java               # Manejador abstracto base
                │           ├── TemperatureHandler.java           # Regla: 0°C a 40°C
                │           ├── HumidityHandler.java              # Regla: <= 85%
                │           ├── WindHandler.java                  # Regla: <= 40 km/h
                │           ├── PressureHandler.java              # Regla: 950 a 1050 hPa
                │           ├── VisibilityHandler.java            # Regla: >= 5 km
                │           └── WeatherChainFactory.java          # Factoría ensambladora de la cadena
                │
                ├── aplicacion/
                │   ├── puerto/
                │   │   ├── entrada/                              # Interfaces de Casos de Uso
                │   │   │   ├── CrearDroneUseCase.java
                │   │   │   ├── ListarDronesUseCase.java
                │   │   │   ├── ActualizarDroneUseCase.java
                │   │   │   ├── EliminarDroneUseCase.java
                │   │   │   └── ConsultarClimaUseCase.java
                │   │   │
                │   │   └── salida/                               # Interfaces hacia el exterior
                │   │       ├── DroneRepository.java
                │   │       └── WeatherApiClientPort.java
                │   │
                │   ├── servicio/                                 # Implementación de Casos de Uso
                │   │   ├── CrearDroneServicio.java
                │   │   ├── ListarDronesServicio.java
                │   │   ├── ActualizarDroneServicio.java
                │   │   ├── EliminarDroneServicio.java
                │   │   └── ConsultarClimaServicio.java
                │   │
                │   └── weather/
                │       ├── WeatherResultDTO.java                 # DTO de transferencia global
                │       └── observer/
                │           └── DroneWeatherObserver.java         # Dron adaptado como observador
                │
                └── infraestructura/
                    ├── persistencia/                             # Adaptadores de base de datos
                    │   ├── PostgresDroneRepository.java
                    │   └── Singleton.java
                    │
                    ├── weather/                                  # Adaptadores de APIs externas
                    │   ├── GeoApiClient.java
                    │   ├── OpenMeteoWeatherClient.java
                    │   └── OpenMeteoAdapter.java
                    │
                    ├── ui/                                       # Adaptador de entrada
                    │   └── DroneController.java
                    │
                    └── vista/                                    # Interfaz de usuario JavaFX
                        └── DroneView.java
```

---

## ⚙️ Configuración y Puesta en Marcha

### 1. Prerrequisitos
- JDK 11 o superior instalado y configurado en el `PATH`.
- PostgreSQL en ejecución con la base de datos configurada.
- Conexión a Internet activa (para consultar las APIs de Open-Meteo).

### 2. Configuración de la Base de Datos
Crear o verificar el archivo `src/main/resources/database.properties` con las credenciales correspondientes:

```properties
db.url=jdbc:postgresql://localhost:5432/nombre_de_tu_bd
db.user=tu_usuario
db.password=tu_contrasena
```

Estructura de tablas requerida en PostgreSQL:

```sql
CREATE TABLE IF NOT EXISTS drones (
    id VARCHAR(50) PRIMARY KEY,
    serial VARCHAR(100) UNIQUE NOT NULL,
    modelo VARCHAR(100) NOT NULL,
    fabricante VARCHAR(100) NOT NULL,
    peso NUMERIC(10,2) NOT NULL,
    tipo VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS drones_agricultura (
    id_drone VARCHAR(50) PRIMARY KEY REFERENCES drones(id) ON DELETE CASCADE,
    capacidad_tanque NUMERIC(10,2) NOT NULL
);

CREATE TABLE IF NOT EXISTS drones_vigilancia (
    id_drone VARCHAR(50) PRIMARY KEY REFERENCES drones(id) ON DELETE CASCADE,
    deteccion_termica BOOLEAN NOT NULL
);
```

### 3. Ejecución desde Eclipse o IDE
1. Importar el proyecto como **Existing Maven Project**.
2. Realizar **Project -> Clean...**.
3. Ejecutar la clase principal: `co.edu.poli.SW2.App.java` como **Java Application**.

---

## 📖 Guía de Uso de la Aplicación

1. **Gestión CRUD:**
   - Diligencia los datos en el formulario izquierdo y presiona **Crear**.
   - Haz clic en cualquier dron de la tabla para cargar sus datos en el formulario y utilizar **Actualizar** o **Eliminar**.
2. **Monitoreo Climático (Observer):**
   - Selecciona un dron de la tabla y haz clic en **Suscribir**.
   - La etiqueta inferior mostrará los drones suscritos: `Drones suscritos (1): D-001`.
   - Para quitarlo de la lista, selecciónalo y presiona **Desuscribir**.
3. **Consulta y Evaluación de Vuelo (Chain of Responsibility):**
   - Selecciona la ciudad en el menú desplegable (`Bogota` o `Brujas`).
   - Presiona **Consultar Clima y Evaluar Vuelo**.
   - Se abrirá una ventana emergente detallando:
     - Aspectos meteorológicos actuales (temperatura, humedad, viento, presión y visibilidad).
     - Veredicto de viabilidad: `CLIMA APTO` o `CLIMA NO APTO` (indicando la regla incumplida).
     - Estado de autorización para la flota de drones suscritos.
