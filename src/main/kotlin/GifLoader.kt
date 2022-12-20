import it.grabz.grabzit.GrabzItClient
import it.grabz.grabzit.GrabzItException
import it.grabz.grabzit.parameters.AnimationOptions

const val defaultDuration = 60

class GifTooLargeException(override val message: String?) : Exception()

fun gifLoader(url: String, start: Int, end: Int, fileName: String): Boolean {
    val client = GrabzItClient(secrets["gif_loader_key"], secrets["gif_loader_secret"])
    val animOptions = AnimationOptions()
    animOptions.duration = end - start
    animOptions.framesPerSecond = 10F
    animOptions.speed = 1F
    animOptions.start = start
    animOptions.quality = -1
    try {
        client.URLToAnimation(url, animOptions)
        client.SaveTo(fileName)
        return true
    } catch (e: GrabzItException) {
        if (e.message == "Your animated GIF exceeds the maximum total resolution allowed. " +
            "Reduce the duration, frames per second, width or height of the image to create your animated GIF.")
            throw GifTooLargeException("The given interval is too big: the video fragment can't be loaded.")
        throw e
    }
}