FROM openjdk:17-jdk-alpine
VOLUME /main-app
ADD target/chess-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar","/app.jar"]