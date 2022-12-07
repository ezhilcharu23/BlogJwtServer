FROM openjdk:11
ADD target/server4.jar server4.jar
EXPOSE 8085
ENTRYPOINT ["java","-jar","server4.jar"]