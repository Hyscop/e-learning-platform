# E-Learning Platform - Microservices Architecture

A multi-tenant e-learning platform built with microservices architecture using Spring Boot, designed for learning modern backend development practices.

## 🏗️ Architecture Overview

This project implements a microservices-based e-learning platform with the following services:

- **API Gateway**: Entry point, routing, JWT validation
- **User Service**: Authentication, user management, role management
- **Course Service**: Course creation and curriculum management
- **Enrollment Service**: Student enrollment management
- **Progress Service**: Learning progress tracking, certificates
- **Notification Service**: Async notifications (email, in-app)
- **Media Service**: File and video upload handling
- **Search Service**: Full-text search with Elasticsearch

## 🛠️ Technology Stack

- **Language**: Java 21 LTS with Spring Boot 3.x
- **Databases**: PostgreSQL (User, Enrollment), MongoDB (Course, Progress)
- **Message Queue**: RabbitMQ
- **Cache**: Redis
- **Search**: Elasticsearch
- **Containerization**: Docker & Docker Compose
- **Monitoring**: Prometheus + Grafana
- **Logging**: ELK Stack

## 📋 Prerequisites

- Java 21 or higher
- Maven 3.8+
- Docker Desktop
- Git

## 🚀 Getting Started

### Local Development Setup

1. **Clone the repository**

   ```bash
   git clone <repository-url>
   cd e-learning-platform
   ```

2. **Build all services**

   ```bash
   mvn clean install
   ```

3. **Run with Docker Compose**

   ```bash
   docker-compose up -d
   ```

4. **Access services**
   - API Gateway: http://localhost:8080
   - User Service: http://localhost:8081

## 📁 Project Structure

```
e-learning-platform/
├── api-gateway/           # API Gateway service
├── user-service/          # User management & authentication
├── course-service/        # Course management
├── enrollment-service/    # Enrollment management
├── docker-compose.yml     # Docker orchestration
└── pom.xml               # Parent POM
```

## 🔧 Development Workflow

### Running Individual Services

Each service can be run independently:

```bash
cd user-service
mvn spring-boot:run
```

### Git Commit Convention

We follow conventional commits:

```
feat(user-service): add user registration endpoint
fix(course-service): resolve null pointer in course creation
refactor(api-gateway): improve routing configuration
docs(readme): update setup instructions
test(enrollment-service): add unit tests for enrollment logic
```

## 📊 Current Development Phase

**Phase 1**: Basic service setup with Docker and simple CRUD operations

- [x] Project structure initialization
- [ ] User Service implementation
- [ ] Basic Docker setup
- [ ] Docker Compose configuration

## 🧪 Testing

Run tests for all services:

```bash
mvn test
```

Run tests for a specific service:

```bash
cd user-service
mvn test
```

## 📝 License

This is an educational project for learning purposes.

## 🤝 Contributing

This is a personal learning project. Contributions welcome for educational improvements.

---

**Note**: This project is built incrementally as a learning exercise, prioritizing understanding over speed.
