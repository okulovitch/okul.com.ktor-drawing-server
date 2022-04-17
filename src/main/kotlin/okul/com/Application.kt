package okul.com

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import okul.com.plugins.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        configureRouting()
        configureSerialization()
        configureSockets()
        configureMonitoring()
    }.start(wait = true)
}
