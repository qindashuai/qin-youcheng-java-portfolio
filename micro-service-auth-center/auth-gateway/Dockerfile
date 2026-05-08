FROM openjdk:11-jre-slim
VOLUME /tmp
ARG JAR_FILE=target/auth-gateway-1.0.0.jar
COPY ${JAR_FILE} app.jar
ENV JAVA_OPTS="-Xms256m -Xmx512m"
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app.jar"]
