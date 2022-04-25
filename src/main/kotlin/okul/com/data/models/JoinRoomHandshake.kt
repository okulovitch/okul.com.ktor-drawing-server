package okul.com.data.models

import okul.com.util.Constants

data class JoinRoomHandshake(
    val username: String,
    val roomName: String,
    val clientId: String
) : BaseModel(Constants.TYPE_JOIN_ROOM_HANDSHAKE)
