# 🧑‍💻 Test Users API

[![Build Status](https://github.com/JaimeCombita/test-users/actions/workflows/ci.yml/badge.svg)](https://github.com/JaimeCombita/test-users/actions/workflows/ci.yml)
[![codecov](https://codecov.io/gh/JaimeCombita/test-users/branch/main/graph/badge.svg)](https://codecov.io/gh/JaimeCombita/test-users)
![Java](https://img.shields.io/badge/Java-21-blue?logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3-brightgreen?logo=spring)

Generic test project to implement a **CRUD de usuarios y teléfonos** usando **Spring Boot 3**, **Java 21**, y **H2 Database**.  
Este proyecto sigue buenas prácticas de arquitectura en capas, DTOs de entrada/salida, validaciones y documentación con OpenAPI.

---

## 🚀 Stack Tecnológico

- **Java 21**
- **Spring Boot 3**
    - Spring Web
    - Spring Data JPA
    - Spring Validation
- **Hibernate** (ORM)
- **H2 Database** (en memoria para desarrollo)
- **MapStruct** (mapeo entre entidades y DTOs)
- **Lombok** (reducción de boilerplate)
- **Springdoc OpenAPI** (Swagger UI)
- **Maven** (gestión de dependencias y build)

---

## 🏛️ Arquitectura

El proyecto sigue una arquitectura en capas:

- **Controller** → expone los endpoints REST.
- **Service** → lógica de negocio.
- **Repository** → acceso a datos con Spring Data JPA.
- **Model** → entidades JPA (`User`, `Phone`).
- **DTOs** → objetos de transferencia para entrada (`UserRequestDTO`, `PhoneDTO`, `UserUpdateDTO`) y salida (`UserResponseDTO`).
- **Mapper** → conversión entre entidades y DTOs usando MapStruct.

Relaciones:
- `User` ↔ `Phone` → relación **OneToMany** con cascada y eliminación de huérfanos.

---
## 📂 Estructura del Proyecto
- test-users/
    - src/
        - main/
            - java/com/example/users/
                - controller/
                - service/
                - repository/
                - model/
            - resources/
                - application.yml
        - test/
    - pom.xml
    - README.md

## 📡 Endpoints

Base path: `/api/v1`

| Método | Endpoint            | Descripción                                |
|--------|---------------------|--------------------------------------------|
| POST   | `/user`             | Crear un nuevo usuario                     |
| GET    | `/users`            | Obtener todos los usuarios                 |
| GET    | `/user/{id}`        | Obtener usuario por ID                     |
| PUT    | `/user/{id}`        | Actualizar parcialmente un usuario (name, email, password, isActive) |
| DELETE | `/user`             | Eliminar todos los usuarios                |
| DELETE | `/user/{id}`        | Eliminar usuario por ID                    |

---

## ⚙️ Instrucciones para ejecutar el proyecto

1. **Clonar el repositorio**
   ```bash
   git clone https://github.com/JaimeCombita/test-users.git
   cd test-users

2. **Compilar el proyecto**
   ```bash
   mvn clean install
3. **Ejecutar la aplicacion**
   ```bash
   mvn spring-boot:run

## Acceder a la API
- **Home:** http://localhost:8080/api/v1/
- **Swagger UI:** http://localhost:8080/swagger-ui-html
- **OpenAPI JSON:** http://localhost:8080/v3/api-docs
- **OpenAPI YAML:** http://localhost:8080/v3/api-docs.yaml

## 📝 Explicación de diseño
- Separación DTOs entrada/salida → evita exponer información sensible como password.
- MapStruct con @AfterMapping → asegura relaciones bidireccionales (User ↔ Phone).
- Validaciones con Jakarta Validation → integridad de datos (@Email, @Size, @NotBlank).
- Relaciones JPA con cascada → al persistir un User, se persisten automáticamente sus Phone.
- Campos de auditoría (created, modified, lastLogin) → se poblan automáticamente y se marcan como readOnly en OpenAPI.
- Uso de ResponseEntity y códigos semánticos → 201 Created en POST, 204 No Content en DELETE.
- Arquitectura en capas → facilita mantenibilidad, escalabilidad y pruebas unitarias.
