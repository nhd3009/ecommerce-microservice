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
- Analytics
  - GET /api/v1/analytics/revenue/daily
  - GET /api/v1/analytics/dashboard
  - GET /api/v1/analytics/top-products
- Expense
  - POST /api/v1/expenses
  - GET /api/v1/expenses
  - PUT /api/v1/expenses/{id}
  - DELETE /api/v1/expenses/{id}

> Note: You can import this via Insomnia
> [Insomnia_2026-01-30.har](../../Documents/Insomnia_2026-01-30.har)

## Environment variable
```
PRODUCT_DB_URL=your_product_db_url (mysql)
PRODUCT_DB_USERNAME=your_product_db_username
PRODUCT_DB_PASSWORD=your_product_db_password

AUTH_DB_URL=your_auth_db_url (mysql)
AUTH_DB_USERNAME=your_auth_db_username
AUTH_DB_PASSWORD=your_auth_db_password

ORDER_DB_URL=your_order_db_url (mysql)
ORDER_DB_USERNAME=your_order_db_username
ORDER_DB_PASSWORD=your_order_db_password

ANALYTICS_DB_URL=your_analytics_db_url (mysql)
ANALYTICS_DB_USERNAME=your_analytics_db_username
ANALYTICS_DB_PASSWORD=your_analytics_db_password

MAIL_HOST=your_mail_host
MAIL_USERNAME=your_mail_user_name
MAIL_PASSWORD=your_mail_password
MAIL_PORT=your_mail_port

ZIPKIN_URL= YOUR_ZIPKIN_URL(Default http://localhost:9411/api/v2/spans)

DEFAULT_EUREKA_URL=YOUR_EUREKA_URL(Default http://localhost:8761/eureka)

JWT_SECRET=your_jwt_secret
JWT_EXP=your_jwt_expire(6000000)
```

> Note: For mail service you can use Mailtrap and create a sandbox to use
