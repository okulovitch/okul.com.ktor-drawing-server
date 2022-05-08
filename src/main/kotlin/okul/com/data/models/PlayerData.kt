package okul.com.data.models

data class PlayerData(
    val username : String,
    var isDrawData: Boolean = false,
    var score: Int = 0,
    var rank: Int = 0
)
