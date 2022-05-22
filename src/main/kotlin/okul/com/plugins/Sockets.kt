package okul.com.plugins

import io.ktor.http.cio.websocket.*
import io.ktor.websocket.*
import io.ktor.application.*
import io.ktor.routing.*

fun Application.configureSockets() {
//    install(WebSockets)

    routing {
        webSocket("/") { // websocketSession
            for (frame in incoming) {
                when (frame) {
                    is Frame.Text -> {
                        val text = frame.readText()
                        outgoing.send(Frame.Text("YOU SAID: $text"))
                        if (text.equals("bye", ignoreCase = true)) {
                            close(CloseReason(CloseReason.Codes.NORMAL, "Client said BYE"))
                        }
                    }
                }
            }
        }
    }
}
