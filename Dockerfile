FROM openjdk:17
LABEL authors="Timmy <https://github.com/yaiiow159>"
MAINTAINER Timmy
ARG JAR_FILE=target/blog_app-*.jar
ARG ACTIVE_PROFILE=dev
# Environment
ENV TZ=Asia/Taipei
ENV JAVA_OPTS="-Xms128m -Xmx256m"
ENV APP_NAME=blog_app
# Expose port
EXPOSE 9090
# Workdir
WORKDIR /usr/app
RUN mkdir -p /usr/app

ADD target/${JAR_FILE} /usr/app/app.jar
#
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=$ACTIVE_PROFILE -jar /usr/app/app.jar" ]