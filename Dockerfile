# Use a lightweight Temurin JRE for runtime
# The build pipeline (CI) already runs `mvn package` so the JAR will be available in target/
# You can override the JAR path at build-time with --build-arg JAR_FILE=target/your-name.jar
ARG JAR_FILE=target/users-1.0.0.jar
FROM eclipse-temurin:21-jre as runtime

WORKDIR /app

# Copy the packaged jar (expects it to exist in target/)
COPY ${JAR_FILE} app.jar

# Create a non-root user and switch to it for better security
RUN addgroup --system app && adduser --system --ingroup app app || true
USER app

EXPOSE 8080

ENV JAVA_OPTS="-Xms256m -Xmx512m"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]

