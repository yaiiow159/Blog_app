FROM openjdk:17
LABEL authors="Timmy <https://github.com/yaiiow159>"
MAINTAINER Timmy
ARG JAR_FILE=target/Blog_app-0.0.1-SNAPSHOT.jar
ARG ACTIVE_PROFILE=dev
# Environment
ENV TZ=Asia/Taipei JAVA_OPTS="-Xms256m -Xmx256m"
# Expose port
EXPOSE 9090
# Workdir
RUN mkdir -p /usr/app
WORKDIR /usr/app


ADD ${JAR_FILE} /usr/app/blog_app.jar

#
CMD java ${JAVA_OPTS} -Dspring.profiles.active=${ACTIVE_PROFILE} -jar /usr/app/blog_app.jar