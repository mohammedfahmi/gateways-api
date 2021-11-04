FROM openjdk:8-jdk-alpine
RUN apk --no-cache add curl
ARG JAR_FILE=target/*.jar
ARG CONFIG_FILE=src/main/resources/application.yaml

COPY ${JAR_FILE} app.jar
COPY ${CONFIG_FILE} ./config/application.yaml
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar /app.jar" ]