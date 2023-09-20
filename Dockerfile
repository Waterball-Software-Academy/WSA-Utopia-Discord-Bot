# Part 1: Build the app using Maven
FROM maven:3.8.3-openjdk-17-slim

ADD ./ /
## build after dependencies are down so it wont redownload unless the POM changes
RUN mvn package

# Part 2: use the JAR file used in the first part and copy it across ready to RUN
FROM openjdk:17-jdk-slim
WORKDIR /root/
## COPY packaged JAR file and rename as app.jar
## → this relies on your MAVEN package command building a jar
## that matches *-jar-with-dependencies.jar with a single match
COPY --from=0 /main/target/*-jar-with-dependencies.jar app.jar
ENTRYPOINT ["java","-jar","./app.jar"]
