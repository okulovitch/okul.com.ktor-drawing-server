package okul.com

import io.ktor.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.sessions.*
import io.ktor.util.*
import okul.com.plugins.*
import okul.com.session.DrawingSession

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        install(Sessions) {
            cookie<DrawingSession>("Session")
        }
        intercept(ApplicationCallPipeline.Features) {
           if (call.sessions.get<DrawingSession>() == null) {
               val clientId = call.parameters["client_id"] ?: ""
               call.sessions.set(DrawingSession(clientId, generateNonce()))
           }
        }

        configureRouting()
        configureSerialization()
        configureSockets()
        configureMonitoring()
    }.start(wait = true)
}
