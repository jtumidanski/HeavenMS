FROM openjdk:14-ea-jdk-alpine
USER root

RUN mkdir bos

COPY cos-base/target/ /cos/

ADD https://github.com/ufoscout/docker-compose-wait/releases/download/2.5.0/wait /wait

RUN chmod +x /wait

CMD /wait && java --enable-preview -jar /cos/cos-base-1.0-SNAPSHOT.jar