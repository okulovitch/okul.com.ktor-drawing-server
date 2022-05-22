package okul.com

import com.google.gson.Gson
import io.ktor.application.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.sessions.*
import io.ktor.util.*
import io.ktor.websocket.*
import okul.com.plugins.*
import okul.com.routes.createRoomRoute
import okul.com.session.DrawingSession

val server = DrawingServer()// used for global access
val gson = Gson()// used for global access
fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        configureSessions()
        install(WebSockets)
        configureRouting()//todo fix
        configureSerialization()
        configureSockets()
        configureMonitoring()
    }.start(wait = true)
}
