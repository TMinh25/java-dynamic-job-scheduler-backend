#
# Build stage
#
FROM public.ecr.aws/docker/library/maven:3.8.1-openjdk-17-slim AS build

COPY job-grpc/src /home/app/job-grpc/src
COPY job-grpc/settings.xml /home/app/job-grpc
COPY job-grpc/pom.xml /home/app/job-grpc
RUN mvn -f /home/app/job-grpc/pom.xml clean package install -s /home/app/job-grpc/settings.xml -DskipTests
RUN mvn -f /home/app/job-grpc/pom.xml deploy -s /home/app/job-grpc/settings.xml

COPY src /home/app/src
COPY settings.xml /home/app
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package install -s /home/app/settings.xml -DskipTests


#
# Package stage
#
FROM public.ecr.aws/docker/library/openjdk:17-jdk-oracle

EXPOSE 8181 9090
ARG JAR_FILE=/home/app/target/*.jar
COPY --from=build $JAR_FILE app.jar
ENTRYPOINT ["java","-jar","app.jar"]
