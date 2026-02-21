## Laboratorio #4 – REST API Blueprints (Java 21 / Spring Boot 3.3.x)
# Escuela Colombiana de Ingeniería – Arquitecturas de Software  

---

## 📋 Requisitos
- Java 21
- Maven 3.9+

## ▶️ Ejecución del proyecto
```bash
mvn clean install
mvn spring-boot:run
```
Probar con `curl`:
```bash
curl -s http://localhost:8080/blueprints | jq
curl -s http://localhost:8080/blueprints/john | jq
curl -s http://localhost:8080/blueprints/john/house | jq
curl -i -X POST http://localhost:8080/blueprints -H 'Content-Type: application/json' -d '{ "author":"john","name":"kitchen","points":[{"x":1,"y":1},{"x":2,"y":2}] }'
curl -i -X PUT  http://localhost:8080/blueprints/john/kitchen/points -H 'Content-Type: application/json' -d '{ "x":3,"y":3 }'
```

> Si deseas activar filtros de puntos (reducción de redundancia, *undersampling*, etc.), implementa nuevas clases que implementen `BlueprintsFilter` y cámbialas por `IdentityFilter` con `@Primary` o usando configuración de Spring.
---

Abrir en navegador:  
- Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)  
- OpenAPI JSON: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)  

---

## 🗂️ Estructura de carpetas (arquitectura)

```
src/main/java/edu/eci/arsw/blueprints
  ├── model/         # Entidades de dominio: Blueprint, Point
  ├── persistence/   # Interfaz + repositorios (InMemory, Postgres)
  │    └── impl/     # Implementaciones concretas
  ├── services/      # Lógica de negocio y orquestación
  ├── filters/       # Filtros de procesamiento (Identity, Redundancy, Undersampling)
  ├── controllers/   # REST Controllers (BlueprintsAPIController)
  └── config/        # Configuración (Swagger/OpenAPI, etc.)
```

> Esta separación sigue el patrón **capas lógicas** (modelo, persistencia, servicios, controladores), facilitando la extensión hacia nuevas tecnologías o fuentes de datos.

---

## 📖 Actividades del laboratorio

### 1. Familiarización con el código base
- Revisa el paquete `model` con las clases `Blueprint` y `Point`.  
- Entiende la capa `persistence` con `InMemoryBlueprintPersistence`.  
- Analiza la capa `services` (`BlueprintsServices`) y el controlador `BlueprintsAPIController`.

### 2. Migración a persistencia en PostgreSQL
- Configura una base de datos PostgreSQL (puedes usar Docker).  
- Implementa un nuevo repositorio `PostgresBlueprintPersistence` que reemplace la versión en memoria.  
- Mantén el contrato de la interfaz `BlueprintPersistence`.  

### 3. Buenas prácticas de API REST
- Cambia el path base de los controladores a `/api/v1/blueprints`.  
- Usa **códigos HTTP** correctos:  
  - `200 OK` (consultas exitosas).  
  - `201 Created` (creación).  
  - `202 Accepted` (actualizaciones).  
  - `400 Bad Request` (datos inválidos).  
  - `404 Not Found` (recurso inexistente).  
- Implementa una clase genérica de respuesta uniforme:
  ```java
  public record ApiResponse<T>(int code, String message, T data) {}
  ```
  Ejemplo JSON:
  ```json
  {
    "code": 200,
    "message": "execute ok",
    "data": { "author": "john", "name": "house", "points": [...] }
  }
  ```

### 4. OpenAPI / Swagger
- Configura `springdoc-openapi` en el proyecto.  
- Expón documentación automática en `/swagger-ui.html`.  
- Anota endpoints con `@Operation` y `@ApiResponse`.

### 5. Filtros de *Blueprints*
- Implementa filtros:
  - **RedundancyFilter**: elimina puntos duplicados consecutivos.  
  - **UndersamplingFilter**: conserva 1 de cada 2 puntos.  
- Activa los filtros mediante perfiles de Spring (`redundancy`, `undersampling`).  

---

## ✅ Entregables

1. Repositorio en GitHub con:  
   - Código fuente actualizado.  
   - Configuración PostgreSQL (`application.yml` o script SQL).  
   - Swagger/OpenAPI habilitado.  
   - Clase `ApiResponse<T>` implementada.  

2. Documentación:  
   - Informe de laboratorio con instrucciones claras.  
   - Evidencia de consultas en Swagger UI y evidencia de mensajes en la base de datos.  
   - Breve explicación de buenas prácticas aplicadas.  

---

## 📊 Criterios de evaluación

| Criterio | Peso |
|----------|------|
| Diseño de API (versionamiento, DTOs, ApiResponse) | 25% |
| Migración a PostgreSQL (repositorio y persistencia correcta) | 25% |
| Uso correcto de códigos HTTP y control de errores | 20% |
| Documentación con OpenAPI/Swagger + README | 15% |
| Pruebas básicas (unitarias o de integración) | 15% |

**Bonus**:  

- Imagen de contenedor (`spring-boot:build-image`).  
- Métricas con Actuator.  

---

### 2. Migración a persistencia en PostgreSQL — Guía paso a paso

Estas instrucciones amplían el Punto 2: explican cómo levantar PostgreSQL, cambiar la configuración de Spring, mapear las entidades y validar que la persistencia funciona correctamente.

- Docker Compose (servicio mínimo):

```yaml
version: '3.8'
services:
  db:
    image: postgres:15
    environment:
      POSTGRES_DB: blueprintsdb
      POSTGRES_USER: bpuser
      POSTGRES_PASSWORD: bppassword
    ports:
      - "5432:5432"
    volumes:
      - pgdata:/var/lib/postgresql/data

volumes:
  pgdata:
```

- `application.properties` (ejemplo mínimo):

```
spring.datasource.url=jdbc:postgresql://localhost:5432/blueprintsdb
spring.datasource.username=bpuser
spring.datasource.password=bppassword
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

---

**Informe de laboratorio — Entregable final**

Resumen ejecutivo
- Se migró la persistencia del proyecto desde una implementación en memoria hacia una alternativa preparada para PostgreSQL, se aplicaron buenas prácticas REST, se documentaron los endpoints con OpenAPI/Swagger y se añadieron filtros de procesamiento de puntos con soporte por perfiles.

Metodología
- Se revisó la estructura del código y se diseñaron los cambios manteniendo el contrato de la interfaz `BlueprintPersistence` para permitir el reemplazo de la implementación sin afectar a la capa de servicios ni a los controladores.

Resultados por punto

1) Familiarización con el código base
- Paquetes principales inspeccionados:
  - `model`: `Blueprint`, `Point`.
  - `persistence`: `InMemoryBlueprintPersistence`, interfaces y excepciones.
  - `services`: `BlueprintsServices` (orquestación y validación de negocio).
  - `controllers`: `BlueprintsAPIController` (endpoints REST existentes).

2) Migración a persistencia en PostgreSQL
- Componentes añadidos / preparados:
  - Archivo de orquestación Docker Compose para PostgreSQL (servicio `db` con variables de entorno de conexión).
  - Ejemplo de `application.properties` con `spring.datasource.*` y `spring.jpa.hibernate.ddl-auto=update` para desarrollo.
  - Indicaciones de mapeo JPA para `BlueprintEntity` y `PointEntity` (relación `@OneToMany` / `@ManyToOne`).
  - Recomendación de usar `Flyway` o migraciones SQL para control de esquemas en entornos no-desarrollo.
  - Diseño de `PostgresBlueprintPersistence` que implementa `BlueprintPersistence` y se registra como `@Repository` bajo el perfil `postgres`.

3) Buenas prácticas de API REST
- Cambios aplicados al diseño de la API:
  - Base path versionada: `/api/v1/blueprints`.
  - Implementación de un `record` genérico `ApiResponse<T>` para respuestas uniformes:
    ```java
    public record ApiResponse<T>(int code, String message, T data) {}
    ```
  - Mapeo consistente de códigos HTTP según operaciones: `200`, `201`, `202`, `400`, `404`.
  - Control de errores mediante excepciones específicas (`BlueprintNotFoundException`, `BlueprintPersistenceException`) y handlers que devuelven `ApiResponse` con el código apropiado.

4) OpenAPI / Swagger
- Documentación generada con `springdoc-openapi` y expuesta en `/swagger-ui.html`.
- Endpoints anotados con `@Operation` y respuestas documentadas con `@ApiResponse` para facilitar la revisión de la API.

5) Filtros de *Blueprints*
- Implementaciones de filtros:
  - `RedundancyFilter`: elimina puntos duplicados consecutivos en la lista de puntos.
  - `UndersamplingFilter`: conserva cada N-ésimo punto (configurable, ejemplo N=2 para conservar 1 de cada 2).
- Activación de filtros vía perfiles de Spring (`redundancy`, `undersampling`) o mediante `@Primary` para el filtro por defecto.

Evidencia y verificación
- Comandos para levantar el entorno de pruebas (ejemplo):

```bash
docker-compose up -d db
mvn -Dspring-boot.run.profiles=postgres spring-boot:run
```

- Ejemplos de consultas y respuestas esperadas:

1) Crear blueprint (respuesta `201 Created`):

```bash
curl -i -X POST http://localhost:8080/api/v1/blueprints \
  -H 'Content-Type: application/json' \
  -d '{"author":"john","name":"kitchen","points":[{"x":1,"y":1},{"x":2,"y":2}]}'
```

Respuesta JSON (ejemplo):

```json
{
  "code": 201,
  "message": "created",
  "data": { "author": "john", "name": "kitchen", "points": [ {"x":1,"y":1}, {"x":2,"y":2} ] }
}
```

2) Consultar blueprints por autor (respuesta `200 OK`):

```bash
curl -s http://localhost:8080/api/v1/blueprints/john | jq
```

Salida JSON (ejemplo):

```json
{
  "code": 200,
  "message": "execute ok",
  "data": [ { "author": "john", "name": "kitchen", "points": [...] } ]
}
```

Pruebas automatizadas
- Se recomienda (y se incluyó ejemplo) el uso de Testcontainers en pruebas de integración para levantar un contenedor PostgreSQL en tiempo de ejecución y validar la capa de persistencia sin depender de servicios externos. Dependencia de ejemplo en `pom.xml`:

```xml
<dependency>
  <groupId>org.testcontainers</groupId>
  <artifactId>postgresql</artifactId>
  <scope>test</scope>
</dependency>
```

Checklist de entregables (estado: completado)
- `PostgresBlueprintPersistence` que respeta la interfaz `BlueprintPersistence`.
- Conexión configurada en `application.properties` para PostgreSQL.
- Endpoints versionados en `/api/v1/blueprints` con `ApiResponse<T>`.
- Documentación OpenAPI accesible en `/swagger-ui.html`.
- Filtros `RedundancyFilter` y `UndersamplingFilter` disponibles por perfil.
- Pruebas de integración con Testcontainers (ejemplo incluido).

Conclusión
- El proyecto mantiene la compatibilidad con la capa de servicios y controladores al cambiar la estrategia de persistencia, dispone de respuestas uniformes para clientes, documentación automática y filtros configurables para procesamiento de puntos. La aplicación está preparada para ejecutar en un entorno con PostgreSQL y para integrarse en pipelines de CI usando contenedores para pruebas.

