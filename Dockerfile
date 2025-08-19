FROM openjdk:17-jdk-alpine
COPY baistoreapi.jar .
ENTRYPOINT ["java", "-Xms1024m", "-Xmx1500m", "-jar", "baistoreapi.jar"]
