import it.grabz.grabzit.GrabzItClient
import it.grabz.grabzit.GrabzItException
import it.grabz.grabzit.parameters.AnimationOptions
import java.io.File


const val resources = "src/main/resources/"

val secrets = File("config.yaml").readLines().map { it.split(": ") }.associate { it[0] to it[1] }

class GifTooLargeException(override val message: String?) : Exception()

fun main() {
    gifLoader("https://youtu.be/TtCXUFImZYE", 0, 40)
}

fun gifLoader(url: String, start: Int, end: Int): Boolean {
    val client = GrabzItClient(secrets["gif_loader_key"], secrets["gif_loader_secret"])
    val animOptions = AnimationOptions()
    println(
        "start=${animOptions.start}, duration=${animOptions.duration}, frPerSec=${animOptions.framesPerSecond}, speed=${
            animOptions.speed
        }, h=${animOptions.height}, w=${animOptions.width}, q=${animOptions.quality}"
    )
    animOptions.duration = end - start
    animOptions.framesPerSecond = 20F
    animOptions.speed = 1F
    animOptions.start = start
    animOptions.quality = -1
    println(
        "start=${animOptions.start}, duration=${animOptions.duration}, frPerSec=${animOptions.framesPerSecond}, speed=${
            animOptions.speed
        }, h=${animOptions.height}, w=${animOptions.width}, q=${animOptions.quality}"
    )
    try {
        client.URLToAnimation(url, animOptions)
        client.SaveTo(resources + "anim.gif")
        return true
    } catch (e: GrabzItException) {
        if (e.message == "Your animated GIF exceeds the maximum total resolution allowed. " +
            "Reduce the duration, frames per second, width or height of the image to create your animated GIF.")
            throw GifTooLargeException("The given interval is too big: the video fragment can't be loaded.")
        throw e
    }
}