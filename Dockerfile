FROM openjdk:17
LABEL authors="Timmy <https://github.com/yaiiow159>"
MAINTAINER Timmy
ARG JAR_FILE=target/Blog_app-0.0.1-SNAPSHOT.jar
# Environment
ENV TZ=Asia/Taipei
ENV JAVA_OPTS="-Xms256m -Xmx256m -Dfile.encoding=UTF-8"
ENV SPRING_PROFILES_ACTIVE=dev

# Expose port
EXPOSE 9090

# Workdir
RUN mkdir -p /app
WORKDIR /app

ADD ${JAR_FILE} /app/blog_app.jar
CMD java ${JAVA_OPTS} -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE} -Djava.security.egd=file:/dev/./urandom -jar /app/blog_app.jar