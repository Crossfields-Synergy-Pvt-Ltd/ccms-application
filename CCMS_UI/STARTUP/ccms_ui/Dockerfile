FROM maven:3-eclipse-temurin-8 AS builder

WORKDIR /build
COPY pom.xml .
COPY src src/
COPY conf/spring-config-docker.xml src/main/webapp/WEB-INF/spring-config.xml

RUN mvn clean package -DskipTests

FROM tomcat:7-jre8

COPY --from=builder /build/target/CCMS.war /usr/local/tomcat/webapps/CCMS.war

# Entrypoint that forwards .env-derived environment variables to Tomcat as
# -D system properties so Spring XML ${...} placeholders resolve.
COPY docker-entrypoint.sh /usr/local/bin/docker-entrypoint.sh
RUN chmod +x /usr/local/bin/docker-entrypoint.sh

# Create data directory for historical CSV files (survives without bind mount)
RUN mkdir -p /home/data/dontdelete

EXPOSE 8080

ENTRYPOINT ["docker-entrypoint.sh"]
CMD ["catalina.sh", "run"]
