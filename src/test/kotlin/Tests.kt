import com.github.kiulian.downloader.YoutubeDownloader
import org.junit.jupiter.api.Test
import java.io.File

class Tests {

    private fun testLoader(
        url: String, downloader: YoutubeDownloader,
        start: Int, end: Int, fileName: String, cmdId: Int
    ) {
        val videoInfo = getInfo(url, downloader).data()
        val file1 = File(fileName)
        if (!file1.parentFile.exists())
            throw Exception("No parent directory for file ${file1.absolutePath}")
        file1.deleteOnExit()
        println("Downloading data into file ${file1.absolutePath}...")
        loader(start, end, fileName, videoInfo, cmdId)
        println("The loading process is completed")
        file1.parentFile?.listFiles()?.forEach { println(it.name) }
        val file2 = File(fileName)
        check(file2.exists())
        println("Data is downloaded into file ${file1.absolutePath}")
        file2.delete()
    }

    @Test
    fun testGifLoading() {
        val downloader = YoutubeDownloader()
        testLoader("https://www.youtube.com/watch?v=NrJEFrth27Q", downloader,
            0, 2, "src/test/resources/file0_10.gif", 2)
        testLoader("https://www.youtube.com/watch?v=NrJEFrth27Q", downloader,
            140, 144, "src/test/resources/file140_160.gif", 2)
    }

    @Test
    fun testAudioLoading() {
        val downloader = YoutubeDownloader()
        testLoader("https://www.youtube.com/watch?v=NrJEFrth27Q", downloader,
            0, 2, "src/test/resources/file0_10.wav", 3)
        testLoader("https://www.youtube.com/watch?v=NrJEFrth27Q", downloader,
            140, 144, "src/test/resources/file140_160.wav", 3)
    }

    @Test
    fun testVideoLoading() {
        val downloader = YoutubeDownloader()
        testLoader("https://www.youtube.com/watch?v=NrJEFrth27Q", downloader,
            0, 2, "src/test/resources/file0_10.mp4", 4)
        testLoader("https://www.youtube.com/watch?v=NrJEFrth27Q", downloader,
            140, 144, "src/test/resources/file140_160.mp4", 4)
    }
}