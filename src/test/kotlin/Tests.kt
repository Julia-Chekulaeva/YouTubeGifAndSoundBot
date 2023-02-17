import com.github.junrar.Junrar
import com.github.kiulian.downloader.YoutubeDownloader
import org.junit.jupiter.api.Test
import java.io.File

class Tests {

    init {
        Junrar.extract("ffmpeg-master-latest-win64-gpl.rar", "./")
    }

    private fun testLoader(
        url: String, downloader: YoutubeDownloader,
        start: Int, end: Int, fileName: String, cmdId: Int
    ) {
        val videoInfo = getInfo(url, downloader).data()
        loader(start, end, fileName, videoInfo, cmdId)
        check(File(fileName).exists())
    }

    @Test
    fun testGifLoading() {
        val downloader = YoutubeDownloader()
        testLoader("https://www.youtube.com/watch?v=NrJEFrth27Q", downloader,
            0, 10, "src/test/kotlin/resources/file0_10.gif", 2)
        testLoader("https://www.youtube.com/watch?v=NrJEFrth27Q", downloader,
            140, 145, "src/test/kotlin/resources/file140_160.gif", 2)
    }

    @Test
    fun testAudioLoading() {
        val downloader = YoutubeDownloader()
        testLoader("https://www.youtube.com/watch?v=NrJEFrth27Q", downloader,
            0, 10, "src/test/kotlin/resources/file0_10.wav", 3)
        testLoader("https://www.youtube.com/watch?v=NrJEFrth27Q", downloader,
            140, 145, "src/test/kotlin/resources/file140_160.wav", 2)
    }

    @Test
    fun testVideoLoading() {
        val downloader = YoutubeDownloader()
        testLoader("https://www.youtube.com/watch?v=NrJEFrth27Q", downloader,
            0, 10, "src/test/kotlin/resources/file0_10.mp4", 4)
        testLoader("https://www.youtube.com/watch?v=NrJEFrth27Q", downloader,
            140, 145, "src/test/kotlin/resources/file140_160.mp4", 2)
    }
}