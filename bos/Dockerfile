FROM openjdk:14-ea-jdk-alpine
USER root

RUN mkdir bos

COPY bos-base/target/ /bos/

ADD https://github.com/ufoscout/docker-compose-wait/releases/download/2.5.0/wait /wait

RUN chmod +x /wait

CMD /wait && java --enable-preview -jar /bos/bos-base-1.0-SNAPSHOT.jar