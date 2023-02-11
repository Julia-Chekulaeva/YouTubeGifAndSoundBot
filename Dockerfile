FROM gradle:7.4-jdk11-alpine
ENV telegram_bot_token=telegram_bot_token
RUN gradle build
RUN mkdir "app"
COPY build/libs/YouTubeGifAndSoundBot-1.0.jar /app/app.jar
ADD ffmpeg-master-latest-win64-gpl.rar /app/ffmpeg-master-latest-win64-gpl
ENTRYPOINT ["java", "-jar", "/app/build/libs/YouTubeGifAndSoundBot-1.0.jar"]