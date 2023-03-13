#

FROM ubuntu:java

COPY ./target/CrossWord-1.0-SNAPSHOT.jar .

COPY ./sampledata.trace .

COPY ./sampledata.mv .

EXPOSE 80

ENTRYPOINT java -jar CrossWord-1.0-SNAPSHOT.jar

