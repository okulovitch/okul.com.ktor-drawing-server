package okul.com.routes

import com.google.gson.JsonParser
import io.ktor.http.cio.websocket.*
import io.ktor.routing.*
import io.ktor.sessions.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import okul.com.data.Room
import okul.com.data.models.BaseModel
import okul.com.data.models.ChatMessage
import okul.com.data.models.DrawData
import okul.com.gson
import okul.com.server
import okul.com.session.DrawingSession
import okul.com.util.Constants

fun Route.gameWebSocketRoute() {//ws is webSocket
    route("/ws/draw"){
        standardWebSocket { socket, clientId, message, payload ->
            when (payload) {
                is DrawData -> {
                    val room = server.rooms[payload.roomName] ?: return@standardWebSocket
                    if (room.phase == Room.Phase.GAME_RUNNING) {
                        room.broadcastToAllExcept(message,clientId)
                    }
                }
                is ChatMessage -> {

                }
            }
        }
    }
}
fun Route.standardWebSocket(
    handleFrame: suspend (
        socket: DefaultWebSocketServerSession,
        clientId: String,
        message: String,
        payload: BaseModel
    ) -> Unit
) {
    //receive websocket requests
    webSocket {
        val session = call.sessions.get<DrawingSession>()
        if (session == null) {
            close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No session"))
            return@webSocket
        }

        try {
            incoming.consumeEach { frame ->
                if (frame is Frame.Text) {
                    val message = frame.readText()
                    val jsonObject = JsonParser.parseString(message).asJsonObject
                    val type = when (jsonObject.get("type").asString) {
                        Constants.TYPE_CHAT_MESSAGE -> ChatMessage::class.java
                        Constants.TYPE_DRAW_DATA -> DrawData::class.java
                        else -> BaseModel::class.java
                    }
                    val payload = gson.fromJson(message, type)
                    handleFrame(this, session.clientId, message, payload)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            //Handle disconnects
        }
    }
}