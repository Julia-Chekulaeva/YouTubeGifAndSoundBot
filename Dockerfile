FROM gradle:7.4-jdk17-alpine as build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle jar

FROM openjdk:17-alpine
COPY --from=build /home/gradle/src/build/libs/YouTubeGifAndSoundBot-1.0.jar /app
RUN apk update
RUN apk add --no-cache ffmpeg
ENV ffmpeg_path=/usr/bin/ffmpeg
ENTRYPOINT ["java", "-jar", "/app/YouTubeGifAndSoundBot-1.0.jar"]
