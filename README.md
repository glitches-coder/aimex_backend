# AIMEX Backend

**AI-Powered Expense Tracker - Backend Service**

Enterprise-grade RESTful API service for intelligent expense management with AI-driven categorization and analytics capabilities.

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5g.shields./img.shields.io/badge/MongoDB-Latest-greenimg.shields.io/badge/License-Proprietary-blue
## Overview

AIMEX Backend is a robust, scalable microservice built with Spring Boot that powers an intelligent expense tracking platform. The system leverages AI capabilities to automatically categorize expenses, provide financial insights, and help users maintain budget discipline through real-time analytics and monitoring.

### Key Features

- **Intelligent Expense Management**: Track and manage expenses with automated categorization
- **Budget Control**: Set and monitor budgets with real-time alerts and threshold management
- **Advanced Analytics**: Generate comprehensive financial reports and spending insights
- **Category Management**: Flexible categorization system for organizing financial data
- **RESTful API**: Clean, well-documented API endpoints for seamless integration
- **Production Monitoring**: Built-in Spring Boot Actuator for health checks and metrics
- **NoSQL Persistence**: High-performance MongoDB integration for scalable data storage

### Latest Enhancements (Nov 2025)

- AI-driven categorization pipeline with OpenAI (fallbacks + merchant caching)
- JWT-secured authentication layer with register/login flows
- Month-aware budget alerting with detailed status payloads
- Bulk expense import and recurring-expense detection heuristics
- Rich analytics suite: monthly summary, category breakdown, trends, AI insights

## Architecture

### Technology Stack

| Component | Technology | Version |
|-----------|-----------|---------|
| **Framework** | Spring Boot | 3.5.7 |
| **Language** | Java | 25 |
| **Database** | MongoDB | Latest |
| **Build Tool** | Maven | 3.x |
| **Monitoring** | Spring Actuator | 3.5.7 |

### Project Structure

```
backend/
├── src/
│   ├── main/
│   │   ├── java/com/aimex/backend/
│   │   │   ├── controller/          # REST API Controllers
│   │   │   │   ├── ExpensesController.java
│   │   │   │   ├── BudgetsController.java
│   │   │   │   ├── CategoriesController.java
│   │   │   │   └── AnalyticsController.java
│   │   │   ├── models/              # Domain Models
│   │   │   │   ├── Expense.java
│   │   │   │   ├── Budget.java
│   │   │   │   ├── Category.java
│   │   │   │   └── User.java
│   │   │   ├── repository/          # Data Access Layer
│   │   │   ├── service/             # Business Logic Layer
│   │   │   └── BackendApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── data/
│   └── test/                        # Test Suites
├── pom.xml
└── mvnw
```

## Prerequisites

Before running this application, ensure you have the following installed:

- **Java Development Kit (JDK) 25** or higher
- **Maven 3.6+** (or use included Maven wrapper)
- **MongoDB 4.4+** (running locally or remotely)
- **Docker** (optional, for containerized deployment)

## Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/glitches-coder/aimex_backend.git
cd aimex_backend/backend
```

### 2. Configure Database Connection

Edit `src/main/resources/application.properties`:

```properties
spring.application.name=backend
spring.data.mongodb.uri=mongodb://localhost:27017/expense_tracker
spring.data.mongodb.database=expense_tracker
spring.data.mongodb.auto-index-creation=true
```

**Environment-Specific Configuration:**

For production environments, use environment variables:

```bash
export MONGODB_URI=mongodb://username:password@production-host:27017/expense_tracker?authSource=admin
export MONGODB_DATABASE=expense_tracker
```

### 3. Build the Application

Using Maven wrapper (recommended):

```bash
./mvnw clean install
```

Or with local Maven:

```bash
mvn clean install
```

### 4. Run the Application

**Development Mode:**

```bash
./mvnw spring-boot:run
```

**Production Mode:**

```bash
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

The application will start on **http://localhost:8080**

## API Endpoints

### Expenses Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/expenses` | Retrieve all expenses |
| `GET` | `/api/expenses/{id}` | Get specific expense by ID |
| `POST` | `/api/expenses` | Create new expense |
| `PUT` | `/api/expenses/{id}` | Update existing expense |
| `DELETE` | `/api/expenses/{id}` | Delete expense |

### Budget Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/budgets` | List all budgets |
| `GET` | `/api/budgets/{id}` | Get budget details |
| `POST` | `/api/budgets` | Create budget |
| `PUT` | `/api/budgets/{id}` | Update budget |
| `DELETE` | `/api/budgets/{id}` | Remove budget |

### Category Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/categories` | List all categories |
| `GET` | `/api/categories/{id}` | Get category details |
| `POST` | `/api/categories` | Create category |
| `PUT` | `/api/categories/{id}` | Update category |
| `DELETE` | `/api/categories/{id}` | Delete category |

### Analytics

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/analytics/summary` | Get spending summary |
| `GET` | `/api/analytics/trends` | Analyze spending trends |
| `GET` | `/api/analytics/category-breakdown` | Category-wise breakdown |

### Health & Monitoring

| Endpoint | Description |
|----------|-------------|
| `/actuator/health` | Application health status |
| `/actuator/info` | Application information |
| `/actuator/metrics` | Performance metrics |

## Configuration

### Application Properties

Key configuration parameters:

```properties
# Server Configuration
server.port=8080
server.servlet.context-path=/

# MongoDB Configuration
spring.data.mongodb.uri=mongodb://localhost:27017/expense_tracker
spring.data.mongodb.database=expense_tracker
spring.data.mongodb.auto-index-creation=true

# Actuator Configuration
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=always

# Logging Configuration
logging.level.com.aimex.backend=INFO
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} - %msg%n
```

### Environment Variables

For production deployments, use environment variables:

```bash
SPRING_DATA_MONGODB_URI=mongodb://production-host:27017/expense_tracker
SPRING_DATA_MONGODB_DATABASE=expense_tracker
SERVER_PORT=8080
SPRING_PROFILES_ACTIVE=production
```

## Database Schema

### Collections

**Expenses Collection:**
- `_id`: ObjectId
- `userId`: String
- `amount`: Double
- `category`: String
- `description`: String
- `date`: Date
- `paymentMethod`: String
- `tags`: Array<String>

**Budgets Collection:**
- `_id`: ObjectId
- `userId`: String
- `category`: String
- `limit`: Double
- `period`: String
- `startDate`: Date
- `endDate`: Date

**Categories Collection:**
- `_id`: ObjectId
- `name`: String
- `description`: String
- `icon`: String
- `color`: String

**Users Collection:**
- `_id`: ObjectId
- `username`: String
- `email`: String
- `preferences`: Object

## Deployment

### Docker Deployment

Create a `Dockerfile`:

```dockerfile
FROM openjdk:25-jdk-slim
WORKDIR /app
COPY target/backend-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Build and run:

```bash
docker build -t aimex-backend .
docker run -p 8080:8080 -e MONGODB_URI=mongodb://host.docker.internal:27017/expense_tracker aimex-backend
```

### Docker Compose

```yaml
version: '3.8'
services:
  mongodb:
    image: mongo:latest
    ports:
      - "27017:27017"
    volumes:
      - mongodb_data:/data/db
    environment:
      MONGO_INITDB_DATABASE: expense_tracker

  backend:
    build: .
    ports:
      - "8080:8080"
    environment:
      SPRING_DATA_MONGODB_URI: mongodb://mongodb:27017/expense_tracker
    depends_on:
      - mongodb

volumes:
  mongodb_data:
```

Run with:

```bash
docker-compose up -d
```

### Cloud Deployment

**AWS Elastic Beanstalk:**

```bash
eb init -p java-17 aimex-backend
eb create production-env
eb deploy
```

**Kubernetes:**

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: aimex-backend
spec:
  replicas: 3
  selector:
    matchLabels:
      app: aimex-backend
  template:
    metadata:
      labels:
        app: aimex-backend
    spec:
      containers:
      - name: backend
        image: aimex-backend:latest
        ports:
        - containerPort: 8080
        env:
        - name: MONGODB_URI
          valueFrom:
            secretKeyRef:
              name: mongodb-secret
              key: uri
```

## Testing

### Run Unit Tests

```bash
./mvnw test
```

### Run Integration Tests

```bash
./mvnw verify
```

### Test Coverage

```bash
./mvnw clean test jacoco:report
```

View coverage report at `target/site/jacoco/index.html`

## Development

### Code Style

This project follows standard Java conventions and Spring Boot best practices. Use the included IDE configuration files in `.idea/` for consistent formatting.

### Hot Reload

Spring Boot DevTools is included for automatic application restart during development:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
    <scope>runtime</scope>
    <optional>true</optional>
</dependency>
```

### Debugging

Run in debug mode:

```bash
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
```

Attach your IDE debugger to port 5005.

## Monitoring & Operations

### Health Checks

```bash
curl http://localhost:8080/actuator/health
```

### Metrics

```bash
curl http://localhost:8080/actuator/metrics
```

### Logging

Configure logging levels in `application.properties`:

```properties
logging.level.root=WARN
logging.level.com.aimex.backend=DEBUG
logging.level.org.springframework.web=INFO
logging.file.name=logs/aimex-backend.log
```

## Performance Optimization

- **Connection Pooling**: MongoDB connection pooling configured automatically
- **Indexing**: Auto-index creation enabled for optimal query performance
- **Caching**: Consider adding Spring Cache for frequently accessed data
- **Pagination**: Implement pagination for large result sets

## Security Considerations

**For Production Deployment:**

- Enable HTTPS/TLS encryption
- Implement Spring Security for authentication/authorization
- Use MongoDB authentication and role-based access control
- Secure actuator endpoints with authentication
- Implement rate limiting for API endpoints
- Use environment variables for sensitive configuration
- Enable CORS only for trusted origins
- Implement input validation and sanitization
- Regular dependency updates for security patches

## Troubleshooting

### Common Issues

**MongoDB Connection Failed:**
```bash
# Verify MongoDB is running
sudo systemctl status mongod

# Check connection string in application.properties
spring.data.mongodb.uri=mongodb://localhost:27017/expense_tracker
```

**Port Already in Use:**
```bash
# Change port in application.properties
server.port=8081
```

**Out of Memory:**
```bash
# Increase JVM heap size
java -Xmx512m -Xms256m -jar target/backend-0.0.1-SNAPSHOT.jar
```

## Contributing

Contributions are welcome! Please follow these guidelines:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### Coding Standards

- Follow Java naming conventions
- Write comprehensive unit tests
- Document public APIs with Javadoc
- Keep methods focused and concise
- Use Lombok annotations to reduce boilerplate

## Roadmap

- [ ] JWT Authentication & Authorization
- [ ] AI-powered expense categorization
- [ ] Receipt image processing
- [ ] Multi-currency support
- [ ] Real-time notifications
- [ ] Export to PDF/Excel
- [ ] Machine learning insights
- [ ] GraphQL API support
- [ ] Microservices architecture
- [ ] Event-driven architecture with Kafka

***

**Built with ❤️ by Glitches-Coder**
