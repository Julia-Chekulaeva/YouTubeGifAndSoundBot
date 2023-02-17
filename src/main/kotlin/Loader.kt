import com.github.kiulian.downloader.YoutubeDownloader
import com.github.kiulian.downloader.downloader.request.RequestVideoInfo
import com.github.kiulian.downloader.downloader.response.Response
import com.github.kiulian.downloader.model.videos.VideoInfo
import com.github.kiulian.downloader.model.videos.quality.VideoQuality
import net.bramp.ffmpeg.FFmpeg
import net.bramp.ffmpeg.FFmpegExecutor
import net.bramp.ffmpeg.FFprobe
import net.bramp.ffmpeg.builder.FFmpegBuilder
import java.io.File


class URLException : Exception() {
    override val message = "Wrong URL"
}

val videoQualityMax = VideoQuality.medium

val urlRegex = Regex(
    """https?://((www\.)?youtube\.com/(watch\?v=|shorts/)|youtu\.be/)[\w\d_-]+([&?].*)?"""
)

fun getInfo(url: String, downloader: YoutubeDownloader): Response<VideoInfo> {
    if (!url.matches(urlRegex)) {
        throw URLException()
    }
    val videoId = url.split("youtu.be/", "shorts/", "watch?v=", "?", "&")[1]

    val requestInfo = RequestVideoInfo(videoId)
    return downloader.getVideoInfo(requestInfo)
}

fun loader (
    start: Int, end: Int, fileName: String,
    videoInfo: VideoInfo, cmdId: Int
) {
    val file = File(fileName)
    val startL = start * 1000L
    val endL = end * 1000L
    val urlFormat = when (cmdId) {
        2 -> videoInfo.videoFormats().filter {
                it.videoQuality() <= videoQualityMax
            }.maxByOrNull { it.videoQuality() }!!
        3 -> videoInfo.bestAudioFormat()
        else -> videoInfo.bestVideoWithAudioFormat()
    }
    convert(urlFormat.url(), file.absolutePath, startL, endL)
}

fun convert(inputFile: String, outputFile: String, start: Long, end: Long) {
    println(File("./ffmpeg-master-latest-win64-gpl/bin").listFiles().joinToString { it.name })
    println(File("./ffmpeg-master-latest-win64-gpl/bin").exists())
    val ffMPEG = FFmpeg("ffmpeg-master-latest-win64-gpl/bin/ffmpeg")
    val ffProbe = FFprobe("ffmpeg-master-latest-win64-gpl/bin/ffprobe")

    val builder = FFmpegBuilder()
        .setInput(inputFile)
        .overrideOutputFiles(true)
        .addOutput(outputFile)

    builder.startOffset = start
    builder.duration = end - start

    val executor = FFmpegExecutor(ffMPEG, ffProbe)
    executor.createJob(builder.done()).run()
}