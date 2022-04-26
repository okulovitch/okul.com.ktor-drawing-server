package okul.com.data.models

import okul.com.util.Constants

data class ChosenWord(
    val chosenWord: String,
    val roomName: String
): BaseModel(Constants.TYPE_CHOSEN_WORD)
