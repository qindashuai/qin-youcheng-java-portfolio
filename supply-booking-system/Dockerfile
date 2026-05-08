FROM openjdk:11-jdk-slim

VOLUME /tmp

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} app.jar

ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC"
ENV SPRING_PROFILES_ACTIVE=prod

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE} -jar /app.jar"]
