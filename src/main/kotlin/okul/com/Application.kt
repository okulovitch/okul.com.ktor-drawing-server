package okul.com

import com.google.gson.Gson
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import okul.com.plugins.*

val server = DrawingServer()// used for global access
val gson = Gson()// used for global access
fun main() {
    embeddedServer(Netty, port = 8001, host = "0.0.0.0") {
        configureSessions()
        configureRouting()
        configureSerialization()
        configureSockets()
        configureMonitoring()
    }.start(wait = true)
}
