package okul.com.data.models

import okul.com.util.Constants

data class Announcement(
    val message: String,
    val timestamp: Long,
    val announcementType: Int
) : BaseModel(Constants.TYPE_ANNOUNCEMENT) {
    companion object {
        const val TYPE_PLAYER_GUESSED_WORD = 0
        const val TYPE_PLAYER_JOINED = 0
        const val TYPE_PLAYER_LEFT = 0
        const val TYPE_EVERYBODY_GUESSED_IT = 0
    }
}
