FROM gradle:7.4-jdk17-alpine as build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle jar

FROM jrottenberg/ffmpeg:3.3-alpine as ffmpeg_img

FROM openjdk:17-alpine
ENV telegram_bot_token=$telegram_bot_token_env
COPY --from=build /home/gradle/src/build/libs/YouTubeGifAndSoundBot-1.0.jar /app
ENV ff_mpeg=ffmpeg/ffmpeg
ENV ff_probe=ffmpeg/ffprobe
COPY --from=ffmpeg_img ffmpeg /ffmpeg
ENTRYPOINT ["java", "-jar", "/app/YouTubeGifAndSoundBot-1.0.jar"]
