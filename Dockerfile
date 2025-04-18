# Start from an OpenJDK base image
FROM amazoncorretto:21-alpine-jdk

# Set the working directory
WORKDIR /app

# Copy the jar file (update `your-app.jar` to your actual jar name)
COPY target/spring-boot-Url-shortener-0.0.1-SNAPSHOT.jar app.jar

# Expose the port your app runs on (default is 8080)
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

#While building the Docker image for this application as we are running PostgreSQL in a Docker container,
#make sure to replace the spring.datasource.url in the application.properties file with the
#Docker container name of PostgreSQL.

