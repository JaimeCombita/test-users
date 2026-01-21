# Use a lightweight Temurin JRE for runtime
# The build pipeline (CI) already runs `mvn package` so the JAR will be available in target/
# You can override the JAR path at build-time with --build-arg JAR_FILE=target/your-name.jar
FROM eclipse-temurin:21-jre as runtime

WORKDIR /app

# Copy the packaged jar (expects it to exist in target/)
# Use a wildcard to match the actual artifact produced by Maven (artifactId-version.jar)
COPY target/*.jar app.jar

# Create a non-root user and switch to it for better security
RUN addgroup --system app && adduser --system --ingroup app app || true
USER app

EXPOSE 8080

# Default JVM options
ENV JAVA_OPTS="-Xms256m -Xmx512m"
# Default Spring profile (can be overridden at runtime)
ENV SPRING_PROFILES_ACTIVE="dev"

# Ensure the app binds to the port provided by the environment (Cloud Run sets PORT)
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE} -Dserver.port=${PORT:-8080} -jar /app/app.jar"]
