-- Base de datos del proyecto Salud300
CREATE DATABASE IF NOT EXISTS salud300
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE salud300;

-- Especialidades
CREATE TABLE IF NOT EXISTS specialty (
                                         id   INT          NOT NULL,
                                         name VARCHAR(100) NOT NULL,
    PRIMARY KEY (id)
    );

-- Profesionales (cada uno pertenece a una especialidad)
CREATE TABLE IF NOT EXISTS professional (
                                            id             INT          NOT NULL,
                                            identification VARCHAR(30)  NOT NULL,
    name           VARCHAR(100) NOT NULL,
    last_name      VARCHAR(100) NOT NULL,
    license_number VARCHAR(50)  NOT NULL,
    email          VARCHAR(150) NOT NULL,
    specialty_id   INT          NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (specialty_id) REFERENCES specialty (id)
    );

-- Pacientes
CREATE TABLE IF NOT EXISTS patient (
                                       id             INT          NOT NULL,
                                       identification VARCHAR(30)  NOT NULL,
    name           VARCHAR(100) NOT NULL,
    last_name      VARCHAR(100) NOT NULL,
    gender         VARCHAR(10)  NOT NULL,
    date_of_birth  DATE         NOT NULL,
    phone          VARCHAR(30)  NOT NULL,
    PRIMARY KEY (id)
    );

-- Citas (un profesional no puede tener dos citas a la misma fecha y hora)
CREATE TABLE IF NOT EXISTS appointment (
                                           id               INT         NOT NULL,
                                           patient_id       INT         NOT NULL,
                                           professional_id  INT         NOT NULL,
                                           appointment_date DATETIME    NOT NULL,
                                           status           VARCHAR(15) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE (professional_id, appointment_date),
    FOREIGN KEY (patient_id)      REFERENCES patient (id),
    FOREIGN KEY (professional_id) REFERENCES professional (id)
    );