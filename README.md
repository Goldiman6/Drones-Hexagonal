# Gestión de Drones - Arquitectura Hexagonal

Este proyecto es una aplicación de gestión de vehículos no tripulados (Drones), rediseñada desde un modelo MVC tradicional hacia una estricta **Arquitectura Hexagonal (Ports & Adapters)**.

## 🚀 Funcionalidades de la Aplicación

La aplicación provee un sistema completo para administrar drones especializados:
- **Gestión (CRUD) de Drones de Agricultura:** Permite registrar, listar, actualizar y eliminar drones agrícolas, incluyendo atributos específicos como la capacidad del tanque de fumigación.
- **Gestión (CRUD) de Drones de Vigilancia:** Permite administrar drones de seguridad con opciones de configuración especial (ej. detección térmica).
- **Interfaz Gráfica (GUI):** Interfaz amigable e intuitiva desarrollada completamente en JavaFX.
- **Persistencia Segura:** Toda la información se almacena permanentemente en una base de datos relacional PostgreSQL.

## 🛠️ Tecnologías

- **Lenguaje:** Java 21+
- **Interfaz Gráfica:** JavaFX (Módulos `javafx.controls`, `javafx.fxml`)
- **Base de Datos:** PostgreSQL
- **Conexión a BD:** JDBC (`java.sql`)
- **Gestión de dependencias:** Maven
- **Arquitectura:** Hexagonal (Ports & Adapters)

---

## 📂 Estructura de Carpetas

El proyecto está rigurosamente dividido por responsabilidades para proteger el **Dominio** (el núcleo del software) de dependencias externas.

```text
src/main/java/
├── module-info.java
│
├── co/edu/poli/SW2/
│   └── App.java
│
└── com/drone/
    ├── aplicacion/
    │   ├── puerto/
    │   │   ├── entrada/
    │   │   │   ├── ActualizarDroneUseCase.java
    │   │   │   ├── CrearDroneUseCase.java
    │   │   │   ├── EliminarDroneUseCase.java
    │   │   │   └── ListarDronesUseCase.java
    │   │   │
    │   │   └── salida/
    │   │       └── DroneRepository.java
    │   │
    │   └── servicio/
    │       ├── ActualizarDroneServicio.java
    │       ├── CrearDroneServicio.java
    │       ├── EliminarDroneServicio.java
    │       └── ListarDronesServicio.java
    │
    ├── dominio/
    │   └── modelo/
    │       ├── Agricultura.java
    │       ├── Drone.java
    │       ├── Mision.java
    │       ├── Piloto.java
    │       ├── Sensor.java
    │       └── Vigilancia.java
    │
    └── infraestructura/
        ├── persistencia/
        │   ├── PostgresDroneRepository.java
        │   └── Singleton.java
        │
        ├── ui/
        │   └── DroneController.java
        │
        └── vista/
            └── DroneView.java
