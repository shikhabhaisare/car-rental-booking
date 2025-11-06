# Car Rental Booking Service

## Project Overview

The **Car Rental Booking Service** is a Spring Boot application that allows customers to:

- Confirm a car rental booking
- Retrieve booking details
- Validate driving license information via a stubbed external API
- Fetch per-day rental rates based on car category via a stubbed pricing API

The service ensures proper validation, logging, and exception handling and is designed with clean architecture principles using DTOs, service layer, mappers, and validators.

---

## Tech Stack

- **Backend:** Java 17, Spring Boot 3.x
- **Database:** H2 (in-memory) or can be replaced with MySQL/PostgreSQL
- **Persistence:** Spring Data JPA
- **Validation:** Jakarta Validation API + custom validators
- **Mapping:** MapStruct
- **Logging:** SLF4J + Lombok `@Slf4j`
- **Testing:** JUnit 5, Mockito
- **Build Tool:** Maven
- **Security:** Spring Security (Basic Authentication enabled)

---

## Project Structure

```
com.xyz.carrental.booking
├── client          # External API clients (Driving License, Car Pricing)
├── controller      # REST controllers
├── dto             # Data transfer objects
├── entity          # JPA entities
├── exception       # Custom exceptions and global handler
├── mapper          # MapStruct mappers
├── repository      # Spring Data JPA repositories
├── service         # Business logic layer
├── stub            # Stub controllers for external APIs
└── validation      # Custom validation logic
```

---

## Features

### Booking Operations

1. **Confirm Booking**
    - Endpoint: `POST /api/v1/bookings`
    - Validates:
        - Customer age ≥ 18
        - Reservation duration ≤ 30 days
        - Customer has a valid driving license (≥ 1 year old)
    - Calculates total rental price based on car segment and number of days.

2. **Retrieve Booking Details**
    - Endpoint: `GET /api/v1/bookings/{bookingId}`
    - Returns:
        - Booking ID
        - Customer details
        - Rental dates
        - Car segment
        - Rental price

---

### External API Stubs

1. **Driving License API Stub**
    - Endpoint: `POST /stub/driving/license/details`
    - Returns license details based on license number.
    - License must be at least 1 year old (calculated using expiry date - 10 years).

2. **Pricing API Stub**
    - Endpoint: `POST /stub/pricing/rental/rate`
    - Returns per-day rental rate for car category: SMALL, MEDIUM, LARGE, EXTRA_LARGE

---

### Logging & Exception Handling

- Logging with `SLF4J` at controller and service layer
- Sensitive data (like driving license number) masked in logs
- Custom `BookingException` and `BookingExceptionHandler` for standardized error responses

---

## Security

- **Basic Authentication** enabled using Spring Security
- Credentials are currently hardcoded in memory for demo purposes:
    - `admin / pass***` → role ADMIN
    - `user / user***` → role USER
- **Future improvements:**
    - Store credentials securely in a Key Vault (Azure Key Vault, AWS Secrets Manager, or HashiCorp Vault)
    - Implement JWT or OAuth2 for stateless authentication
    - Support role-based fine-grained access

---

## How to Run

1. Clone the repository:

```bash
git clone https://github.com/shikhabhaisare/car-rental-booking.git
cd car-rental-booking
```

2. Build the project:

```bash
mvn clean install
```

3. Run the Spring Boot application:

```bash
mvn spring-boot:run
```

4. Test API endpoints using Postman or curl:

- Confirm Booking:

```bash
POST http://localhost:8080/api/v1/bookings
Body:
{
    "drivingLicenseNumber": "DL123456789",
    "age": 30,
    "startDate": "2025-12-01",
    "endDate": "2025-12-05",
    "carSegment": "MEDIUM"
}
```

- Retrieve Booking:

```bash
GET http://localhost:8080/api/v1/bookings/{bookingId}
```

---

## Postman Collection

A ready-to-use Postman collection is included:

📄 **`car-rental.postman_collection.json`**

It includes:
- Endpoints for confirming bookings 
- Retrieving booking details  
- Driving license stub API
- Car pricing stub API 

To use:
1. Open Postman.
2. Click **Import → File**.
3. Select `car-rental.postman_collection.json`.

---

## Unit Testing

- Uses **JUnit 5** and **Mockito**.
- Validates service layer logic, including license validation, pricing, and booking creation.
- Logs debug info during tests.
- Can be executed via:

```bash
mvn test
```

---

## Scope for Improvement / Production Ready Enhancements

1. **Security Enhancements**
    - Implement JWT/OAuth2 authentication and external user management
    - Move credentials to a secure Key Vault
    - Support fine-grained role-based access control

2. **Database Improvements**
    - Multi-table schema for Customers, Cars, and Pricing
    - Use a production-grade DB (PostgreSQL/MySQL)
    - Indexing, proper transactions, and isolation levels

3. **Resilience & Observability**
    - Implement **Circuit Breaker** (Resilience4j / Spring Cloud)
    - Retry and fallback strategies for external API failures
    - Distributed tracing with Sleuth/Zipkin
    - Centralized logging using ELK stack or similar

4. **Caching**
    - Cache pricing data and license verification responses using Redis for performance

5. **API Improvements**
    - API versioning
    - Standardized error codes and messages

6. **Testing Enhancements**
    - Integration and load testing
    - MockMvc or Testcontainers for end-to-end testing

7. **Deployment & Microservices**
    - Split services into microservices (Booking, Pricing, Driving License)
    - Containerize with Docker
    - CI/CD pipelines
    - Kubernetes orchestration

8. **Advanced Features**
    - Advanced pricing rules, discounts, and promotions
    - Support for multiple car segments and dynamic pricing

---

## Service Flow Diagram

```text
+----------------------+        +-----------------------+        +----------------+
|  BookingController   |        |  BookingServiceImpl   |        | External APIs  |
+----------------------+        +-----------------------+        +----------------+
          |                                |                            |
          | POST /api/v1/bookings          |                            |
          |-------------------------------->|                            |
          |                                | validate license           |
          |                                |--------------------------->| Driving License API
          |                                |                            |  (stub)
          |                                |                            |<--------------------------
          |                                | get rate for car segment  |
          |                                |--------------------------->| Car Pricing API (stub)
          |                                |                            |  returns rate
          |                                | calculate total price     |
          |                                | save booking              |
          |<-------------------------------|                            |
          | return bookingId               |                            |
```

---

## Conclusion

This project demonstrates a clean, layered architecture for a car rental booking system with:

- Validation
- Mapping
- Logging
- Stubbed external APIs
- Basic Security (Spring Security)

It is fully testable and provides a solid foundation to evolve into a **scalable, secure, production-ready microservice system**.

