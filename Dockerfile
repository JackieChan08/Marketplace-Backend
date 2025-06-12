FROM openjdk:17-jdk-alpine
COPY a.jar .
ENTRYPOINT ["java", "-Xms1024m", "-Xmx1500m", "-jar", "a.jar"]
