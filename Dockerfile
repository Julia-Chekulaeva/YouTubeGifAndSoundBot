FROM gradle:7.4-jdk17-alpine as build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle jar

FROM openjdk:17-alpine
ENV telegram_bot_token=$telegram_bot_token_env
COPY --from=build /home/gradle/src/build/libs/YouTubeGifAndSoundBot-1.0.jar /app
RUN apk add --no-cache ffmpeg
RUN ls -R /usr/bin
ENV ffmpeg_path=/usr/bin/ffmpeg
ENV ffprobe_path=/usr/bin/ffprobe
ENTRYPOINT ["java", "-jar", "/app/YouTubeGifAndSoundBot-1.0.jar"]
