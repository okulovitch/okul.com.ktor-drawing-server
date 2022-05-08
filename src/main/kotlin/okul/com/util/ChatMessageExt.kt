package okul.com.util

import okul.com.data.models.ChatMessage

fun ChatMessage.matchesWord(word: String): Boolean {
    return message.lowercase().trim() == word.lowercase().trim()
}