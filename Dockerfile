# Start from the official OpenJDK 17 base image
FROM openjdk:17-jdk-slim

FROM openjdk:17-jdk-slim

# update
RUN apt-get update && \
    apt-get upgrade -y && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*


# Make port 8080 available to the world outside this container
EXPOSE 8080

# Set the JAR file path
ARG JAR_FILE=target/*.jar

# Copy JAR file into the image
COPY ${JAR_FILE} app.jar

ENV JAVA_OPTS=""

# Execute the application
ENTRYPOINT ["java","-jar","/app.jar"]
