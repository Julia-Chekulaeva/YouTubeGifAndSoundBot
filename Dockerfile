FROM gradle:7.4-jdk11-alpine
COPY . /home/gradle/src
WORKDIR /home/gradle/src
ENV telegram_bot_token=telegram_bot_token
RUN gradle build
ENTRYPOINT ["java", "-jar", "/home/gradle/src/build/libs/YouTubeGifAndSoundBot-1.0.jar"]