package com.kids123.multiplayer

import com.kids123.domain.model.Difficulty
import com.kids123.domain.model.MultiplayerMode
import com.kids123.domain.model.MultiplayerSession
import com.kids123.domain.model.Kids123Game
import com.kids123.engine.Kids123Engine
import com.kids123.engine.Kids123Generator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SameDeviceSession @Inject constructor() {
    private val _session = MutableStateFlow<MultiplayerSession?>(null)
    val session: StateFlow<MultiplayerSession?> = _session.asStateFlow()

    private var sharedGame: Kids123Game? = null
    private var activePlayer = 1
    private var playerOneName = "Player 1"
    private var playerTwoName = "Player 2"

    fun start(playerOne: String, playerTwo: String, difficulty: Difficulty, seed: Long = System.currentTimeMillis()) {
        playerOneName = playerOne
        playerTwoName = playerTwo
        val level = Kids123Generator.generate(seed, 1, difficulty)
        sharedGame = Kids123Engine.createInitialGame(level)
        activePlayer = 1
        publishSession(difficulty, seed, isActive = true)
    }

    fun getActiveGame(): Kids123Game? = sharedGame

    fun applyAction(game: Kids123Game, action: (Kids123Game) -> Kids123Game): Kids123Game? {
        val current = sharedGame ?: return null
        if (current != game) return null
        val updated = action(current)
        if (updated == current) return updated

        if (updated.isCompleted) {
            val session = _session.value ?: return updated
            val roundWinnerIsPlayerOne = activePlayer == 1
            val newLocalScore = session.localScore + if (roundWinnerIsPlayerOne) 1 else 0
            val newRemoteScore = session.remoteScore + if (roundWinnerIsPlayerOne) 0 else 1
            val newLevel = Kids123Generator.generate(
                session.seed + newLocalScore + newRemoteScore,
                newLocalScore + newRemoteScore + 1,
                session.difficulty
            )
            sharedGame = Kids123Engine.createInitialGame(newLevel)
            activePlayer = if (roundWinnerIsPlayerOne) 2 else 1
            _session.value = session.copy(
                localScore = newLocalScore,
                remoteScore = newRemoteScore,
                activePlayerName = if (activePlayer == 1) playerOneName else playerTwoName
            )
            return updated
        }

        sharedGame = updated
        activePlayer = if (activePlayer == 1) 2 else 1
        publishSession(
            difficulty = _session.value?.difficulty ?: Difficulty.MEDIUM,
            seed = _session.value?.seed ?: System.currentTimeMillis(),
            isActive = true
        )
        return updated
    }

    fun end() {
        _session.value = null
        sharedGame = null
        activePlayer = 1
    }

    private fun publishSession(difficulty: Difficulty, seed: Long, isActive: Boolean) {
        _session.value = MultiplayerSession(
            mode = MultiplayerMode.SAME_DEVICE,
            localPlayerName = playerOneName,
            remotePlayerName = playerTwoName,
            activePlayerName = if (activePlayer == 1) playerOneName else playerTwoName,
            localScore = _session.value?.localScore ?: 0,
            remoteScore = _session.value?.remoteScore ?: 0,
            isActive = isActive,
            seed = seed,
            difficulty = difficulty
        )
    }
}
