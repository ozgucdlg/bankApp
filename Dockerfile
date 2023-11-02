FROM openjdk:17
EXPOSE 8080
ADD target/spring-boot-bankapp-docker.jar spring-boot-bankapp-docker.jar
ENTRYPOINT ["java","-jar","/spring-boot-bankapp-docker.jar"]