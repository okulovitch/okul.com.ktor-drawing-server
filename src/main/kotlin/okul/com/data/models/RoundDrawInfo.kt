package okul.com.data.models

import okul.com.util.Constants

data class RoundDrawInfo(
    val data: List<String>
): BaseModel(Constants.TYPE_CUR_ROUND_DRAW_INFO)
