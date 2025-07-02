# Etapa 1: Construcción con Maven ya incluido
FROM maven:3.9.6-eclipse-temurin-21 AS buildstage

WORKDIR /app

# Copiar archivos necesarios
COPY pom.xml .
COPY src /app/src
COPY wallet /app/wallet

# Configurar el wallet para Oracle
ENV TNS_ADMIN=/app/wallet

# Compilar la aplicación sin ejecutar los tests
RUN mvn clean package -DskipTests

# Etapa 2: Imagen de ejecución más liviana
FROM eclipse-temurin:21-jdk

WORKDIR /app

# Copiar el jar compilado desde la etapa anterior
COPY --from=buildstage /app/target/*.jar /app/app.jar

# Copiar el wallet de Oracle
COPY wallet /app/wallet

ENV TNS_ADMIN=/app/wallet

# Exponer el puerto en que tu app escucha
EXPOSE 8082

# Ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

# docker build -t disp_cita_atencion .
# docker run -p 8082:8082 disp_cita_atencion