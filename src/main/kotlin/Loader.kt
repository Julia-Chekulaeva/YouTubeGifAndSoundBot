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
    val urlFormat = when (cmdId) {
        2 -> videoInfo.videoFormats().filter {
                it.videoQuality() <= videoQualityMax
            }.maxByOrNull { it.videoQuality() }!!
        3 -> videoInfo.bestAudioFormat()
        else -> videoInfo.bestVideoWithAudioFormat()
    }
    val commands = "${System.getenv("ffmpeg-path")} -i ${urlFormat.url()} " +
            "-ss ${start / 3600}:${(start / 60) % 60}:${start % 60} " +
            "-t ${end / 3600}:${(end / 60) % 60}:${end % 60} ${file.absolutePath}"
    Runtime.getRuntime().exec(commands)
}

fun convert(inputFile: String, outputFile: String, start: Long, end: Long) {
    val ffMPEG = FFmpeg(System.getenv("ffmpeg-path") ?: throw Exception("No env var named ffmpeg-path"))
    val ffProbe = FFprobe(System.getenv("ffprobe-path") ?: throw Exception("No env var named ffmpeg-path"))

    val builder = FFmpegBuilder()
        .setInput(inputFile)
        .overrideOutputFiles(true)
        .addOutput(outputFile)

    builder.startOffset = start
    builder.duration = end - start

    val executor = FFmpegExecutor(ffMPEG, ffProbe)
    executor.createJob(builder.done()).run()
}