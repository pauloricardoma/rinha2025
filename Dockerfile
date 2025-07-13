FROM openjdk:21

WORKDIR /app

COPY ./build/libs/rinha2025-all.jar /app/rinha2025-all.jar

CMD ["java", "-jar", "rinha2025-all.jar"]