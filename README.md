# E-Commerce Microservices System

A production-oriented E-Commerce Backend System built with Spring Boot Microservices, focusing on event-driven architecture, scalability, and business analytics.

## Tech Stack
- Java 21
- Spring Boot 3+
- Spring Cloud Gateway
- Spring Security (JWT)
- Apache Kafka
- Spring Data JPA (MySQL)
- Zipkin (Distributed Tracing)
- Redis
- FreeMarker (Email Template)

## System Architecture
<img width="1633" height="1357" alt="microservice drawio" src="https://github.com/user-attachments/assets/4b4009ae-20e6-4c79-80fb-cb7fcb776f06" />

## Core features:
### Authentication & Authorization
- JWT-based authentication
- Role-based access control (ADMIN / EMPLOYEE / USER)
- Centralized authorization at API Gateway

### Product Management:
- Product CRUD
- Redis Caching

### Order Management
- Place order
- Order lifecycle:
  -  PENDING
  -  PROCESSING
  -  DELIVERING
  -  COMPLETED
  -  CANCELLED
- Order return workflow:
  - REQUESTED
  - APPROVED
  - REJECTED
  - COMPLETED
### Event-Driven Architecture (Kafka)
| Event | Producer | Consumer |
| --- | --- | --- |
| order.completed | Order Service | Analytics Service |
| order.returned.analytics | Order Service | Analytics Service |
| order-notification-events | Order Service | Notification Service |
| order.returned.notification | Order Service | Notification Service |

### Analytics Service
- Daily revenue aggregation
- Cost, profit, net profit calculation
- Expense tracking
- Sales & Return transaction history
- Accurate financial metrics:
  - totalRevenue
  - totalCost
  - totalProfit
  - totalExpense
  - netProfit
  - totalItemsSold
  - totalItemsReturned

### Expense Management
- Fixed & variable expenses
- Expense CRUD
- Automatically affects daily net profit

### Order Return Handling
- Partial & multiple returns supported
- Prevents over-return quantity
- Refund calculation based on:
  - Quantity
  - Import price
  - Sell price
- Return data propagated to Analytics via Kafka

### Notification Service
- Email notifications for:
  - Order placed
  - Order status updated
  - Order return approved
- FreeMarker HTML templates
- Kafka-based async processing

## Project Structure
```
ecommerce-microservice/
├── api-gateway
├── auth-service
├── product-service
├── order-service
├── analytics-service
├── notification-service
└── common-lib (shared events & DTOs)
```

## Requirements
- Apache Kafka 3.9.1
- MySQL 8.0
- Spring Boot 3+
- Redis
- Zipkin
- Java 21

> ### Notes:
> To keep my system lightweight and easy to debug, all infrastructure components are installed locally on my laptop (Non-Docker).
> If you want to set up this project on your machine, please install and start the following services:
> - Apache Kafka: [Apache Kafka](https://kafka.apache.org/community/downloads/)
> - Redis: [Redis](https://redis.io/docs/latest/operate/oss_and_stack/install/archive/install-redis/)
> - Zipkin: [Zipkin](https://zipkin.io/pages/quickstart.html)
> - Java: [Java](https://www.oracle.com/java/technologies/downloads/)
> - MySQL: [MySQL](https://dev.mysql.com/downloads/installer/)
> #### Install via docker: Implement later

## Port
- Api Gateway: 8082
- Service Registry (Eureka Server): 8761
- Auth Service: 8083
- Product Service: 8084
- Order Service: 8085
- Notification Service: 8086
- Analytics Service: 8087
- Kafka: 9092
- Zipkin: 9411

## API Documentation
> All the request must be request via Api gateway.
> Example: ```http://localhost:8082/api/v1/auth/register```
### Auth Service
- POST /api/v1/auth/register
- POST /api/v1/auth/login
- POST /api/v1/auth/refresh
- POST /api/v1/auth/logout
- GET /api/v1/auth/verify

### Product Service
- Category
  - POST /api/v1/categories
  - PUT /api/v1/categories/{id}
  - GET /api/v1/categories/{id}
  - GET /api/v1/categories
  - PUT /api/v1/categories/{id}/toggle-status
  - ELETE /api/v1/categories/{id}
- Product
  - POST /api/v1/products
  - PUT /api/v1/products/{id}
  - GET /api/v1/products/{id}
  - GET /api/v1/products
  - PUT /api/v1/products/{id}/toggle-status
  - POST /api/v1/products/{id}/adjust-stock
  - GET /api/v1/products/category/{categoryId}
  - GET /api/v1/products/filter
  - DELETE /api/v1/products/{id}
  - GET /api/v1/products/internal/{id}

### Order Service
- POST /api/v1/orders/place
- GET /api/v1/orders/{id}
- GET /api/v1/orders/my-orders
- GET /api/v1/orders/admin
- PUT /api/v1/orders/{id}/status

### Analytics Service
