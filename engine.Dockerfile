FROM maven:3.6.3-openjdk-14-slim AS enginebuild

COPY settings.xml /usr/share/maven/conf/

COPY script/pom.xml script/pom.xml

COPY engine/pom.xml engine/pom.xml
COPY engine/engine-base/pom.xml engine/engine-base/pom.xml
COPY engine/engine-database/pom.xml engine/engine-database/pom.xml
COPY engine/engine-model/pom.xml engine/engine-model/pom.xml
COPY engine/engine-common/pom.xml engine/engine-common/pom.xml
COPY engine/engine-api/pom.xml engine/engine-api/pom.xml

COPY pom.xml pom.xml

RUN mvn dependency:go-offline package -B

## copy the pom and src code to the container
COPY engine/engine-base/src engine/engine-base/src
COPY engine/engine-database/src engine/engine-database/src
COPY engine/engine-model/src engine/engine-model/src
COPY engine/engine-common/src engine/engine-common/src
COPY engine/engine-api/src engine/engine-api/src

# package our application code
RUN mvn install -DskipTests

FROM groovy:3.0.5-jdk14
USER root

WORKDIR /

RUN mkdir engine

# copy only the artifacts we need from the first stage and discard the rest
COPY --from=enginebuild /engine/engine-base/target/ /engine/

ADD https://github.com/ufoscout/docker-compose-wait/releases/download/2.5.0/wait /wait

ENV JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005

RUN chmod +x /wait

CMD /wait && java --enable-preview -jar engine/engine-base-1.0-SNAPSHOT-jar-with-dependencies.jar -Dwzpath=wz -Xdebug