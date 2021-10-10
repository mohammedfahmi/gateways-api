FROM openjdk:10-jdk

WORKDIR /opt/application/

COPY config ./config/
COPY /maven/application.jar ./application.jar

CMD java -jar $JAVA_OPTS application.jar