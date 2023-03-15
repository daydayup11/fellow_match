FROM maven:3.5-jdk-8-alpine as builder

# Copy local code to the container image.
WORKDIR /app
COPY ../../../../code/user_centor/user_centor/pom.xml .
COPY ../../../../code/user_centor/user_centor/src ./src

# Build a release artifact.
RUN mvn package -DskipTests

# Run the web service on container startup.
CMD ["java","-jar","/app/target/user_centor-0.0.1-SNAPSHOT.jar","--spring.profiles.active=prod"]