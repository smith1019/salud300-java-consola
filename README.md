# Salud300

Aplicación de consola en Java para gestionar especialidades, profesionales, pacientes y citas de un centro de salud. Es mi proyecto integrador de backend, construido siguiendo la arquitectura por capas vista en clase (puertos de entrada y salida).

## Tecnologías

- Java 21
- Maven
- Interfaz de consola (menú por teclado)
- Base de datos MariaDB/MySQL (XAMPP + phpMyAdmin), conectada con JDBC

## Requisitos

- JDK 21 o superior
- IntelliJ IDEA
- XAMPP (MySQL/MariaDB) con phpMyAdmin

## Base de datos

1. Iniciar MySQL desde el panel de XAMPP.
2. Abrir phpMyAdmin (`http://localhost/phpmyadmin`), ir a la pestaña **SQL**, pegar el contenido de `database/salud300.sql` y ejecutarlo. Esto crea la base `salud300` y sus tablas.
3. En `src/main/resources`, copiar `database.properties.example` con el nombre `database.properties` y completar el usuario y la contraseña de MySQL (en XAMPP suele ser `root` sin contraseña).

El archivo `database.properties` no se sube a GitHub porque contiene datos de acceso.

## Cómo ejecutarlo

Con IntelliJ IDEA:

1. Abrir la carpeta del proyecto y esperar a que Maven descargue las dependencias.
2. Preparar la base de datos (sección anterior).
3. Abrir `src/main/java/app/Application.java`.
4. Pulsar el triángulo verde junto a `main` (Run).

## Funcionalidades

Implementadas (desde el menú):

- Crear y listar especialidades
- Crear y listar profesionales (cada uno con su especialidad)
- Crear y listar pacientes
- Crear y listar citas (paciente + profesional + fecha y hora + estado)

Los datos se guardan en la base de datos y se conservan al cerrar el programa.

Reglas de negocio:

- No se repiten ids dentro de la misma entidad
- El id debe ser mayor que cero
- Un profesional solo se crea con una especialidad existente
- La fecha de nacimiento de un paciente no puede ser futura
- Una cita requiere un paciente y un profesional existentes
- Un profesional no puede tener dos citas en la misma fecha y hora

Orden de uso recomendado: especialidad → profesional → paciente → cita.

Pendiente (mejoras futuras):

- Buscar, actualizar y borrar desde el menú (la lógica ya existe en los servicios y repositorios)
- Flujo completo para: alergias, medicamentos, consultas, signos vitales, diagnósticos, tratamientos, prescripciones, historia clínica y alergias del paciente (hoy solo existen como clases del dominio; alergias y medicamentos tienen además sus interfaces)
- Tablas en la base de datos para las entidades pendientes

## Estructura del proyecto

```
database
└── salud300.sql              Script que crea la base de datos y las tablas

src/main/resources
├── database.properties.example   Plantilla de la configuración de conexión
└── database.properties           Datos de conexión (local, no se sube a GitHub)

src/main/java/app
├── Application.java          Punto de entrada: crea los objetos y arranca el menú
├── config                    Conexión a la base de datos (DatabaseConnection, DatabaseException)
├── domain                    Entidades (solo datos) y enums
├── repository
│   ├── *RepositoryJdbc.java  Guardan en la base de datos (los que usa la aplicación)
│   └── *RepositoryImpl.java  Guardan en listas en memoria (versión inicial)
├── service
│   ├── inputports            Interfaces UseCase (lo que la app puede hacer)
│   ├── outputports           Interfaces RepositoryInterface (lo que el servicio necesita)
│   ├── helpers               Menús de selección (género, estado de cita)
│   └── *ServiceImpl.java     Lógica de negocio
├── userinterface             Menú principal
├── utils                     FormRuleValidator (lectura validada del teclado)
└── view                      Vistas de consola
```

## Arquitectura

El flujo de cada operación es:

`View` → `UseCase` (interfaz) → `ServiceImpl` → `RepositoryInterface` (interfaz) → `RepositoryJdbc` → base de datos

Cada capa depende de interfaces y no de clases concretas. Las dependencias se entregan por el constructor (inyección de dependencias) y se conectan en `Application`. Por eso se pudo pasar de listas en memoria a una base de datos cambiando solo las clases de repositorio que se instancian en `Application`.

## Autor

Anderson y
deiby