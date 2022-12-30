import com.github.kiulian.downloader.YoutubeDownloader
import com.github.kiulian.downloader.downloader.request.RequestVideoInfo
import com.github.kiulian.downloader.downloader.response.Response
import com.github.kiulian.downloader.model.videos.VideoInfo
import com.github.kiulian.downloader.model.videos.quality.VideoQuality
import com.madgag.gif.fmsware.AnimatedGifEncoder
import net.bramp.ffmpeg.FFmpeg
import net.bramp.ffmpeg.FFmpegExecutor
import net.bramp.ffmpeg.FFprobe
import net.bramp.ffmpeg.builder.FFmpegBuilder
import java.io.File
import javax.imageio.ImageIO


class URLException : Exception() {
    override val message = "Wrong URL"
}

const val periodMS = 100
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
    if (cmdId == 2) {
        val gifFile = file.absolutePath
        val frames = List((end - start) * 1000 / periodMS) { "$gifFile$it$animExt" }
        val video = gifFile + videoExt
        val animEncoder = AnimatedGifEncoder()
        animEncoder.start(gifFile)
        animEncoder.setDelay(periodMS)
        try {
            convert(urlFormat.url(), video, startL, endL)
            for (i in 0L until endL - startL step periodMS.toLong()) {
                val frameFilePath = frames[(i / periodMS).toInt()]
                convert(video, frameFilePath, i, i + periodMS)
                val buffImg = ImageIO.read(File(frameFilePath))
                animEncoder.addFrame(buffImg)
            }
        } catch (e: Exception) {
            throw e
        } finally {
            animEncoder.finish()
            File(video).delete()
            for (i in 0 until ((endL - startL) / periodMS).toInt()) {
                File(frames[i]).delete()
            }
        }
    } else {
        convert(urlFormat.url(), file.absolutePath, startL, endL)
    }
}

fun convert(inputFile: String, outputFile: String, start: Long, end: Long) {
    val ffMPEG = FFmpeg(System.getenv("ff_mpeg"))
    val ffProbe = FFprobe(System.getenv("ff_probe"))

    val builder = FFmpegBuilder()
        .setInput(inputFile)
        .overrideOutputFiles(true)
        .addOutput(outputFile)

    builder.startOffset = start
    builder.duration = end - start

    val executor = FFmpegExecutor(ffMPEG, ffProbe)
    executor.createJob(builder.done()).run()
}