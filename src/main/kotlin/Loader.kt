import com.github.kiulian.downloader.YoutubeDownloader
import com.github.kiulian.downloader.downloader.request.RequestVideoInfo
import com.github.kiulian.downloader.downloader.response.Response
import com.github.kiulian.downloader.model.videos.VideoInfo
import com.github.kiulian.downloader.model.videos.quality.VideoQuality
import java.io.File
import java.util.concurrent.TimeUnit


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
    val urlFormat = when (cmdId) {
        2 -> videoInfo.videoFormats().filter {
                it.videoQuality() <= videoQualityMax
            }.maxByOrNull { it.videoQuality() }!!
        3 -> videoInfo.bestAudioFormat()
        else -> videoInfo.bestVideoWithAudioFormat()
    }
    val command = "${System.getenv("ffmpeg_path")} -i ${urlFormat.url()} " +
            "-ss ${start / 3600}:${(start / 60) % 60}:${start % 60} " +
            "-t ${end / 3600}:${(end / 60) % 60}:${end % 60} ${file.absolutePath}"
    val process = Runtime.getRuntime().exec(command)
    println("Command: $command")
    println("The process has exited before timeout: ${process.waitFor(3, TimeUnit.MINUTES)}")
    println("Exit code: ${process.exitValue()}")
    println("""Input ffmpeg stream:
        |${process.inputReader().readText()}""".trimMargin())
    println("""Error ffmpeg stream:
        |${process.errorReader().readText()}""".trimMargin())
}