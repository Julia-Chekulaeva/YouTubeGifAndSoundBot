FROM gradle:7.4-jdk11-alpine
COPY . /home/gradle/src
WORKDIR /home/gradle/src
ENV telegram_bot_token=telegram_bot_token
ENV ff_mpeg="/app/ffmpeg-master-latest-win64-gpl/bin/ffmpeg"
ENV ff_probe="/app/ffmpeg-master-latest-win64-gpl/bin/ffprobe"
RUN gradle build
RUN cd build/libs
RUN ls -R
COPY /home/gradle/src/build/libs/YouTubeGifAndSoundBot-1.0.jar /app/app.jar
ADD ffmpeg-master-latest-win64-gpl.rar /app
ENTRYPOINT ["java", "-jar", "/app/app.jar"]