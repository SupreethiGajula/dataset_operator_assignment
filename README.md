# JSON Group-By and Sort-By Operators

A RESTful Spring Boot backend service that persists arbitrary JSON records into PostgreSQL and supports dynamic `groupBy` and `sortBy` query operations using Java Streams.

---

## Tech Stack
* **Language & Framework:** Java 17+, Spring Boot (Web, Data JPA, Validation)
* **Database:** PostgreSQL (via Docker)
* **JSON Processing:** Jackson
* **Testing:** JUnit 5 & Mockito

---

## Architecture & Design Highlights
* **Layered Architecture:** Clean separation across `Controller`, `Service` (Interface + Implementation), `Repository`, and `Entity` layers.
* **Schema-Flexible Storage:** Arbitrary JSON records are serialized and stored in a `TEXT` column alongside dataset metadata, allowing any JSON structure without rigid table migrations.
* **Dynamic Query Engine:** Uses Jackson deserialization and Java Streams (`Collectors.groupingBy` and `Comparator`) to group and sort records dynamically at runtime across numeric and string fields.
* **Custom DTOs & Global Exception Handling:** Dedicated response DTOs (`InsertRecordResponse`, `QueryResponse`) with `@JsonInclude(JsonInclude.Include.NON_NULL)` and a `@RestControllerAdvice` global handler returning structured HTTP error responses (`400 Bad Request`, `404 Not Found`).

---

## Prerequisites
* **Java 17** or higher
* **Docker** (for running PostgreSQL)
* **Maven** (or use the included `./mvnw` wrapper)

---

## Setup & Running Locally

### 1. Start PostgreSQL via Docker
Run the following command to start a local PostgreSQL container and create the `dataset_db` database:
```bash
docker run --name postgres-dataset \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_DB=dataset_db \
  -p 5432:5432 \
  -d postgres
```

### 2. Verify Database Configuration
Ensure `src/main/resources/application.yml` matches your database credentials:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/dataset_db
    username: postgres
    password: postgres
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

server:
  port: 8080
```

### 3. Build & Run the Application
```bash
./mvnw spring-boot:run
```
The API will be available at `http://localhost:8080`.

### 4. Run Unit Tests
```bash
./mvnw test
```

---

## API Documentation & Examples

### 1. Insert Record
Persists an arbitrary JSON record into the specified dataset.

* **Endpoint:** `POST /api/dataset/{datasetName}/record`
* **Example Request:**
  ```bash
  curl -X POST http://localhost:8080/api/dataset/employee_dataset/record \
    -H "Content-Type: application/json" \
    -d '{
      "id": 1,
      "name": "John Doe",
      "age": 30,
      "department": "Engineering"
    }'
  ```
* **Response (`201 Created`):**
  ```json
  {
    "message": "Record added successfully",
    "dataset": "employee_dataset",
    "recordId": 1
  }
  ```

---

### 2. Query Dataset (Group-By)
Groups all records in a dataset by the specified JSON field.

* **Endpoint:** `GET /api/dataset/{datasetName}/query?groupBy={fieldName}`
* **Example Request:**
  ```bash
  curl "http://localhost:8080/api/dataset/employee_dataset/query?groupBy=department"
  ```
* **Response (`200 OK`):**
  ```json
  {
    "groupedRecords": {
      "Engineering": [
        { "id": 1, "name": "John Doe", "age": 30, "department": "Engineering" },
        { "id": 2, "name": "Jane Smith", "age": 25, "department": "Engineering" }
      ],
      "Marketing": [
        { "id": 3, "name": "Alice Brown", "age": 28, "department": "Marketing" }
      ]
    }
  }
  ```

---

### 3. Query Dataset (Sort-By)
Sorts all records in a dataset by the specified JSON field in ascending (`asc`) or descending (`desc`) order.

* **Endpoint:** `GET /api/dataset/{datasetName}/query?sortBy={fieldName}&order={asc|desc}`
* **Example Request:**
  ```bash
  curl "http://localhost:8080/api/dataset/employee_dataset/query?sortBy=age&order=asc"
  ```
* **Response (`200 OK`):**
  ```json
  {
    "sortedRecords": [
      { "id": 2, "name": "Jane Smith", "age": 25, "department": "Engineering" },
      { "id": 3, "name": "Alice Brown", "age": 28, "department": "Marketing" },
      { "id": 1, "name": "John Doe", "age": 30, "department": "Engineering" }
    ]
  }
  ```

---

### 4. Query Dataset (All Records)
If neither `groupBy` nor `sortBy` is provided, returns all records in the dataset.

* **Endpoint:** `GET /api/dataset/{datasetName}/query`
* **Response (`200 OK`):**
  ```json
  {
    "records": [
      { "id": 1, "name": "John Doe", "age": 30, "department": "Engineering" },
      { "id": 2, "name": "Jane Smith", "age": 25, "department": "Engineering" },
      { "id": 3, "name": "Alice Brown", "age": 28, "department": "Marketing" }
    ]
  }
  ```

---

## Error Responses

| HTTP Status | Condition | Example Message |
| :--- | :--- | :--- |
| `400 Bad Request` | Empty JSON body, malformed JSON, or invalid `order` parameter | `Invalid sort order: 'up'. Allowed values are 'asc' or 'desc'.` |
| `404 Not Found` | Querying a dataset name that has no stored records | `No records found for dataset: unknown_dataset` |