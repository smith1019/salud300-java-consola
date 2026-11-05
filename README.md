# Salud300

Aplicación de consola en Java para gestionar especialidades, profesionales, pacientes y citas de un centro de salud. Es mi proyecto integrador de backend, construido siguiendo la arquitectura por capas vista en clase (puertos de entrada y salida).

## Tecnologías

- Java 21
- Interfaz de consola (menú por teclado)
- Datos guardados en memoria (listas `ArrayList`); al cerrar el programa se pierden

## Requisitos

- JDK 21 o superior
- IntelliJ IDEA 

## Cómo ejecutarlo

Con IntelliJ IDEA:

1. Abrir la carpeta del proyecto.
2. Abrir `src/main/java/app/Application.java`.
3. Pulsar el triángulo verde junto a `main` (Run).

## Funcionalidades

Implementadas (desde el menú):

- Crear y listar especialidades
- Crear y listar profesionales (cada uno con su especialidad)
- Crear y listar pacientes
- Crear y listar citas (paciente + profesional + fecha y hora + estado)

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
- Persistencia en base de datos

<img width="1646" height="867" alt="menu" src="https://github.com/user-attachments/assets/6d523c99-feaf-4403-a2a6-5db87e266e3c" />

## Estructura del proyecto

```
src/main/java/app
├── Application.java          Punto de entrada: crea los objetos y arranca el menú
├── domain                    Entidades (solo datos) y enums
├── repository                Implementaciones que guardan en listas
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

`View` → `UseCase` (interfaz) → `ServiceImpl` → `RepositoryInterface` (interfaz) → `RepositoryImpl`

Cada capa depende de interfaces y no de clases concretas. Las dependencias se entregan por el constructor (inyección de dependencias) y se conectan en `Application`.

## Autor

Anderson y
deiby
