FROM openjdk:17.0.2-jdk
VOLUME /tmp
EXPOSE 8081
ADD /target/Medical-TEC-Rest-0.0.1-SNAPSHOT.jar rest.jar
ENTRYPOINT ["java","-jar","rest.jar"]
