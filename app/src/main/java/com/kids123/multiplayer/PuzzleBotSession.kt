package com.kids123.multiplayer

import com.kids123.domain.model.Kids123Game
import com.kids123.domain.model.Difficulty
import com.kids123.domain.model.LearningStepMode
import com.kids123.domain.model.MultiplayerMode
import com.kids123.domain.model.MultiplayerSession
import com.kids123.engine.Kids123Engine
import com.kids123.engine.Kids123Generator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PuzzleBotSession @Inject constructor() {
    private val _session = MutableStateFlow<MultiplayerSession?>(null)
    val session: StateFlow<MultiplayerSession?> = _session.asStateFlow()

    private var playerGame: Kids123Game? = null
    private var botGame: Kids123Game? = null
    private var playerName = "You"
    private val botName = "AI Bot"

    fun start(player: String, difficulty: Difficulty, seed: Long = System.currentTimeMillis()) {
        playerName = player
        val level = Kids123Generator.generate(seed, 1, difficulty)
        val game = Kids123Engine.createInitialGame(level)
        playerGame = game
        botGame = game
        _session.value = MultiplayerSession(
            mode = MultiplayerMode.SAME_DEVICE,
            localPlayerName = playerName,
            remotePlayerName = botName,
            activePlayerName = playerName,
            isActive = true,
            seed = seed,
            difficulty = difficulty
        )
    }

    fun getPlayerGame(): Kids123Game? = playerGame

    fun applyPlayerAction(game: Kids123Game, action: (Kids123Game) -> Kids123Game): Kids123Game? {
        val current = playerGame ?: return null
        if (current != game) return null
        val updated = action(current)
        playerGame = updated
        botGame = updated
        return updated
    }

    fun applyBotMove(): Kids123Game? {
        var game = botGame ?: return null
        game = when (game.currentStep) {
            LearningStepMode.LEARN -> Kids123Engine.nextStep(game)
            LearningStepMode.TRACE -> {
                var traced = game
                repeat(8) {
                    traced = Kids123Engine.addTracePoint(traced, 0.5f, 0.5f)
                }
                Kids123Engine.completeTrace(traced)
            }
            LearningStepMode.QUIZ -> {
                val answer = Kids123Engine.botQuizAnswer(game) ?: return game
                Kids123Engine.selectQuizAnswer(game, answer)
            }
            null -> game
        }
        playerGame = game
        botGame = game
        val session = _session.value
        if (session != null && game.isCompleted) {
            _session.value = session.copy(
                remoteScore = session.remoteScore + 1,
                activePlayerName = playerName
            )
        }
        return game
    }

    fun onPlayerWon() {
        val session = _session.value ?: return
        _session.value = session.copy(
            localScore = session.localScore + 1,
            activePlayerName = playerName
        )
        startNewRound(session)
    }

    fun onBotWon() {
        val session = _session.value ?: return
        _session.value = session.copy(
            remoteScore = session.remoteScore + 1,
            activePlayerName = playerName
        )
        startNewRound(session)
    }

    private fun startNewRound(session: MultiplayerSession) {
        val newSeed = session.seed + session.localScore + session.remoteScore
        val level = Kids123Generator.generate(newSeed, session.localScore + session.remoteScore + 1, session.difficulty)
        val game = Kids123Engine.createInitialGame(level)
        playerGame = game
        botGame = game
    }

    fun end() {
        _session.value = null
        playerGame = null
        botGame = null
    }
}
