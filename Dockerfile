FROM eclipse-temurin:17-jdk-alpine

RUN addgroup -g 1500 appgroup && \
    adduser -D -u 1500 -G appgroup appuser

WORKDIR /app

COPY build/libs/app.jar app.jar

RUN chown -R appuser:appgroup /app

USER appuser

ENTRYPOINT ["java", "-jar", "/app/app.jar"]