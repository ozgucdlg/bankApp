# Rest API Bank application 


## Introduction
Created a REST API for a simple Banking application using Spring Boot, 
Spring Data JPA (Hibernate) and PostgreSQL database.
A simple version of Banking App with basic functionalities: 
- Creating a bank account, 
- Fetching account details, 
- Deposit/withdrawal.

## Technologies
- Java 17,
- Maven,
- Springboot,
- Spring MVC,
- Angular,
- TypeScript,
- Linux,
- Docker
- Dependencies: 
  - Spring Web, 
  - Spring Data JPA, 
  - PostgreSQL Driver, 
  - Lombok

## Architecture

![Architecture](https://github.com/ozgucdlg/bankApp/blob/master/schema.png)

## Scripts
- Build the project:
    ```
    mvn clean install
    ```

- To start the application in the terminal:
    ```
    mvn spring-boot:run
    ```
- To dockerize the application:
  ```
  docker image ls
  ```


## Test
Completed API testing with __Postman and Swager__ each scenario was monitored. Tested CRUD API with endpoints.

## Deployment
The application was deployed on GitHub applying each step of sprints. There is only one branch which is __master__.



