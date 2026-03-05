# Use Java 21, 
FROM eclipse-temurin:21-jre-alpine

# Set the working directory inside the container
WORKDIR /src

# build the project 
COPY target/*.jar app.jar

# Run the application with restricted JVM memory settings to fit the 1GB limit
ENTRYPOINT ["java", "-Xms256m", "-Xmx512m", "-jar", "app.jar"]