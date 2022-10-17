FROM openjdk:11
EXPOSE 8080
WORKDIR /applications
COPY target/exercise-0.0.1-SNAPSHOT.jar /applications/exercise.jar
ENTRYPOINT ["java","-jar", "exercise.jar"]