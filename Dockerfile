FROM openjdk:17
LABEL authors="Timmy <https://github.com/yaiiow159>"
MAINTAINER Timmy
# Environment
ENV MYSQL_DATABASE blog_app
ENV MYSQL_ROOT_PASSWORD root
ENV REDIS_PORT 6379
ENV REDIS_HOST localhost
ENV REDIS_PASSWORD null
ENV REDIS_DB 0
# Workdir
WORKDIR /app

# Copy JAR file
COPY /target/Blog_app-0.0.1-SNAPSHOT.jar /app/Blog_app-0.0.1-SNAPSHOT.jar

# Expose port
EXPOSE 9091

# Command to run the application
CMD ["java", "-jar", "/app/Blog_app-0.0.1-SNAPSHOT.jar"]
