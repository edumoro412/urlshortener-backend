# Etapa 1: Build
# Usamos una imagen con Maven y JDK para compilar el proyecto
FROM maven:3.9.9-eclipse-temurin-21 AS build

# Establecemos el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiamos los archivos de definición (pom.xml) y el wrapper de Maven primero
# Esto permite aprovechar la cache de Docker cuando el código fuente cambia pero las dependencias no.
COPY pom.xml ./
COPY mvnw mvnw.cmd ./
COPY .mvn/ .mvn/

# Descargamos dependencias sin compilar el código (mejora los tiempos de build por cache)
RUN mvn -q -B -e -DskipTests dependency:go-offline

# Copiamos el resto del código fuente
COPY src/ src/

# Compilamos y generamos el JAR (saltamos tests para acelerar en contenedor; puedes quitar -DskipTests si lo prefieres)
RUN mvn -q -B -DskipTests package

# Etapa 2: Runtime
# Usamos una imagen ligera solo con JRE para ejecutar el JAR resultante
FROM eclipse-temurin:21-jre-alpine AS runtime

# Variables configurables
# SPRING_PROFILES_ACTIVE: permite elegir el perfil (dev, prod, etc.)
ENV SPRING_PROFILES_ACTIVE=prod \
    JAVA_OPTS=""

# Directorio de trabajo en el contenedor
WORKDIR /app

# Copiamos el JAR construido desde la etapa de build
# Buscamos el artefacto en la carpeta target del módulo raíz (urlshortener/target)
# Nota: el nombre del artefacto se extrae copiando cualquier .jar del target. Si deseas ser explícito,
# cambia el patrón por el nombre exacto del JAR generado por tu pom.xml
COPY --from=build /app/target/*.jar app.jar

# Exponemos el puerto por defecto de Spring Boot (configúralo si usas otro)
EXPOSE 8080

# Comando de ejecución: pasamos JAVA_OPTS para opciones de memoria, GC, etc.
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
