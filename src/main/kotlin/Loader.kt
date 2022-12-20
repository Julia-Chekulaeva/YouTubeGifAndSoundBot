import com.github.kiulian.downloader.YoutubeDownloader
import com.github.kiulian.downloader.downloader.request.RequestVideoInfo
import com.github.kiulian.downloader.downloader.response.Response
import com.github.kiulian.downloader.model.videos.VideoInfo
import net.bramp.ffmpeg.FFmpeg
import net.bramp.ffmpeg.FFmpegExecutor
import net.bramp.ffmpeg.FFprobe
import net.bramp.ffmpeg.builder.FFmpegBuilder
import java.io.File


class URLException : Exception() {
    override val message = "Wrong URL"
}

val urlRegex = Regex(
    """https?://(www.youtube.com/watch\?v=[\w\d_]+([&?].*)?|youtu.be/[\w\d_]+)"""
)

fun getInfo(url: String, downloader: YoutubeDownloader): Response<VideoInfo> {
    if (!url.matches(urlRegex)) {
        throw URLException()
    }
    val urlSplit = url.split("://")[1]
    val videoId = if (urlSplit.startsWith("www.youtube.com/watch?v="))
            urlSplit.split("watch?v=", "?", "&")[1]
        else if (urlSplit.startsWith("youtube.com/shorts/"))
            urlSplit.split("shorts/", "?", "&")[1]
        else
            urlSplit.split("/")[1]

    val requestInfo = RequestVideoInfo(videoId)
    return downloader.getVideoInfo(requestInfo)
}

fun loader (
    start: Int, end: Int, fileName: String,
    videoInfo: VideoInfo, cmdId: Int
) {
    val file = File(fileName)
    val urlFormat = when (cmdId) {
        2 -> videoInfo.bestVideoFormat()
        else -> videoInfo.bestAudioFormat()
    }
    convert(urlFormat.url(), file.absolutePath, start, end)
}

fun convert(inputFile: String, outputFile: String, start: Int, end: Int) {
    val ffMPEG = FFmpeg(secrets["ff_mpeg"] ?: throw Exception("No ff_mpeg specified in config.yaml file"))
    val ffProbe = FFprobe(secrets["ff_probe"] ?: throw Exception("No ff_probe specified in config.yaml file"))

    val builder = FFmpegBuilder()
        .setInput(inputFile)
        .overrideOutputFiles(true)
        .addOutput(outputFile)

    builder.startOffset = start * 1000L
    if (end >= 0)
        builder.duration = (end - start) * 1000L

    val executor = FFmpegExecutor(ffMPEG, ffProbe)
    executor.createJob(builder.done()).run()
}