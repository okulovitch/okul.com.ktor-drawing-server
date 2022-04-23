package okul.com.plugins

import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import okul.com.routes.createRoomRoute
import okul.com.routes.getRoomsRoute

fun Application.configureRouting() {
    install(Routing) {
        createRoomRoute()
        getRoomsRoute()
    }
}
