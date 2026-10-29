🚁 Gestor de Drones — Arquitectura Hexagonal & Patrones de Comportamiento

Proyecto académico desarrollado para el curso Ingeniería de Software II.
Implementa una arquitectura hexagonal completa con cuatro patrones de comportamiento integrados sobre un sistema de gestión de vehículos aéreos no tripulados.

📋 Descripción General

Sistema desktop desarrollado en Java con JavaFX para la administración integral de drones, con persistencia en PostgreSQL, consulta de clima en tiempo real mediante las APIs de Open-Meteo, y la implementación de cuatro patrones de diseño de comportamiento:

#	Patrón	Propósito
1	Observer	Suscripción de drones a alertas climáticas en tiempo real
2	Chain of Responsibility	Evaluación secuencial de 5 condiciones de viabilidad de vuelo
3	Strategy	Cálculo de consumo energético ponderado por peso del dron
4	Command	Programación y ejecución secuencial de tareas de misión
🛠️ Tecnologías Utilizadas
Tecnología	Versión	Uso
Java	11 / 21	Lenguaje principal
JavaFX	13	Interfaz gráfica de usuario
PostgreSQL	42.7.3	Persistencia relacional
Jackson Databind	2.15.2	Procesamiento JSON
Apache Maven	—	Gestión de dependencias y build
java.net.http	(JDK 11)	Cliente HTTP para APIs externas

Patrones Arquitectónicos:

Hexagonal (Ports & Adapters) — Desacoplamiento total entre capas
Singleton — Conexión única a base de datos
Factory Method — Ensamblado de la cadena de responsabilidad
✨ Funcionalidades
1. 📁 Gestión de Drones (CRUD)
Registro, consulta, actualización y eliminación de drones.
Drones de Agricultura: atributo especializado capacidadTanque (L).
Drones de Vigilancia: atributo especializado detecciónTérmica (boolean).
Persistencia en PostgreSQL con manejo de errores de restricciones únicas (id, serial).
2. 📡 Monitoreo Climático — Patrón Observer
Suscripción y desuscripción dinámica de drones al sistema de alertas (sin modificar la BD).
Al consultar el clima de una ciudad, el WeatherSubject notifica automáticamente a todos los drones suscritos con las condiciones meteorológicas actuales.
3. ✈️ Evaluación de Viabilidad de Vuelo — Patrón Chain of Responsibility

La cadena evalúa las condiciones en orden estricto. Si alguna falla, la evaluación se detiene inmediatamente e informa la regla incumplida:

Eslabón	Regla
TemperatureHandler	Temperatura entre 0 °C y 40 °C
HumidityHandler	Humedad relativa ≤ 85 %
WindHandler	Velocidad del viento ≤ 40 km/h
PressureHandler	Presión atmosférica entre 950 y 1050 hPa
VisibilityHandler	Visibilidad ≥ 5 km
4. 🔋 Consumo Energético por Ruta — Patrón Strategy

Calcula el porcentaje de batería consumido para una ruta, considerando la distancia geodésica (fórmula de Haversine) y el peso real del dron:

Estrategia	Fórmula
AhorroStrategy	distancia × 0.5 × (1 + peso × 0.01)
NormalStrategy	distancia × 1.0 × (1 + peso × 0.015)
AltoRendimientoStrategy	distancia × 1.5 × (1 + peso × 0.02)

Si el consumo supera el 100 %, el sistema emite una Alerta Crítica de Batería Insuficiente.

5. 🎯 Programación de Misiones — Patrón Command

Permite programar una secuencia de tareas antes de iniciar una misión. Las tareas se encolan en orden y se ejecutan secuencialmente:

Comando	Descripción
TomarFotografiaCommand	Captura aérea 4K georreferenciada a 120 m de altura
EscanearZonaCommand	Barrido multiespectral (Agricultura) o perimetral LiDAR térmico (Vigilancia)
MedirTemperaturaCommand	Telemetría ambiental y térmica de motores
DroneOperacionService actúa como Receiver / Wrapper del dron sin modificar la entidad de dominio.
ProgramadorTareas actúa como Invoker, gestionando la cola y ejecutando la misión.
🏛️ Arquitectura Hexagonal
╔══════════════════════════════════════════════════════════════════════════════════╗
║                           ARQUITECTURA HEXAGONAL                                ║
║                                                                                  ║
║  ┌──────────────────┐    ┌──────────────────────────────┐    ┌────────────────┐  ║
║  │ ADAPTADOR ENTRADA│    │       CAPA APLICACIÓN        │    │ ADAPT. SALIDA  │  ║
║  │                  │    │                              │    │                │  ║
║  │  DroneView       │──> │  Crear/Listar/Actualizar/    │──> │ PostgresRepo   │  ║
║  │  DroneController │    │  EliminarDroneServicio       │    │ (PostgreSQL)   │  ║
║  │                  │    │  ConsultarClimaServicio      │──> │ OpenMeteoAdapt │  ║
║  │                  │    │  ProgramadorTareas (Invoker) │    │ (HTTP/REST)    │  ║
║  │                  │    │  DroneOperacionService       │    │ DatabaseConn   │  ║
║  └──────────────────┘    │  (Receiver/Wrapper)          │    │ (Singleton)    │  ║
║                           └──────────────────────────────┘    └────────────────┘  ║
║                                           │                                       ║
║                                           ▼                                       ║
║                                NÚCLEO DEL DOMINIO                                 ║
║   ┌──────────────────────────────────────────────────────────────────────────┐    ║
║   │ modelo:   Drone · Agricultura · Vigilancia · Piloto · Sensor · Mision   │    ║
║   │ weather:  Weather · WeatherObserver · WeatherSubject                    │    ║
║   │ chain:    WeatherHandler · Temp · Hum · Wind · Press · Vis · Factory    │    ║
║   │ strategy: ConsumoStrategy · Ahorro · Normal · AltoRendimiento · Ruta    │    ║
║   │ command:  DroneCommand · TomarFoto · EscanearZona · MedirTemperatura    │    ║
║   └──────────────────────────────────────────────────────────────────────────┘    ║
╚══════════════════════════════════════════════════════════════════════════════════╝
Flujo de Interacción
[DroneView]
    └──> [DroneController]
              ├──> [*UseCase] ──> [*Servicio] ──> [DroneRepository] ──> [PostgreSQL]
              ├──> [ConsultarClimaUseCase]
              │         └──> [ConsultarClimaServicio]
              │                    ├──> [WeatherApiClientPort] ──> [OpenMeteoAdapter] ──> (API)
              │                    ├──> [WeatherHandler (Chain)] ──> [FlightEvaluation]
              │                    └──> [WeatherSubject] ──> Notifica Observadores
              └──> [ProgramadorTareas (Invoker)]
                        └──> [DroneCommand[]] ──> [DroneOperacionService (Receiver)]
📂 Estructura del Proyecto
SW2/
├── pom.xml
├── README.md
└── src/
    └── main/
        ├── resources/
        │   ├── database.properties
        │   └── ruta.json                              # Rutas de vuelo con coordenadas
        └── java/
            ├── module-info.java
            ├── co/edu/poli/SW2/
            │   └── App.java                           # Composition Root / Wiring
            └── com/drone/
                ├── dominio/
                │   ├── modelo/                        # Entidades puras de negocio
                │   │   ├── Drone.java                 # Entidad abstracta base
                │   │   ├── Agricultura.java
                │   │   ├── Vigilancia.java
                │   │   ├── Piloto.java
                │   │   ├── Sensor.java
                │   │   └── Mision.java
                │   ├── weather/                       # Dominio climático
                │   │   ├── Weather.java
                │   │   ├── WeatherObserver.java       # Interfaz Observer
                │   │   ├── WeatherSubject.java        # Sujeto Observable
                │   │   └── chain/                     # Chain of Responsibility
                │   │       ├── FlightEvaluation.java
                │   │       ├── WeatherHandler.java    # Manejador abstracto
                │   │       ├── TemperatureHandler.java
                │   │       ├── HumidityHandler.java
                │   │       ├── WindHandler.java
                │   │       ├── PressureHandler.java
                │   │       ├── VisibilityHandler.java
                │   │       └── WeatherChainFactory.java
                │   ├── strategy/                      # Patrón Strategy
                │   │   ├── ConsumoStrategy.java       # Interfaz
                │   │   ├── AhorroStrategy.java
                │   │   ├── NormalStrategy.java
                │   │   ├── AltoRendimientoStrategy.java
                │   │   ├── PuntoGeografico.java
                │   │   ├── Ruta.java
                │   │   └── ResultadoConsumo.java
                │   └── command/                       # Patrón Command
                │       ├── DroneCommand.java          # Interfaz de comando
                │       ├── TomarFotografiaCommand.java
                │       ├── EscanearZonaCommand.java
                │       └── MedirTemperaturaCommand.java
                ├── aplicacion/
                │   ├── puerto/
                │   │   ├── entrada/                   # Puertos de entrada (Use Cases)
                │   │   │   ├── CrearDroneUseCase.java
                │   │   │   ├── ListarDronesUseCase.java
                │   │   │   ├── ActualizarDroneUseCase.java
                │   │   │   ├── EliminarDroneUseCase.java
                │   │   │   └── ConsultarClimaUseCase.java
                │   │   └── salida/                    # Puertos de salida
                │   │       ├── DroneRepository.java
                │   │       └── WeatherApiClientPort.java
                │   ├── servicio/                      # Implementación de casos de uso
                │   │   ├── CrearDroneServicio.java
                │   │   ├── ListarDronesServicio.java
                │   │   ├── ActualizarDroneServicio.java
                │   │   ├── EliminarDroneServicio.java
                │   │   └── ConsultarClimaServicio.java
                │   ├── service/                       # Servicios del Patrón Command
                │   │   ├── DroneOperacionService.java # Receiver / Wrapper de Drone
                │   │   └── ProgramadorTareas.java     # Invoker (cola de tareas)
                │   ├── usecase/
                │   │   └── CalcularConsumoUseCase.java # Contexto del Patrón Strategy
                │   └── weather/
                │       ├── WeatherResultDTO.java
                │       └── observer/
                │           └── DroneWeatherObserver.java
                └── infraestructura/
                    ├── persistencia/
                    │   ├── PostgresDroneRepository.java
                    │   └── Singleton.java             # Pool de conexión
                    ├── weather/
                    │   ├── GeoApiClient.java
                    │   ├── OpenMeteoWeatherClient.java
                    │   └── OpenMeteoAdapter.java
                    ├── loader/
                    │   └── JsonRutaLoader.java
                    ├── util/
                    │   └── HaversineUtil.java         # Distancia geodésica
                    ├── ui/
                    │   └── DroneController.java       # Adaptador de entrada
                    └── vista/
                        └── DroneView.java             # Interfaz JavaFX
⚙️ Configuración y Puesta en Marcha
Prerrequisitos
JDK 11 o superior instalado y configurado en el PATH.
PostgreSQL en ejecución.
Conexión a Internet activa (para consultar las APIs de Open-Meteo).
Eclipse con soporte para Maven o cualquier IDE compatible.
1. Configuración de Base de Datos

Crea o edita el archivo src/main/resources/database.properties:

properties
db.url=jdbc:postgresql://localhost:5432/nombre_de_tu_bd
db.user=tu_usuario
db.password=tu_contrasena

Ejecuta el siguiente script SQL en tu base de datos:

sql
CREATE TABLE IF NOT EXISTS drones (
    id               VARCHAR(50)    PRIMARY KEY,
    serial           VARCHAR(100)   UNIQUE NOT NULL,
    modelo           VARCHAR(100)   NOT NULL,
    fabricante       VARCHAR(100)   NOT NULL,
    peso             NUMERIC(10,2)  NOT NULL,
    tipo             VARCHAR(50)    NOT NULL
);
CREATE TABLE IF NOT EXISTS drones_agricultura (
    id_drone         VARCHAR(50)    PRIMARY KEY REFERENCES drones(id) ON DELETE CASCADE,
    capacidad_tanque NUMERIC(10,2)  NOT NULL
);
CREATE TABLE IF NOT EXISTS drones_vigilancia (
    id_drone          VARCHAR(50)    PRIMARY KEY REFERENCES drones(id) ON DELETE CASCADE,
    deteccion_termica BOOLEAN        NOT NULL
);
2. Ejecución desde Eclipse
File → Import → Maven → Existing Maven Projects → seleccionar la carpeta SW2.
Project → Clean... → limpiar el proyecto.
Clic derecho en co.edu.poli.SW2.App → Run As → Java Application.
📖 Guía de Uso
CRUD de Drones
Completa el formulario de la izquierda con los datos del dron.
Selecciona el tipo (Agricultura o Vigilancia) e ingresa el atributo especializado.
Usa los botones Crear, Actualizar o Eliminar según la operación.
Haz clic en cualquier fila de la tabla para cargar los datos del dron seleccionado.
Monitoreo Climático (Observer)
Selecciona un dron de la tabla.
Presiona Suscribir para añadirlo al sistema de alertas.
La etiqueta inferior mostrará: Drones suscritos (N): ID-001, ID-002...
Selecciona un dron suscrito y presiona Desuscribir para retirarlo.
Consulta y Evaluación de Vuelo (Chain of Responsibility)
Selecciona una ciudad en el menú desplegable.
Presiona Consultar Clima y Evaluar Vuelo.
Un pop-up mostrará:
Condiciones meteorológicas actuales (temperatura, humedad, viento, presión, visibilidad).
Veredicto: ✅ CLIMA APTO o ❌ CLIMA NO APTO con la regla exacta incumplida.
Notificación al listado de drones suscritos.
Estimación de Consumo (Strategy)
Selecciona un dron de la tabla (se usará su peso automáticamente).
Elige la ruta de vuelo del desplegable.
Selecciona el modo de vuelo: Ahorro, Normal o Alto Rendimiento.
Presiona Calcular Consumo.
El pop-up mostrará la distancia, consumo estimado y autonomía disponible.

⚠️ Si la batería es insuficiente en modo Alto Rendimiento, el sistema lo indicará como misión inviable.

Programación de Misión (Command)
Selecciona un dron de la tabla.
En la sección "Programación de Misión", presiona los botones para encolar tareas:
+ Foto → agrega TomarFotografiaCommand
+ Escaneo → agrega EscanearZonaCommand
+ Temperatura → agrega MedirTemperaturaCommand
La etiqueta de secuencia se actualiza en tiempo real: [1. Tomar Fotografía] ➔ [2. Escanear Zona] ➔ ...
Presiona 🚀 Ejecutar Misión para ejecutar todas las tareas en orden y ver el reporte consolidado.
Usa Limpiar para reiniciar la cola de tareas.
🧩 Diagramas UML
Diagrama del Dominio
plantuml
@startuml
package "com.drone.dominio" {
    package "modelo" {
        abstract class Drone
        class Agricultura
        class Vigilancia
        Drone <|-- Agricultura
        Drone <|-- Vigilancia
    }
    package "command" {
        interface DroneCommand
        class TomarFotografiaCommand
        class EscanearZonaCommand
        class MedirTemperaturaCommand
        DroneCommand <|.. TomarFotografiaCommand
        DroneCommand <|.. EscanearZonaCommand
        DroneCommand <|.. MedirTemperaturaCommand
    }
    package "strategy" {
        interface ConsumoStrategy
        class AhorroStrategy
        class NormalStrategy
        class AltoRendimientoStrategy
        ConsumoStrategy <|.. AhorroStrategy
        ConsumoStrategy <|.. NormalStrategy
        ConsumoStrategy <|.. AltoRendimientoStrategy
    }
    package "weather" {
        class Weather
        interface WeatherObserver
        class WeatherSubject
        WeatherSubject --> "*" WeatherObserver
    }
    package "weather.chain" {
        abstract class WeatherHandler
        class FlightEvaluation
        WeatherHandler --> FlightEvaluation
    }
}
@enduml
Diagrama de Arquitectura
plantuml
@startuml
package "Dominio" {
    abstract class Drone
    interface DroneCommand
    interface ConsumoStrategy
    class Weather
}
package "Aplicacion" {
    interface CrearDroneUseCase
    interface DroneRepository
    class ProgramadorTareas
    class DroneOperacionService
    class ConsultarClimaServicio
}
package "Infraestructura.Entrada" {
    class DroneController
    class DroneView
}
package "Infraestructura.Salida" {
    class PostgresDroneRepository
    class OpenMeteoAdapter
}
DroneView --> DroneController
DroneController --> CrearDroneUseCase
DroneController --> ProgramadorTareas
ProgramadorTareas --> DroneCommand
DroneOperacionService --> Drone
DroneRepository <|-- PostgresDroneRepository
@enduml
👥 Autores
Nombre	Programa	Universidad
(Tu nombre aquí)	Ingeniería de Sistemas	Politécnico Colombiano Jaime Isaza Cadavid
📄 Licencia

Este proyecto es de uso académico y fue desarrollado para el curso Ingeniería de Software II — Polítécnico Colombiano Jaime Isaza Cadavid.
