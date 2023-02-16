FROM gradle:7.4-jdk17-alpine as build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build

FROM openjdk:17-alpine
ADD ffmpeg-master-latest-win64-gpl.rar /app
ENV telegram_bot_token=telegram_bot_token
ENV ff_mpeg="/app/ffmpeg-master-latest-win64-gpl/bin/ffmpeg"
ENV ff_probe="/app/ffmpeg-master-latest-win64-gpl/bin/ffprobe"
COPY --from=build /home/gradle/src/build/libs/YouTubeGifAndSoundBot-1.0.jar /app
ENTRYPOINT ["java", "-jar", "/app/YouTubeGifAndSoundBot-1.0.jar"]