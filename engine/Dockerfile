FROM groovy:3.0.5-jdk14
USER root

WORKDIR /

RUN mkdir engine

COPY engine-base/target/ /engine/

ADD https://github.com/ufoscout/docker-compose-wait/releases/download/2.5.0/wait /wait

RUN chmod +x /wait

CMD /wait && java --enable-preview -jar /engine/engine-base-1.0-SNAPSHOT.jar -Dwzpath=wz