# E-Learning Platform - Microservices Architecture

A multi-tenant e-learning platform built with microservices architecture using Spring Boot, designed for practising modern backend development practices.

## 🏗️ Architecture Overview

This project implements a microservices-based e-learning platform with the following services:

### ✅ Implemented Services

- **API Gateway** - Entry point, JWT validation, header injection, routing
- **User Service** - Authentication, user management, JWT token generation
- **Course Service** - Course CRUD, role validation, instructor management

### 🚧 Planned Services

- **Enrollment Service** - Student enrollment management
- **Progress Service** - Learning progress tracking, certificates
- **Notification Service** - Async notifications (email, in-app)
- **Media Service** - File and video upload handling
- **Search Service** - Full-text search with Elasticsearch

## 🛠️ Technology Stack

### Backend

- **Language**: Java 21 LTS
- **Framework**: Spring Boot 3.2.0
- **Gateway**: Spring Cloud Gateway (Reactive WebFlux)
- **Security**: Spring Security 6 + JWT (JJWT 0.12.x)
- **Data Access**: Spring Data JPA, Spring Data MongoDB

### Databases

- **PostgreSQL** - User Service (relational data)
- **MongoDB 7** - Course Service (document storage with embedded modules/lessons)

### Infrastructure (Current)

- **Docker & Docker Compose** - Container orchestration
- **Postman** - API testing with 28+ security tests

### Infrastructure (Planned)

- **Message Queue**: RabbitMQ
- **Cache**: Redis
- **Search**: Elasticsearch
- **Monitoring**: Prometheus + Grafana
- **Logging**: ELK Stack

## 📋 Prerequisites

- Java 21 or higher
- Maven 3.8+
- Docker Desktop
- Git

## 📁 Project Structure

```
e-learning-platform/
├── api-gateway/              # API Gateway service (Port 8080)
│   ├── src/main/java/
│   │   └── com/elearning/gateway/
│   │       ├── security/
│   │       │   ├── JwtAuthenticationFilter.java  # JWT validation & header injection
│   │       │   └── JwtUtil.java                  # JWT parsing utilities
│   │       └── ApiGatewayApplication.java
│   └── src/main/resources/
│       └── application.yml                       # Gateway routes configuration
│
├── user-service/             # User Service (Port 8081)
│   ├── src/main/java/
│   │   └── com/elearning/user/
│   │       ├── controller/
│   │       │   ├── AuthController.java           # Login/Logout with JWT generation
│   │       │   └── UserController.java           # User CRUD operations
│   │       ├── security/
│   │       │   └── JwtService.java               # JWT token creation with claims
│   │       └── model/
│   │           └── User.java                     # User entity with roles
│   └── src/main/resources/
│       └── application.yml                       # PostgreSQL configuration
│
├── course-service/           # Course Service (Port 8082)
│   ├── src/main/java/
│   │   └── com/elearning/course/
│   │       ├── controller/
│   │       │   └── CourseController.java         # Course CRUD with role validation
│   │       ├── model/
│   │       │   ├── Course.java                   # Course with instructor names denormalized
│   │       │   ├── Module.java                   # Embedded modules
│   │       │   └── Lesson.java                   # Embedded lessons
│   │       ├── exception/
│   │       │   └── GlobalExceptionHandler.java   # Security & error handling
│   │       └── service/
│   │           └── CourseService.java            # Business logic with ownership validation
│   └── src/main/resources/
│       └── application.yml                       # MongoDB configuration
│
│
├── docker-compose.yml        # PostgreSQL + MongoDB orchestration
└── pom.xml                   # Parent POM with dependencies
```

## 📊 Current Development Status

### ✅ Phase 1: Core Infrastructure (COMPLETED)

- [x] Project structure with parent POM
- [x] Docker Compose setup (PostgreSQL + MongoDB)
- [x] Multi-module Maven configuration
- [x] Development environment setup

### ✅ Phase 2: User Service (COMPLETED)

- [x] User registration and authentication
- [x] JWT token generation with custom claims (role, firstName, lastName)
- [x] Spring Security 6 integration
- [x] PostgreSQL database with Flyway migrations
- [x] Role-based user model (STUDENT, INSTRUCTOR, ADMIN)

### ✅ Phase 3: API Gateway (COMPLETED)

- [x] Spring Cloud Gateway setup
- [x] JWT validation filter (reactive)
- [x] Header injection (X-User-Email, X-User-Role, X-User-FirstName, X-User-LastName)
- [x] Public/protected endpoint routing with HTTP method differentiation
- [x] Centralized error handling

### ✅ Phase 4: Course Service (COMPLETED)

- [x] Course CRUD operations
- [x] MongoDB integration with embedded documents (modules/lessons)
- [x] Role-based authorization (INSTRUCTOR/ADMIN only for create/update/delete)
- [x] Ownership validation (instructors can only modify own courses)
- [x] Instructor name denormalization for frontend performance

### 🚧 Phase 5: Enrollment Service (IN PROGRESS)

- [ ] Student enrollment in courses
- [ ] Enrollment validation
- [ ] Payment integration (future)

### 🚧 Phase 6: Advanced Features (PLANNED)

- [ ] Progress tracking
- [ ] Certificates
- [ ] Notifications
- [ ] Media upload
- [ ] Search functionality

## 🔐 Security Architecture

### JWT Token Flow

```
User Login → User Service generates JWT with claims:
  - sub: user@email.com
  - role: INSTRUCTOR
  - firstName: John
  - lastName: Doe

Request with JWT → API Gateway:
  1. Validates JWT signature
  2. Checks expiration
  3. Extracts claims
  4. Injects headers:
     - X-User-Email: user@email.com
     - X-User-Role: INSTRUCTOR
     - X-User-FirstName: John
     - X-User-LastName: Doe

Downstream Service receives headers → Validates role & ownership
```

### Authorization Matrix

| Action                | Student | Instructor | Admin |
| --------------------- | ------- | ---------- | ----- |
| Browse Courses        | ✅      | ✅         | ✅    |
| View Course Details   | ✅      | ✅         | ✅    |
| Create Course         | ❌      | ✅         | ✅    |
| Update Own Course     | ❌      | ✅         | ✅    |
| Update Other's Course | ❌      | ❌         | ✅    |
| Delete Own Course     | ❌      | ✅         | ✅    |
| Delete Other's Course | ❌      | ❌         | ✅    |

---

## 📚 API Documentation

### User Service Endpoints

**Public:**

- `POST /api/users/register` - Register new user
- `POST /api/users/auth/login` - Login and get JWT token
- `POST /api/users/auth/logout` - Logout

**Protected (Authenticated users):**

- `GET /api/users/` - Get all users
- `GET /api/users/{id}` - Get user by ID
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user

### Course Service Endpoints

**Public (No auth required):**

- `GET /api/courses` - Browse all courses
- `GET /api/courses/details/{id}` - Get course details
- `GET /api/courses/published` - Get published courses only
- `GET /api/courses/category/{category}` - Filter by category
- `GET /api/courses/level/{level}` - Filter by level
- `GET /api/courses/search?title=keyword` - Search courses
- `GET /api/courses/instructor/{email}` - Get instructor's courses
- `GET /api/courses/count` - Get total course count
- `GET /api/courses/exists/{id}` - Check if course exists

**Protected (INSTRUCTOR/ADMIN only):**

- `POST /api/courses/create` - Create new course
- `PUT /api/courses/update/{id}` - Update course (owner only)
- `DELETE /api/courses/delete/{id}` - Delete course (owner only)
- `PATCH /api/courses/toggle-publish/{id}` - Toggle publish status (owner only)

---
