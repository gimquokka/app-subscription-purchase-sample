# alpine image는 netty error(C Libraray 종속성 이슈)가 발생해 미사용
# cf) https://martinheinz.dev/blog/92
FROM amazoncorretto:22

ARG JAR_FILE=/build/libs/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-XX:MinRAMPercentage=50.0","-XX:MaxRAMPercentage=80.0","-jar","/app.jar"]