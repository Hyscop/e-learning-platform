# E-Learning Platform - Microservices Architecture

A multi-tenant e-learning platform built with microservices architecture using Spring Boot, designed for practising modern backend development practices.

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

## 📁 Project Structure

```
e-learning-platform/
├── api-gateway/           # API Gateway service
├── user-service/          # User management & authentication
├── course-service/        # Course management
├── enrollment-service/    # Enrollment management
├── docker-compose.yml     # Docker orchestration
└── pom.xml                # Parent POM
```

## 📊 Current Development Phase

**Phase 1**: Basic service setup with Docker and simple CRUD operations

- [x] Project structure initialization
- [x] User Service implementation
- [x] Basic Docker setup
- [x] Docker Compose configuration
