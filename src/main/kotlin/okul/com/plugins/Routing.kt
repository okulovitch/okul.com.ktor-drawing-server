package okul.com.plugins

import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.application.*
import io.ktor.websocket.*

import okul.com.routes.createRoomRoute
import okul.com.routes.gameWebSocketRoute

import okul.com.routes.getRoomsRoute
import okul.com.routes.joinRoomRoute

fun Application.configureRouting() {
    install(WebSockets)
    install(Routing) {
        createRoomRoute()
        getRoomsRoute()
        joinRoomRoute()
        gameWebSocketRoute()
    }
}
