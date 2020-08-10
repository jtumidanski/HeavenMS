FROM app-core:latest AS corebuild

FROM maven:3.6.3-openjdk-14-slim AS cosbuild

COPY --from=corebuild /root/.m2/repository/app-core/ /root/.m2/repository/app-core/

COPY parent/pom.xml parent/pom.xml
COPY parent/parent-base/pom.xml parent/parent-base/pom.xml
COPY parent/parent-database/pom.xml parent/parent-database/pom.xml
COPY parent/parent-model/pom.xml parent/parent-model/pom.xml

COPY shared/pom.xml shared/pom.xml
COPY shared/shared-rest/pom.xml shared/shared-rest/pom.xml
COPY shared/shared-utility/pom.xml shared/shared-utility/pom.xml

COPY bos/pom.xml bos/pom.xml
COPY bos/bos-base/pom.xml bos/bos-base/pom.xml
COPY bos/bos-database/pom.xml bos/bos-database/pom.xml
COPY bos/bos-model/pom.xml bos/bos-model/pom.xml

COPY cos/pom.xml cos/pom.xml
COPY cos/cos-base/pom.xml cos/cos-base/pom.xml
COPY cos/cos-database/pom.xml cos/cos-database/pom.xml
COPY cos/cos-model/pom.xml cos/cos-model/pom.xml

COPY script/pom.xml script/pom.xml

COPY engine/pom.xml engine/pom.xml
COPY engine/engine-base/pom.xml engine/engine-base/pom.xml
COPY engine/engine-database/pom.xml engine/engine-database/pom.xml
COPY engine/engine-model/pom.xml engine/engine-model/pom.xml
COPY engine/engine-common/pom.xml engine/engine-common/pom.xml

COPY pom.xml pom.xml

RUN mvn dependency:go-offline package -B

## copy the pom and src code to the container
COPY shared/shared-rest/src shared/shared-rest/src
COPY shared/shared-utility/src shared/shared-utility/src

COPY bos/bos-base/src bos/bos-base/src
COPY bos/bos-database/src bos/bos-database/src
COPY bos/bos-model/src bos/bos-model/src

COPY cos/cos-base/src cos/cos-base/src
COPY cos/cos-database/src cos/cos-database/src
COPY cos/cos-model/src cos/cos-model/src

COPY engine/engine-base/src engine/engine-base/src
COPY engine/engine-database/src engine/engine-database/src
COPY engine/engine-model/src engine/engine-model/src
COPY engine/engine-common/src engine/engine-common/src

FROM openjdk:14-ea-jdk-alpine
USER root

RUN mkdir cos

COPY --from=cosbuild /cos/cos-base/target/ /cos/

ADD https://github.com/ufoscout/docker-compose-wait/releases/download/2.5.0/wait /wait

RUN chmod +x /wait

CMD /wait && java --enable-preview -jar /cos/cos-base-1.0-SNAPSHOT.jar