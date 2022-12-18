import it.grabz.grabzit.GrabzItClient
import it.grabz.grabzit.parameters.AnimationOptions
import java.io.File

val secrets = File("config.yaml").readLines().map { it.split(": ") }.associate { it[0] to it[1] }

fun main() {
    gifLoader("https://youtu.be/gVkyBu_v7vA", 11, 100)
}

fun gifLoader(url: String, start: Int, end: Int) {
    val client = GrabzItClient(secrets["gif_loader_key"], secrets["gif_loader_secret"])

    val options = AnimationOptions()
    options.duration = end - start
    options.start = start
    options.customId = "123456"
    println("start=${options.start}, duration=${options.duration}, frPerSec=${options.framesPerSecond}, speed=${
        options.speed}, h=${options.height}, w=${options.width}, q=${options.quality}")

    client.URLToAnimation(url, options)
    //Then call the Save method
    client.Save("http://www.example.com/handler")
}