# Compilar o backend
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY demoApp/pom.xml ./pom.xml
COPY demoApp/src ./src

RUN mvn -B clean package -DskipTests

# Executar o backend
FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/target/demoApp-0.0.1-SNAPSHOT.jar ./app.jar

ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=70.0"

CMD ["java", "-jar", "app.jar"]
