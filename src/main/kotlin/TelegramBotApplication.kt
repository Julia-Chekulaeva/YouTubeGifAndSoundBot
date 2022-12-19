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

val secrets = File("config.yaml").readLines().map { it.split(": ") }.associate { it[0] to it[1] }

val commands = listOf("/start", "/commands", "/gif", "/audio")

val responseMessages = listOf(
    "Hello!", commands.joinToString("\n","List of commands:\n"),
    "Starting downloading a gif...", "Starting downloading an audio..."
)

const val resources = "src/main/resources/"

val regex = Regex("""((\d?\d:)?[0-5]?\d:[0-5]?\d)?-((\d?\d:)?[0-5]?\d:[0-5]?\d)?""")

fun main() {
    TelegramBotsApi(DefaultBotSession::class.java).registerBot(Bot())
}

class Bot : TelegramLongPollingBot() {
    private val botUsername = "YouTubeGifAndSoundBot"

    override fun getBotToken() = secrets["telegram_bot_token"]

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
            val fileName = resources + update.updateId + if (cmdId == 2)".gif" else ".mp4"
            try {
                val videoInfo = getInfo(url, downloader)
                val duration = videoInfo.data().details().lengthSeconds()
                val timeSteps = arrayOf(0, duration)
                if (args.size >= 3 && args[2].matches(regex)) {
                    args[2].split("-").withIndex().forEach {
                        if (it.value != "")
                            timeSteps[it.index] = it.value.split(":").fold(0) { sum, it2 ->
                                sum * 60 + it2.toInt()
                            }
                    }
                }
                val start = timeSteps[0]
                val end = timeSteps[1].coerceAtMost(duration)
                sendMsg.text = "Start time: $start seconds\nEnd time: $end seconds"
                execute(sendMsg)
                if (cmdId == 2) {
                    gifLoader(url, start, end, fileName)
                    val sendAnim = SendAnimation()
                    sendAnim.setChatId(update.message.chatId)
                    sendAnim.animation = InputFile()
                    sendAnim.animation.setMedia(File(fileName))
                    execute(sendAnim)
                    sendMsg.text = "Gif is loaded from YouTube."
                    execute(sendMsg)
                } else {
                    gifLoader(url, start, end, fileName)
                    val sendAudio = SendAudio()
                    sendAudio.setChatId(update.message.chatId)
                    sendAudio.audio = InputFile()
                    sendAudio.audio.setMedia(File(fileName))
                    execute(sendAudio)
                    sendMsg.text = "Audio is loaded from YouTube."
                    execute(sendMsg)
                }
            } catch (e: Exception) {
                sendMsg.text = "An error occurred: ${e.message}"
                execute(sendMsg)
            } finally {
                File(fileName).delete()
            }
        }
    }
}