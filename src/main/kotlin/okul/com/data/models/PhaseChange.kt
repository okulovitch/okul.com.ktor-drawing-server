package okul.com.data.models

import okul.com.data.Room
import okul.com.util.Constants

data class PhaseChange(
    var phase: Room.Phase?,
    var time: Long,
    val drawingPlayer: String?= null
): BaseModel(Constants.TYPE_PHASE_CHANGE)
