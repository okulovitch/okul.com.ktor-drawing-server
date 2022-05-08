package okul.com.routes

import com.google.gson.JsonParser
import io.ktor.http.cio.websocket.*
import io.ktor.routing.*
import io.ktor.sessions.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import okul.com.data.Player
import okul.com.data.Room
import okul.com.data.models.*
import okul.com.gson
import okul.com.server
import okul.com.session.DrawingSession
import okul.com.util.Constants

fun Route.gameWebSocketRoute() {
    route("/ws/draw") {
        standardWebSocket { socket, clientId, message, payload ->
            when (payload) {
                is JoinRoomHandshake -> {
                    val room = server.rooms[payload.roomName]
                    if (room == null) {
                        val gameError = GameError(GameError.ERROR_ROOM_NOT_FOUND)
                        socket.send(Frame.Text(gson.toJson(gameError)))
                        return@standardWebSocket
                    }
                    val player = Player(
                        payload.username,
                        socket,
                        payload.clientId
                    )
                    server.playerJoined(player)
                    if(!room.containsPlayer(player.username)) {
                        room.addPlayer(player.clientId, player.username, socket)
                    }
                }
                is DrawData -> {
                    val room = server.rooms[payload.roomName] ?: return@standardWebSocket
                    if (room.phase == Room.Phase.GAME_RUNNING) {
                        room.broadcastToAllExcept(message, clientId)
                    }
                }
                is ChosenWord -> {
                    val room = server.rooms[payload.roomName] ?: return@standardWebSocket
                    room.setWordAndWitchToGameRunning(payload.chosenWord)
                }
                is ChatMessage -> {
                    val room = server.rooms[payload.roomName] ?: return@standardWebSocket
                    if (!room.checkWordAndNotifyPlayers(payload)) {
                        room.broadcast(message)
                    }
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
    webSocket {
        val session = call.sessions.get<DrawingSession>()
        if (session == null) {
            close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No session."))
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
                        Constants.TYPE_ANNOUNCEMENT -> Announcement::class.java
                        Constants.TYPE_JOIN_ROOM_HANDSHAKE -> JoinRoomHandshake::class.java
                        Constants.TYPE_PHASE_CHANGE -> PhaseChange::class.java
                        Constants.TYPE_CHOSEN_WORD -> ChosenWord::class.java
                        Constants.TYPE_GAME_STATE -> GameState::class.java
                        else -> BaseModel::class.java
                    }
                    val payload = gson.fromJson(message, type)
                    handleFrame(this, session.clientId, message, payload)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            // Handle disconnects
            val playerClientId = server.getRoomWithClientId(session.clientId)?.players?.find {
                it.clientId == session.clientId
            }
            if (playerClientId != null) {
                server.playerLeft(session.clientId, false)
            }
        }
    }
}