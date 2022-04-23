package okul.com.data.models

import okul.com.util.Constants

data class DrawData(
    val roomName: String,
    val color: Int,
    val thickness: Float,
    val fromX: Float,
    val fromY: Float,
    val toX: Float,
    val toY: Float,
    val motionEvent: Int
): BaseModel(Constants.TYPE_DRAW_DATA)
