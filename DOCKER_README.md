# Docker Setup for Bank Application

This repository contains Docker configurations for running the Bank Application with three separate components:
1. Spring Boot Backend
2. Angular Frontend 
3. PostgreSQL Database

## Prerequisites

- Docker and Docker Compose installed on your machine
- Git to clone the repository

## Project Structure

```
bankApp/
├── Dockerfile                  # Backend Dockerfile
├── docker-compose.yml          # Production Docker Compose
├── docker-compose.dev.yml      # Development Docker Compose
├── database/                   # Database Docker files
│   ├── Dockerfile
│   ├── init.sql                # Initial schema
│   └── fix-database.sql        # Data fixes
├── bank-app-frontend/          # Frontend code
│   ├── Dockerfile
│   └── nginx.conf
└── src/                        # Backend code
```

## Quick Start

### For Production-like Environment

```bash
# Build and start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop all services
docker-compose down
```

### For Development Environment

```bash
# Build and start all services in development mode
docker-compose -f docker-compose.dev.yml up -d

# View logs
docker-compose -f docker-compose.dev.yml logs -f

# Stop all services
docker-compose -f docker-compose.dev.yml down
```

## Accessing the Application

- Frontend: http://localhost:80 (Production) or http://localhost:4200 (Development)
- Backend API: http://localhost:8080
- Database: localhost:5432 (accessible via PostgreSQL client)

## Development Workflow

### Backend Development
- Code changes in the `src/` directory will be automatically detected
- Remote debugging is available on port 5005

### Frontend Development
- Angular dev server with hot module replacement runs on port 4200
- Code changes in `bank-app-frontend/src` are automatically detected and applied

### Database
- Database data is persisted in Docker volumes
- To reset the database, delete the volume: `docker volume rm bankapp_postgres-data-dev`

## Customizing Configurations

### Environment Variables
You can modify environment variables in the docker-compose files:

- Database credentials
- Spring Boot properties
- Angular environment settings

### Database Initialization
- Add or modify SQL scripts in the `database/` directory
- Scripts are executed alphabetically when the container starts for the first time

## Troubleshooting

### Database Connection Issues
```bash
# Check if PostgreSQL is running
docker ps | grep bank-app-db

# View database logs
docker-compose logs db
```

### Backend Issues
```bash
# View backend logs
docker-compose logs backend
```

### Frontend Issues
```bash
# View frontend logs
docker-compose logs frontend
```

## Important Notes

1. For production deployment, update passwords and security settings
2. The frontend Nginx configuration proxies API requests to the backend
3. Development mode mounts source code volumes for live editing 