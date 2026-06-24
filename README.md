<br />
<div align="center">
<h3 align="center">PRAGMA POWER-UP — micro-plazoleta</h3>
  <p align="center">
    Microservice responsible for managing restaurants, dishes, and orders for a restaurant chain with multiple branches.
  </p>
</div>

### Built With

* ![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=java&logoColor=white)
* ![Spring](https://img.shields.io/badge/Spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
* ![Gradle](https://img.shields.io/badge/Gradle-02303A.svg?style=for-the-badge&logo=Gradle&logoColor=white)
* ![MySQL](https://img.shields.io/badge/MySQL-00000F?style=for-the-badge&logo=mysql&logoColor=white)

### Service Dependencies

Communicates with the following microservices via Feign Client:
- **micro-usuarios** — validates user roles and retrieves employee data
- **micro-mensajeria** — sends SMS notifications on order status changes
- **micro-trazabilidad** — records order lifecycle logs

<!-- GETTING STARTED -->
## Getting Started

### Prerequisites

* JDK 17 [https://jdk.java.net/17/](https://jdk.java.net/17/)
* Gradle [https://gradle.org/install/](https://gradle.org/install/)
* MySQL [https://dev.mysql.com/downloads/installer/](https://dev.mysql.com/downloads/installer/)

### Recommended Tools
* IntelliJ Community [https://www.jetbrains.com/idea/download/](https://www.jetbrains.com/idea/download/)
* Postman [https://www.postman.com/downloads/](https://www.postman.com/downloads/)

### Environment Variables

Configure the following environment variables before running:

| Variable | Description |
|---|---|
| `SERVER_PORT` | Port on which the service runs |
| `SPRING_PROFILES_ACTIVE` | Active profile (e.g. `dev`) |
| `DB_URL` | MySQL JDBC connection URL |
| `DB_USERNAME` | Database username |
| `DB_PASSWORD` | Database password |
| `JWT_SECRET` | Secret key for JWT validation |
| `USERS_SERVICE_URL` | Base URL of micro-usuarios |
| `MESSAGING_SERVICE_URL` | Base URL of micro-mensajeria |
| `TRACEABILITY_SERVICE_URL` | Base URL of micro-trazabilidad |

### Installation

1. Clone the repo
2. Change directory
   ```sh
   cd micro-plazoleta
   ```
3. Create a MySQL database
4. Set the required environment variables
5. Build and run
   ```sh
   ./gradlew bootRun
   ```

<!-- USAGE -->
## Usage

Once running, open the Swagger UI in your browser:

```
http://localhost:<SERVER_PORT>/swagger-ui/index.html
```

### API Endpoints

| Method | Path | Role | Description |
|---|---|---|---|
| `POST` | `/api/v1/restaurant` | ADMIN | Create a restaurant |
| `GET` | `/api/v1/restaurant` | USER | List restaurants |
| `GET` | `/api/v1/restaurant/{restaurantId}/is-owner` | INTERNAL | Check restaurant ownership |
| `POST` | `/api/v1/dish` | OWNER | Create a dish |
| `PATCH` | `/api/v1/dish/{id}` | OWNER | Update a dish |
| `GET` | `/api/v1/dish/restaurant/{restaurantId}` | USER | List dishes by restaurant |
| `PATCH` | `/api/v1/dish/{id}/status` | OWNER | Toggle dish active/inactive |
| `POST` | `/api/v1/order` | CUSTOMER | Create an order |
| `GET` | `/api/v1/order` | EMPLOYEE | List orders by status |
| `PATCH` | `/api/v1/order/{orderId}/assign` | EMPLOYEE | Assign order to employee |
| `PATCH` | `/api/v1/order/{orderId}/ready` | EMPLOYEE | Mark order as ready |
| `PATCH` | `/api/v1/order/{orderId}/deliver` | EMPLOYEE | Mark order as delivered |
| `PATCH` | `/api/v1/order/{orderId}/cancel` | CUSTOMER | Cancel an order |

<!-- TESTS -->
## Tests

```sh
./gradlew test jacocoTestReport
```

Or right-click the test folder in IntelliJ and choose **Run tests with coverage**.
