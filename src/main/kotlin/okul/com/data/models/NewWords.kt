package okul.com.data.models

import okul.com.util.Constants

data class NewWords(
    val newWords: List<String>
): BaseModel(Constants.TYPE_NEW_WORDS)
