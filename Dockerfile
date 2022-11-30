FROM openjdk:17-alpine
RUN apk add --no-cache sudo
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} accruals-api.jar
ENTRYPOINT ["java","-jar","accruals-api.jar"]
EXPOSE 8080
