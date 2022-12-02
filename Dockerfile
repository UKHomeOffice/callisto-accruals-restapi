FROM openjdk:17-alpine
RUN apk add --no-cache \
    curl=7.79.1-r3 \
    openssl=1.1.1s-r0 \
    aws-cli=1.19.93-r0
WORKDIR /usr/src/main
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} accruals-api.jar
ENTRYPOINT ["java","-jar","accruals-api.jar"]
EXPOSE 8080
