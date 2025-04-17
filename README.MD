# Bank Application - REST API

## Project Overview
A RESTful banking application built with Spring Boot that provides essential banking functionalities including account management, transactions, and user authentication. This backend service offers secure endpoints for banking operations with proper authentication and authorization.

## Technologies
- Java 17
- Spring Boot
- Spring Security
- Spring Data JPA
- PostgreSQL
- Maven
- Docker
- Lombok
- JUnit (Testing)

## Features
- User Authentication & Authorization
  - User registration and login
  - Secure password encryption
  - Role-based access control

- Account Management
  - Create new bank accounts
  - View account details
  - Update account information
  - Delete accounts (admin only)

- Transaction Operations
  - Deposit funds
  - Withdraw funds
  - Transfer between accounts
  - Transaction history

- Notification System
  - Email notifications for transactions
  - Account activity alerts
  - Welcome messages for new users

## API Endpoints

### Authentication
- POST `/api/auth/register` - Register new user
- POST `/api/auth/login` - User login
- GET `/api/auth/user/{username}` - Get user details
- PUT `/api/auth/{id}` - Update user
- DELETE `/api/auth/{id}` - Delete user

### Accounts
- GET `/api/accounts/{id}` - Get account details
- POST `/api/accounts/{id}/deposit` - Make deposit
- POST `/api/accounts/{id}/withdraw` - Make withdrawal

### Transactions
- POST `/api/transactions/transfer` - Transfer money
- GET `/api/transactions/account/{accountId}` - Get account transactions
- GET `/api/transactions/{transactionId}` - Get transaction details

### Notifications
- GET `/api/notifications` - Get all notifications (admin)
- GET `/api/notifications/recipient/{recipient}` - Get user notifications

## Database Schema
- Auth (User Authentication)
- Account (Bank Accounts)
- Transaction (Money Transfers)
- Notification (System Notifications)

## Setup and Installation

### Prerequisites
- Java 17 or higher
- Maven
- PostgreSQL
- Docker (optional)

### Local Setup
1. Clone the repository:
```bash
git clone [repository-url]
```

2. Configure PostgreSQL:
- Create database named 'bankApp'
- Update `application.properties` with your database credentials

3. Build the project:
```bash
mvn clean install
```

4. Run the application:
```bash
mvn spring-boot:run
```

### Docker Setup
1. Build Docker image:
```bash
docker build -t bank-app-backend .
```

2. Run container:
```bash
docker run -p 8080:8080 bank-app-backend
```

## Testing
- Comprehensive unit tests
- Integration tests for API endpoints
- Postman collection for API testing
- Swagger UI for API documentation and testing

## Security Implementation
- Basic Authentication
- Password encryption using BCrypt
- Role-based authorization
- Secure endpoints with Spring Security
- CORS configuration for API access

## Future Enhancements
- JWT Authentication
- Additional transaction types
- Enhanced notification system
- Advanced reporting features
- API rate limiting

## Contributing
1. Fork the repository
2. Create feature branch
3. Commit changes
4. Push to branch
5. Create Pull Request





