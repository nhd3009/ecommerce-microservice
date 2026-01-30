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

## Requirement
- Apache Kafka 3.9.1
- MySQL 8.0
- Spring Boot 3+
- Redis
- Zipkin
- Java 21


