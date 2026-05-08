FROM openjdk:11-jdk-slim

VOLUME /tmp
VOLUME /app/uploads

WORKDIR /app

COPY target/enterprise-rag-knowledge-1.0.0.jar app.jar

ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC"
ENV SPRING_PROFILES_ACTIVE=prod
ENV MYSQL_HOST=mysql
ENV MYSQL_PORT=3306
ENV MYSQL_USERNAME=root
ENV MYSQL_PASSWORD=root
ENV REDIS_HOST=redis
ENV REDIS_PORT=6379
ENV OLLAMA_BASE_URL=http://ollama:11434
ENV CHAT_MODEL=deepseek-r1:7b
ENV EMBEDDING_MODEL=nomic-embed-text

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE} -jar app.jar"]
