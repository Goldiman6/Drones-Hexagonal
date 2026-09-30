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
├── co/edu/poli/SW2/
│   └── App.java                           # WIRING: Punto de entrada e inyección de dependencias
│
└── com/drone/
    ├── dominio/modelo/                    # NÚCLEO: Clases puras (Drone, Agricultura, Vigilancia)
    │
    ├── aplicacion/
    │   ├── puerto/
    │   │   ├── entrada/                   # INTERFACES USE CASE (CrearDroneUseCase, Listar...)
    │   │   └── salida/                    # INTERFAZ REPOSITORY (DroneRepository)
    │   └── servicio/                      # IMPLEMENTACIONES (CrearDroneServicio, Listar...)
    │
    └── infraestructura/
        ├── persistencia/                  # ADAPTADORES SALIDA (PostgresDroneRepository, Singleton)
        └── vista/                         # ADAPTADORES ENTRADA (DroneController, DroneView)
```

---

## Adaptadores disponibles

| Adaptador | Tipo | Puerto que implementa | Tecnología |
| :--- | :--- | :--- | :--- |
| `DroneController` | Entrada | `CrearDroneUseCase` `ListarDronesUseCase` `ActualizarDroneUseCase` `EliminarDroneUseCase` | JavaFX |
| `PostgresDroneRepository` | Salida | `DroneRepository` | PostgreSQL / JDBC |

---

## ⚙️ Configuración de la Base de Datos

La aplicación utiliza el patrón **Singleton** para mantener una única conexión global. Las credenciales no se encuentran quemadas en el código fuente (Hardcoded), sino que se leen desde un archivo de propiedades.

Para ejecutar el proyecto, debes asegurar que exista el archivo `database.properties` en `src/main/resources/` con el siguiente formato:

```properties
db.url=jdbc:postgresql://localhost:5432/nombre_de_tu_bd
db.user=tu_usuario
db.password=tu_contrasena
```
