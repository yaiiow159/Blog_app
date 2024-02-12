FROM openjdk:17
LABEL authors="Timmy <https://github.com/yaiiow159>"
ARG JAR_FILE=target/blog_app-0.0.1.jar

# Environment
ENV TZ=Asia/Taipei
ENV JAVA_OPTS="-Xms128m -Xmx256m"
ENV APP_NAME=blog_app

# Workdir
WORKDIR /user/local

ADD target/${JAR_FILE} /user/local/app.jar

# Expose port
EXPOSE 19090

# Run
ENTRYPOINT ["java","-jar","-Dspring.profiles.active=dev","/user/local/app.jar"]