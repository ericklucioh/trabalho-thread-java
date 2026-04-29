FROM eclipse-temurin:17-jdk AS build

WORKDIR /app

COPY scripts /app/scripts
COPY src /app/src
RUN chmod +x /app/scripts/*.sh && /app/scripts/package.sh

FROM eclipse-temurin:17-jdk

WORKDIR /app

RUN apt-get update \
    && apt-get install -y --no-install-recommends \
        fontconfig \
        libfreetype6 \
        libxext6 \
        libxi6 \
        libxrender1 \
        libxtst6 \
    && rm -rf /var/lib/apt/lists/*

COPY --from=build /app/target/trabalho-thread-java-1.0.0-SNAPSHOT.jar /app/app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
