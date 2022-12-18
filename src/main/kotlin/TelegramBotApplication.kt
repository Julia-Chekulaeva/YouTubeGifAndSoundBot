import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession
import java.io.File

val secrets = File("config.yaml").readLines().map { it.split(": ") }.associate { it[0] to it[1] }

val commands = listOf("/start", "/commands", "/gif", "/audio")

fun main() {
    TelegramBotsApi(DefaultBotSession::class.java).registerBot(Bot())
}

class Bot : TelegramLongPollingBot() {
    private val botUsername = "YouTubeGifAndSoundBot"

    override fun getBotToken() = secrets["telegram_bot_token"]

    override fun getBotUsername() = botUsername

    override fun onUpdateReceived(update: Update) {
        val args = update.message.text.split(Regex(""" +"""))
        val cmd = args[0]
        val sendMsg = SendMessage()
        sendMsg.setChatId(update.message.chatId)
        if (cmd == commands[0]) {
            sendMsg.text = "Hello!"
            this.execute(sendMsg)
        }
        if (cmd == commands[1]) {
            sendMsg.text = commands.joinToString("\n","List of commands:\n")
            this.execute(sendMsg)
        }
        if (cmd == commands[2]) {
            sendMsg.text = "Start downloading a gif..."
            this.execute(sendMsg)
        }
        if (cmd == commands[3]) {
            sendMsg.text = "Start downloading an audio..."
            this.execute(sendMsg)
        }
    }
}