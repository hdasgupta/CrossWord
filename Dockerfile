FROM ubuntu:java

COPY ./target/CrossWord-1.0-SNAPSHOT.jar /root/out.jar

EXPOSE 80

EXPOSE 8082

ENTRYPOINT java -jar /root/out.jar