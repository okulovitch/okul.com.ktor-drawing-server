package okul.com.data

import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.*
import okul.com.data.models.Announcement
import okul.com.data.models.PhaseChange
import okul.com.gson

class Room(
    val name: String,
    val maxPlayers: Int,
    var players: List<Player> = listOf()
) {

    private var timerJob: Job? = null
    private var drawingPlayer: Player? = null

    private var phaseChangedListener: ((Phase) -> Unit)? = null
    var phase = Phase.WAITING_FOR_PLAYERS
        set(value) {
            synchronized(field) {
                field = value
                phaseChangedListener?.let { change ->
                    change(value)
                }
            }
        }
    private fun setPhaseChangedListener(listener: (Phase) -> Unit) {
        phaseChangedListener = listener
    }

    init {
        setPhaseChangedListener { newPhase ->
            when (newPhase) {
                Phase.WAITING_FOR_PLAYERS -> waitingForPlayers()
                Phase.WAITING_FOR_START -> waitingForStart()
                Phase.NEW_ROUND -> newRound()
                Phase.GAME_RUNNING -> gameRunning()
                Phase.SHOW_WORD -> showWord()
            }
        }
    }

    suspend fun addPlayer(clientId: String, userName: String, socket: WebSocketSession): Player {
        val player = Player(userName, socket, clientId)
        players = players + player // use imutable list to avoid indexOfBound exception

        if (players.size == 1) {
            phase = Phase.WAITING_FOR_PLAYERS
        } else if (players.size == 2 && phase == Phase.WAITING_FOR_PLAYERS) {
            phase = Phase.WAITING_FOR_START
            players = players.shuffled()
        } else if (phase == Phase.WAITING_FOR_START && players.size == maxPlayers) {
            phase = Phase.NEW_ROUND
            players = players.shuffled()
        }

        val announcement = Announcement(
            "$userName joined the room!",
            System.currentTimeMillis(),
            Announcement.TYPE_PLAYER_JOINED
        )
        broadcast(gson.toJson(announcement))

        return player
    }

    private fun timeAndNotify(ms: Long) {
        timerJob?.cancel()
        timerJob = GlobalScope.launch {
            val phaseChange = PhaseChange(
                phase,
                ms,
                drawingPlayer?.username
            )
            repeat((ms / UPDATE_TIME_FREQUENCY).toInt()) {
               if (it!= 0) {
                   phaseChange.phase = null// notify client about new phase
               }
                broadcast(gson.toJson(phaseChange))
                phaseChange.time -= UPDATE_TIME_FREQUENCY
                delay(UPDATE_TIME_FREQUENCY)
            }
            phase = when (phase) {
                Phase.WAITING_FOR_START -> Phase.NEW_ROUND
                Phase.GAME_RUNNING -> Phase.SHOW_WORD
                Phase.SHOW_WORD -> Phase.NEW_ROUND
                Phase.NEW_ROUND -> Phase.GAME_RUNNING
                else -> Phase.WAITING_FOR_PLAYERS
            }
        }
    }
    suspend fun broadcast(message: String) {
        players.forEach { player ->
            if (player.socket.isActive) {
                player.socket.send(Frame.Text(message))
            }
        }
    }

    suspend fun broadcastToAllExcept(message: String, clientId: String) {
        players.forEach { player ->
            if (player.clientId != clientId && player.socket.isActive) {
                player.socket.send(Frame.Text(message))
            }
        }
    }

    fun containsPlayer(userName: String): Boolean {
        return players.find { it.username == userName } != null
    }

    private fun waitingForPlayers() {
        GlobalScope.launch {
            val phaseChange = PhaseChange(
                Phase.WAITING_FOR_PLAYERS,
                DELAY_WAITING_FOR_START_TO_NEW_ROUND
            )
            broadcast(gson.toJson(phaseChange))
        }
    }
    private fun waitingForStart() {
        GlobalScope.launch {
            timeAndNotify(DELAY_WAITING_FOR_START_TO_NEW_ROUND)
            val phaseChange = PhaseChange(
                Phase.WAITING_FOR_START,
                DELAY_WAITING_FOR_START_TO_NEW_ROUND
            )
            broadcast(gson.toJson(phaseChange))
        }
    }
    private fun newRound() {

    }
    private fun gameRunning() {

    }
    private fun showWord() {

    }
    enum class Phase {
        WAITING_FOR_PLAYERS,
        WAITING_FOR_START,
        NEW_ROUND,
        GAME_RUNNING,
        SHOW_WORD
    }

    companion object {
        const val UPDATE_TIME_FREQUENCY = 1000L

        const val DELAY_WAITING_FOR_START_TO_NEW_ROUND = 10000L
        const val DELAY_NEW_ROUND_TO_GAME_RUNNING = 20000L
        const val DELAY_GAME_RUNNING_TO_SHOW_WORD = 60000L
        const val DELAY_SHOW_WORD_TO_NEW_ROUND = 10000L
    }
}