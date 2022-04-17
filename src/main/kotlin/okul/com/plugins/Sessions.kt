package okul.com.plugins

import io.ktor.application.*
import io.ktor.sessions.*
import io.ktor.util.*
import okul.com.session.DrawingSession

fun Application.configureSessions(){
    install(Sessions) {//todo fix crash needed
        cookie<DrawingSession>("Session")
    }
    intercept(ApplicationCallPipeline.Features) {
        if (call.sessions.get<DrawingSession>() == null) {
            val clientId = call.parameters["client_id"] ?: ""
            call.sessions.set(DrawingSession(clientId, generateNonce()))
        }
    }
}