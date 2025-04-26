# Bank Application

## Project Overview
A full-stack banking application built with Spring Boot (backend) and Angular (frontend) that provides essential banking functionalities including account management, fund transfers, transaction history, and notifications.

## Features

- User authentication and authorization
- Account management
- Fund transfers between accounts
- Transaction history
- Notifications system
- API documentation with Swagger

## Tech Stack

### Backend
- Java 17
- Spring Boot 3.1.2
- Spring Security
- Spring Data JPA
- PostgreSQL database
- Maven

### Frontend
- Angular 14
- TypeScript
- Angular Material
- NgRx for state management

## Running with Docker

The application is fully dockerized with separate containers for the frontend, backend, and database.

### Prerequisites
- Docker and Docker Compose installed

### Production Mode
```bash
# Start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop all services
docker-compose down
```

### Development Mode
```bash
# Start all services in development mode
docker-compose -f docker-compose.dev.yml up -d

# View logs
docker-compose -f docker-compose.dev.yml logs -f

# Stop all services
docker-compose -f docker-compose.dev.yml down
```

### Accessing the Application
- Frontend: http://localhost:80 (Production) or http://localhost:4200 (Development)
- Backend API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html

## Local Development

### Backend Setup
1. Install JDK 17
2. Install PostgreSQL and create a database named `bankApp`
3. Update `src/main/resources/application.properties` if needed
4. Run the application:
```bash
./mvnw spring-boot:run
```

### Frontend Setup
1. Install Node.js and npm
2. Navigate to the frontend directory:
```bash
cd bank-app-frontend
npm install
npm start
```

## Troubleshooting

### Database Issues
- If you encounter database schema issues, try resetting the volumes:
```bash
docker-compose down -v
docker-compose up -d
```

### Notification Service
- The notification system requires proper configuration of message and content fields
- Check database compatibility if notifications fail

### Authentication Issues
- Default admin user: admin/admin123
- New user accounts are created with regular user privileges
- JWT tokens expire after 24 hours

## Contributing
Please read the CONTRIBUTING.md file for details on our code of conduct and the process for submitting pull requests.

## License
This project is licensed under the MIT License - see the LICENSE.md file for details.

## Application Structure

### Backend Structure
```
src/main/java/com/restApi/bankApp/
├── business/               # Business logic layer
│   ├── abstracts/          # Service interfaces
│   └── concretes/          # Service implementations
├── config/                 # Application configuration
├── controllers/            # REST API endpoints
├── dataAccess/             # Data repositories
├── entities/               # Domain models/entities
├── enums/                  # Enumeration types
└── BankAppApplication.java # Application entry point
```

### Frontend Structure
```
bank-app-frontend/
├── src/
│   ├── app/
│   │   ├── core/           # Core services and guards
│   │   ├── features/       # Feature components
│   │   ├── shared/         # Shared components and models
│   │   └── app.module.ts   # Main module
│   ├── assets/             # Static assets
│   └── environments/       # Environment configurations
├── angular.json            # Angular configuration
└── package.json            # Dependencies
```

## API Endpoints

### Authentication
- POST `/api/auth/register` - Register new user
- POST `/api/auth/login` - User login
- GET `/api/auth/user/{username}` - Get user details

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
- PUT `/api/notifications/{id}/read` - Mark notification as read
- PUT `/api/notifications/read-all` - Mark all notifications as read
- DELETE `/api/notifications/{id}` - Delete notification

## Setup and Installation

### Prerequisites
- Java 17 or higher
- Node.js and npm
- PostgreSQL
- Maven

### Backend Setup
1. Clone the repository:
```bash
git clone https://github.com/ozgucdlg/bankApp.git
cd bankApp
```

2. Configure PostgreSQL:
- Create database named 'bankApp'
- Update `application.properties` with your database credentials

3. Build and run the backend:
```bash
mvn clean install
mvn spring-boot:run
```

### Frontend Setup
1. Navigate to the frontend directory:
```bash
cd bank-app-frontend
```

2. Install dependencies:
```bash
npm install
```

3. Run the development server:
```bash
ng serve
```

4. Open your browser and navigate to:
```
http://localhost:4200/
```

## Security Implementation
- Authentication with Spring Security
- Password encryption using BCrypt
- Role-based authorization
- Secure HTTP-only cookies
- CORS configuration

## Contributing
1. Fork the repository
2. Create feature branch
3. Commit changes
4. Push to branch
5. Create Pull Request





