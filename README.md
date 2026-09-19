#  Online Railway Reservation System

A backend-focused **Online Railway Reservation System** built using **Spring Boot Microservices Architecture**.

The system provides railway operations such as user authentication, train and station management, direct and connecting train search, schedules and fares, seat availability, reservation, payment, refund, concessions, Tatkal booking, food ordering, and notifications.

---

##  Project Overview

The application is divided into multiple independent microservices, where each service handles a specific business responsibility.

The project demonstrates concepts such as:

* Microservices Architecture
* API Gateway
* Service Discovery
* Centralized Configuration
* JWT Authentication
* Service-to-Service Communication
* Load Balancing
* Circuit Breaker and Retry
* Saga-style Transaction Management
* Idempotency
* Event-Driven Architecture
* Seat Hold and Confirmation
* Leg-Based Seat Allocation
* Centralized Exception Handling
* REST API Documentation
* Automated Testing

---

#  Architecture


                        Client / Swagger
                              |
                              v
                         API Gateway
                              |
                +-------------+-------------+
                |                           |
          JWT Authentication          Eureka Discovery
                |                           |
                +-------------+-------------+
                              |
         +--------------------+--------------------+
         |                    |                    |
         v                    v                    v
    Auth Service        Train Service       Search Service
         |                    |                    |
         v                    v                    v
 Customer Service    Station-Route       Schedule-Fare
                              |                    |
                              +---------+----------+
                                        |
                                        v
                              Inventory-Quota
                                        |
                                        v
                              Reservation Service
                                /             \
                               v               v
                    Payment-Refund       Notification
                                           Service
```

Each microservice follows a layered architecture:


Controller
    ↓
Service
    ↓
Repository
    ↓
Spring Data JPA / Hibernate
    ↓
MySQL Database
```

---

#  Microservices

##  Auth Service

Responsible for:

* User registration
* User login
* JWT access token generation
* Refresh token generation
* Password encryption using BCrypt
* User authentication and authorization
* User status management

After successful authentication, the service provides:


Access Token
Refresh Token
User ID
Email
Role
```

Protected APIs use:

```http
Authorization: Bearer <JWT_ACCESS_TOKEN>
```

---

##  Customer Service

Responsible for:

* Customer profile management
* Customer information
* Concession management
* Concession verification

Supported concession categories include:


STUDENT
SENIOR_CITIZEN
GOVERNMENT_STAFF
DISABILITY
```

Concession information submitted by a customer is validated against an existing concession verification registry.

---

## 🚆 Train Service

Responsible for maintaining train master data.

Features include:

* Add train
* Update train
* Retrieve train
* Retrieve all trains
* Search using train number
* Manage train active status

Typical train information includes:

```
Train ID
Train Number
Train Name
Train Type
Active Status
```

---

##  Station-Route Service

Responsible for:

* Station master data
* Train routes
* Stop sequence
* Source and destination relationship
* Route distance

Example:

```
Train Route

Chennai
   ↓
Katpadi
   ↓
Jolarpettai
   ↓
Bengaluru
```

Search Service uses this information to identify whether a train passes through the requested source and destination.

---

## 🕒 Schedule-Fare Service

Responsible for:

* Train schedules
* Departure and arrival timings
* Fare calculation
* Tatkal configuration
* Concession-based fare calculation

Supported railway classes include:

```
1A
2A
3A
SL
CC
EC
2S
```

Tatkal configuration can contain:

```
Tatkal Enabled
Advance Days
AC Opening Time
Non-AC Opening Time
Surcharge Percentage
```

---

##  Inventory-Quota Service

Responsible for:

* Seat availability
* Coach configuration
* Quotas
* Seat selection
* Temporary seat holding
* Seat confirmation
* Seat release

Seat lifecycle:

```
AVAILABLE
    ↓
HELD
    ↓
BOOKED
```

If payment fails or the hold expires:

```
HELD
  ↓
RELEASED
  ↓
AVAILABLE
```

### Leg-Based Seat Allocation

The system supports non-overlapping seat allocation.

For example:

```
A → B → C → D
```

If Seat 10 is booked from:

```
B → C
```

the same seat may still be available for:

```
A → B
```

or:

```
C → D
```

because those journey legs do not overlap.

---

##  Search Service

Responsible for searching trains based on:

* Source
* Destination
* Journey date
* Class
* Quota

The Search Service communicates with:

```
Train Service
Station-Route Service
Schedule-Fare Service
Inventory-Quota Service
```

using **WebClient**.

### Direct Train Search

A train is considered direct when:

```
Source Station
       ↓
Same Train
       ↓
Destination Station
```

and the source station occurs before the destination in the route.

### Connecting Train Search

The system also supports connecting journeys.

Example:

```
Source
   ↓
Train 1
   ↓
Transfer Station
   ↓
Train 2
   ↓
Destination
```

The system checks:

* Correct route sequence
* Different trains
* Common transfer station
* Minimum connection time
* Seat availability
* Fare for both legs

The total fare is:

```
Leg 1 Fare + Leg 2 Fare
```

Overall availability is based on the minimum seat availability between both legs.

---

#  Reservation Service

Reservation Service coordinates the complete booking flow.

```
Booking Request
      ↓
Validate Passenger Details
      ↓
Calculate Fare
      ↓
Hold Seats
      ↓
Process Payment
      ↓
Generate PNR
      ↓
Save Reservation
      ↓
Confirm Seats
      ↓
Generate Ticket
      ↓
Send Notification
```

Features include:

* Reservation creation
* PNR generation
* Passenger management
* Seat selection
* Cancellation
* Ticket generation
* Booking history
* Food ordering

---

#  Saga-Style Reservation Flow

Since multiple microservices participate in one reservation, a normal database transaction cannot handle the entire operation.

The project therefore uses a **Saga-like orchestration approach**.

Successful flow:

```
Hold Seat
    ↓
Payment
    ↓
Create Reservation
    ↓
Confirm Seat
```

Failure example:

```
Hold Seat 
    ↓
Payment 
    ↓
Compensating Transaction
    ↓
Release Seat
```

This helps maintain data consistency between microservices.

---

#  Idempotency

Reservation Service uses idempotency to prevent duplicate bookings.

The client can send:

```http
Idempotency-Key: BOOKING-123
```

Before processing a reservation, the backend checks whether the key has already been processed.

```
New Key
   ↓
Process Reservation
   ↓
Store Response

Same Key Again
   ↓
Return Existing Response
```

This prevents duplicate reservation creation when the same request is submitted multiple times.

---

#  Payment-Refund Service

Responsible for:

* Payment processing
* Transaction reference generation
* Payment status
* Refund calculation
* Refund processing
* Cancellation charges

Reservation Service communicates with Payment-Refund Service during the booking Saga.

---

#  Food Ordering

Customers can order food for an existing reservation.

Features include:

* View menu
* Create food order
* Cancel food order
* Food-order notifications

---

#  Notification Service

Notification Service supports both event-driven and direct notifications.

Technologies used:

```
RabbitMQ
Spring AMQP
JavaMailSender
SMTP
Spring Data JPA
```

Automatic events include:

* Reservation confirmation
* Reservation cancellation
* Payment success
* Refund processed
* Food order confirmation

Flow:

```
Reservation / Payment Service
            ↓
          RabbitMQ
            ↓
     Notification Service
            ↓
      JavaMailSender
            ↓
         SMTP Server
            ↓
        User Email
```

The system also supports:

* Train-delay notification
* Custom notification

SMS is represented as a notification channel but requires an external SMS provider for real delivery.

---

#  API Gateway

Spring Cloud API Gateway acts as the **single entry point** for the microservices.

Responsibilities include:

* Request routing
* JWT validation
* Authentication filtering
* Service discovery integration
* Correlation ID handling

Example route:

```
lb://TRAIN-SERVICE
```

`lb://` indicates that Spring Cloud LoadBalancer should locate a registered service instance using Eureka instead of using a hard-coded host and port.

---

#  Eureka Service Discovery

The project uses **Netflix Eureka** as the service registry.

Each microservice registers itself using:

```properties
spring.application.name=SERVICE-NAME
```

Instead of:

```
http://localhost:8082
```

services can communicate using:

```
http://TRAIN-SERVICE
```

The flow is:

```
Microservice
    ↓
Registers with Eureka
    ↓
Eureka Service Registry
    ↓
LoadBalancer
    ↓
Available Service Instance
```

---

#  Config Server

Spring Cloud Config Server provides centralized configuration.

Configuration files are stored in a **Git repository**.

```
Git Configuration Repository
            ↓
       Config Server
            ↓
      Microservices
```

Benefits include:

* Centralized configuration
* Version control
* Easier environment management
* Reduced duplicate configuration

---

#  Service-to-Service Communication

The project primarily uses **WebClient** for synchronous HTTP communication.

Example:

```
Search Service
      ↓
WebClient
      ↓
LoadBalancer
      ↓
Eureka
      ↓
Train Service
```

A `@LoadBalanced WebClient.Builder` allows microservices to use application names instead of fixed ports.

---

# Resilience4j

Resilience4j is used for fault tolerance.

Implemented mechanisms include:

### Retry

Retries temporary failures.

```
Request Failed
     ↓
Retry
     ↓
Retry
     ↓
Success / Final Failure
```

### Circuit Breaker

Circuit-breaker states:

```
CLOSED
   ↓
Too many failures

OPEN
   ↓
Wait duration

HALF_OPEN
   ↓
Test requests

Success → CLOSED
Failure → OPEN
```

Fallback handling helps prevent cascading failures.

---

#  JWT Authentication

Authentication flow:

```
Login
  ↓
Validate Credentials
  ↓
Generate Access Token
  ↓
Generate Refresh Token
  ↓
Return Tokens
```

Protected request:

```
Client
   ↓
Authorization: Bearer <JWT>
   ↓
API Gateway
   ↓
Authentication Filter
   ↓
Validate JWT
   ↓
Forward Request
```

The Gateway can propagate authenticated user information using headers such as:

```
X-User-Id
X-User-Email
X-User-Roles
```

---

#  Exception Handling

Centralized exception handling is implemented using a Global Exception Handler.

Examples include:

```
ResourceNotFoundException → 404 NOT FOUND

BadRequestException → 400 BAD REQUEST

Validation Error → 400 BAD REQUEST

Unexpected Exception → 500 INTERNAL SERVER ERROR
```

This prevents repetitive try-catch handling inside individual controllers.

---

#  Common API Response

The project uses a generic response wrapper:

```java
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
}
```

Example:

```java
ApiResponse<TrainResponse>
```

or:

```java
ApiResponse<List<TrainResponse>>
```

This provides a consistent response structure across microservices.

---

#  Testing

The project uses both manual and automated testing.

### Swagger / OpenAPI

Used for:

* REST API documentation
* Sending API requests
* Testing request bodies
* Testing authentication
* Checking status codes and JSON responses

### JUnit

Used for automated backend testing.

Technologies include:

```
JUnit
@SpringBootTest
Mockito
@ActiveProfiles("test")
```

Test scenarios include:

* Authentication
* Train CRUD
* Reservation lifecycle
* Search functionality
* Connecting trains
* Fare calculation
* Seat hold / confirm / release
* Concurrent seat booking
* Payment and refund
* Notification functionality

---

#  Technology Stack

### Backend

* Java
* Spring Boot
* Spring MVC

### Microservices

* Spring Cloud
* Spring Cloud Gateway
* Netflix Eureka
* Spring Cloud Config
* Spring Cloud LoadBalancer

### Database

* MySQL
* Spring Data JPA
* Hibernate ORM

### Security

* JWT
* BCrypt
* Bearer Authentication

### Communication

* WebClient
* REST APIs
* RabbitMQ

### Fault Tolerance

* Resilience4j
* Retry
* Circuit Breaker
* Fallback

### Notification

* RabbitMQ
* Spring AMQP
* JavaMailSender
* SMTP

### Documentation & Testing

* Swagger
* OpenAPI
* JUnit
* Mockito
* Spring Boot Test

### Tools

* Maven
* Git
* GitHub
* Eclipse / Spring Tool Suite
* MySQL Workbench
* Postman / Swagger UI

---

# 🚀 Running the Project

## Prerequisites

Install:

```text
Java 17+
Maven
MySQL
RabbitMQ
Git
```

Ensure all required MySQL databases are created/configured.

---

## Recommended Startup Order

Start the infrastructure services first:

```
1. Eureka / Discovery Server
2. Config Server
```

Then start the business services:

```
3. Auth Service
4. Customer Service
5. Train Service
6. Station-Route Service
7. Schedule-Fare Service
8. Inventory-Quota Service
9. Search Service
10. Payment-Refund Service
11. Reservation Service
12. Notification Service
```

Finally:

```
13. API Gateway
```

Open the Eureka dashboard and verify that the expected microservices are registered.

---

# 📚 Main Booking Flow

```
Register / Login
       ↓
Search Train
       ↓
Select Direct / Connecting Train
       ↓
Check Schedule & Fare
       ↓
Check Seat Availability
       ↓
Create Reservation
       ↓
Temporarily Hold Seat
       ↓
Process Payment
       ↓
Generate PNR
       ↓
Confirm Seat
       ↓
Generate Ticket
       ↓
Send Notification
```

---

#  My Contribution

My main responsibility in this project was:

### Train Service

I worked with:

* Train entities
* REST controllers
* Service-layer logic
* Repository operations
* Train CRUD APIs
* Swagger testing
* Database verification

### Station-Route Service

I worked with:

* Station information
* Train routes
* Route stop sequences
* Source/destination relationship
* REST APIs
* Database operations
* Swagger testing

These services are consumed by the Search Service to determine direct and connecting train options.

---

#  Key Features

* Microservices-based railway backend
* Centralized API Gateway
* Eureka service discovery
* Git-backed centralized configuration
* JWT authentication
* Refresh-token mechanism
* Train and route management
* Direct and connecting train search
* Tatkal support
* Concession verification
* Fare calculation
* Temporary seat holding
* Leg-based seat allocation
* Saga-style booking workflow
* Idempotent reservation processing
* Payment and refund handling
* Food ordering
* RabbitMQ-based event-driven notifications
* Email notifications
* Train-delay notifications
* Resilience4j fault tolerance
* Global exception handling
* Swagger API documentation
* JUnit automated testing

---

#  Future Enhancements

Possible improvements include:

* Frontend application using React or Angular
* Dockerizing all microservices
* Kubernetes deployment
* Redis caching
* Kafka-based event streaming
* Real SMS gateway integration
* Production-grade distributed tracing
* Centralized logging using ELK
* Payment gateway integration
* RAC / Waiting List implementation
* Dynamic pricing
* Improved rate limiting and security
* Monitoring using Prometheus and Grafana

---

#  Project Type

Backend Microservices Group Project

Primary focus:

```
Spring Boot
Microservices
REST APIs
Database Integration
Distributed Systems
Backend Testing
```

---

## License

This project is developed for educational and training purposes.
