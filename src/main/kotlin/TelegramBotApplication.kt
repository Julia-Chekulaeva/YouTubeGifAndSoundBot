import com.github.kiulian.downloader.YoutubeDownloader
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation
import org.telegram.telegrambots.meta.api.methods.send.SendAudio
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.InputFile
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession
import java.io.File

val commands = listOf("/start", "/commands", "/gif", "/audio")

val responseMessages = listOf(
    """Hello!
        |I can load animation (.gif) of audio (.wav) from the fragment from YouTube.
        |Please send me command
        |/gif <youtube_url> [[[h:]m:s]-[[h:]m:s]]
        |or
        |/gif <youtube_url> [[[h:]m:s]-[[h:]m:s]]
        |to start loading. Example:
        |/gif https://youtu.be/6ofIPBp_mXo -00:5
        |<youtube_url> format should be (http|https)://[www.](youtube.com.(watch?=|shorts/)|youtu.be/)<...>
        |You can use the command /commands to get the list of available commands.""".trimMargin(), commands.joinToString("\n","List of commands:\n"),
    "Starting downloading a gif...", "Starting downloading an audio..."
)

const val resources = "src/main/resources/"

const val audioExt = ".wav"
const val animExt = ".gif"
const val videoExt = ".mp4"

val regex = Regex("""((\d?\d:)?[0-5]?\d:[0-5]?\d)?-((\d?\d:)?[0-5]?\d:[0-5]?\d)?""")

fun main() {
    TelegramBotsApi(DefaultBotSession::class.java).registerBot(Bot())
}

class Bot : TelegramLongPollingBot() {
    private val botUsername = "YouTubeGifAndSoundBot"

    override fun getBotToken(): String = System.getenv("telegram_bot_token")

    override fun getBotUsername() = botUsername

    override fun onUpdateReceived(update: Update) {
        val args = update.message.text.split(Regex("""\s+"""))
        val cmd = args[0]
        val sendMsg = SendMessage()
        sendMsg.setChatId(update.message.chatId)
        val cmdId = commands.indexOf(cmd)
        sendMsg.text = responseMessages[cmdId]
        execute(sendMsg)
        if (cmdId in 2..3) {
            if (args.size < 2) {
                sendMsg.text = "No URL provided"
                execute(sendMsg)
                return
            }
            val url = args[1]
            val downloader = YoutubeDownloader()
            val fileName = resources + update.updateId + if (cmdId == 2) animExt else audioExt
            try {
                val videoInfoData = getInfo(url, downloader).data()
                val duration = videoInfoData.details().lengthSeconds()
                val timeSteps = arrayOf(0, -1)
                if (args.size >= 3 && args[2].matches(regex)) {
                    args[2].split("-").withIndex().forEach {
                        if (it.value != "")
                            timeSteps[it.index] = it.value.split(":").fold(0) { sum, it2 ->
                                sum * 60 + it2.toInt()
                            }
                    }
                }
                val start = timeSteps[0]
                val end = if (timeSteps[1] >= 0) timeSteps[1] else duration
                if (start >= end) {
                    throw Exception("The start of the fragment must be less than the end of it")
                }
                if (end > duration) {
                    throw Exception("The end of the fragment mustn't be greater than the video length")
                }
                sendMsg.text = "Start time: $start seconds\nEnd time: $end seconds"
                execute(sendMsg)
                loader(start, end, fileName, videoInfoData, cmdId)
                val file = File(fileName)
                when (cmdId) {
                    2 -> {
                        val sendAnim = SendAnimation()
                        sendAnim.setChatId(update.message.chatId)
                        sendAnim.animation = InputFile(file)
                        execute(sendAnim)
                        sendMsg.text = "Gif is loaded from YouTube."
                    }
                    3 -> {
                        val sendAudio = SendAudio()
                        sendAudio.setChatId(update.message.chatId)
                        sendAudio.audio = InputFile(file)
                        execute(sendAudio)
                        sendMsg.text = "Audio is loaded from YouTube."
                    }
                }
                execute(sendMsg)
            } catch (e: Exception) {
                sendMsg.text = "An error occurred: ${e.message}"
                execute(sendMsg)
            } finally {
                File(fileName).delete()
            }
        }
    }
}