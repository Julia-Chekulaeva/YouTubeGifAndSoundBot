FROM gradle:7.4-jdk11-alpine
COPY . /home/gradle/src
WORKDIR /home/gradle/src
ENV telegram_bot_token=telegram_bot_token
ENV ff_mpeg="/home/ffmpeg-master-latest-win64-gpl/bin/ffmpeg"
ENV ff_probe="/home/ffmpeg-master-latest-win64-gpl/bin/ffprobe"
RUN gradle build
WORKDIR /home/gradle/src/build/libs
RUN ls -R
ADD ffmpeg-master-latest-win64-gpl.rar /home
ENTRYPOINT ["java", "-jar", "YouTubeGifAndSoundBot-1.0.jar"]