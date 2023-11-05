FROM adoptopenjdk/openjdk11

ENV WORKDIR /app
WORKDIR ${WORKDIR}

ARG JAR_FILE
ADD target/${JAR_FILE} ${WORKDIR}/lib/doc-management-java-api.jar

ENTRYPOINT ["java","-jar","/app/lib/doc-management-java-api.jar"]
