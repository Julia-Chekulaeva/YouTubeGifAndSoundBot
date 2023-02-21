FROM gradle:7.4-jdk17-alpine as build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle jar

FROM openjdk:17-alpine
ENV telegram_bot_token=telegram_bot_token
COPY --from=build /home/gradle/src/build/libs/YouTubeGifAndSoundBot-1.0.jar /app
ENV ff_mpeg=ffmpeg/ffmpeg-5.1.1-amd64-static/ffmpeg
ENV ff_probe=ffmpeg/ffmpeg-5.1.1-amd64-static/ffprobe
COPY --from=build /home/gradle/src/ffmpeg/ubuntu/ffmpeg-5.1.1-amd64-static /ffmpeg
ENTRYPOINT ["java", "-jar", "/app/YouTubeGifAndSoundBot-1.0.jar"]
