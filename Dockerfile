# Usa una imagen base con el JDK de Java 17
FROM openjdk:17-jdk-slim

# Define el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copia los archivos del proyecto Maven
COPY pom.xml .
COPY src ./src

# Construye la aplicación con Maven
RUN mvn clean package -DskipTests

# Expone el puerto en el que la aplicación se ejecutará
EXPOSE 8080

# Define el comando para iniciar la aplicación
CMD ["java", "-jar", "target/citas-api-0.0.1-SNAPSHOT.jar"]