package okul.com.data

import io.ktor.http.cio.websocket.*

data class Player(
    val userName: String,
    var socket: WebSocketSession,
    val clientId: String,
    var isDrawing: Boolean,
    var score: Int = 0,
    var rank: Int = 0
)