package com.kids123.presentation.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kids123.R
import com.kids123.domain.model.Kids123Game
import com.kids123.domain.model.LearningStepMode
import com.kids123.domain.model.NumberQuizType
import kotlin.math.ceil
import kotlin.math.sqrt

private val cardColors = listOf(
    Color(0xFFFF6B6B), Color(0xFF4ECDC4), Color(0xFFFFE66D),
    Color(0xFF95E1D3), Color(0xFFA29BFE), Color(0xFFFF9FF3)
)

@Composable
fun Kids123Board(
    game: Kids123Game,
    reducedMotion: Boolean,
    onNextStep: () -> Unit,
    onQuizAnswer: (Int) -> Unit,
    onTracePoint: (Float, Float) -> Unit,
    onCompleteTrace: () -> Unit,
    modifier: Modifier = Modifier
) {
    val step = game.currentStep ?: return
    val cardColor = Color(game.level.entry.dotColorArgb)
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LinearProgressIndicator(
            progress = { ((game.currentStepIndex + 1f) / game.level.stepCount).coerceIn(0f, 1f) },
            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
            color = cardColor
        )
        Text(stringResource(R.string.step_progress, game.currentStepIndex + 1, game.level.stepCount))
        when (step) {
            LearningStepMode.LEARN -> LearnStep(game, cardColor)
            LearningStepMode.TRACE -> TraceStep(game, cardColor, onTracePoint, onCompleteTrace)
            LearningStepMode.QUIZ -> QuizStep(game, cardColor, onQuizAnswer)
        }
        if (step != LearningStepMode.QUIZ && (step != LearningStepMode.TRACE || game.traceCompleted)) {
            Button(
                onClick = onNextStep,
                enabled = step != LearningStepMode.TRACE || game.traceCompleted,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = cardColor),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(stringResource(R.string.tap_next), fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun LearnStep(game: Kids123Game, cardColor: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor.copy(alpha = 0.25f))
    ) {
        Column(
            Modifier.fillMaxWidth().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                game.level.numberValue.toString(),
                fontSize = 120.sp,
                fontWeight = FontWeight.Bold,
                color = cardColor
            )
            DotGrid(count = game.level.dotCount, color = cardColor, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
fun DotGrid(count: Int, color: Color, modifier: Modifier = Modifier) {
    val columns = ceil(sqrt(count.toDouble())).toInt().coerceAtLeast(1)
    val rows = ceil(count / columns.toFloat()).toInt()
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        var drawn = 0
        repeat(rows) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(columns) {
                    if (drawn < count) {
                        Box(
                            Modifier.size(28.dp).clip(CircleShape).background(color),
                            contentAlignment = Alignment.Center
                        ) {}
                        drawn++
                    }
                }
            }
        }
    }
}

@Composable
private fun TraceStep(
    game: Kids123Game,
    cardColor: Color,
    onTracePoint: (Float, Float) -> Unit,
    onCompleteTrace: () -> Unit
) {
    val digit = game.level.numberValue.toString()
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(stringResource(R.string.trace_mode_title), fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Box(
            Modifier.fillMaxWidth().height(280.dp).clip(RoundedCornerShape(24.dp))
                .background(Color.White).border(3.dp, cardColor, RoundedCornerShape(24.dp))
                .pointerInput(game.currentStepIndex) {
                    detectDragGestures(
                        onDragStart = { onTracePoint(it.x / size.width, it.y / size.height) },
                        onDrag = { change, _ ->
                            change.consume()
                            onTracePoint(change.position.x / size.width, change.position.y / size.height)
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Text(digit, fontSize = 160.sp, fontWeight = FontWeight.Bold, color = Color.LightGray.copy(alpha = 0.5f))
            Canvas(Modifier.fillMaxSize()) {
                if (game.tracePoints.size >= 2) {
                    val path = Path()
                    game.tracePoints.forEachIndexed { i, p ->
                        val o = Offset(p.x * size.width, p.y * size.height)
                        if (i == 0) path.moveTo(o.x, o.y) else path.lineTo(o.x, o.y)
                    }
                    drawPath(path, cardColor, style = Stroke(12f, cap = StrokeCap.Round))
                }
            }
        }
        if (!game.traceCompleted) {
            Button(onClick = onCompleteTrace, shape = RoundedCornerShape(16.dp)) {
                Text(stringResource(R.string.trace_done), fontSize = 18.sp)
            }
        } else {
            Text(stringResource(R.string.trace_complete), color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun QuizStep(game: Kids123Game, cardColor: Color, onQuizAnswer: (Int) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(stringResource(R.string.quiz_mode_title), fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
        Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = cardColor.copy(alpha = 0.2f))) {
            Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(game.level.quizPrompt, fontSize = 22.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center)
                Spacer(Modifier.height(12.dp))
                when (game.level.quizType) {
                    NumberQuizType.COUNT -> DotGrid(game.level.dotCount, cardColor)
                    NumberQuizType.BIGGER -> {
                        val other = game.level.compareValue ?: (game.level.numberValue - 1).coerceAtLeast(1)
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(game.level.numberValue.toString(), fontSize = 36.sp, fontWeight = FontWeight.Bold)
                                DotGrid(game.level.numberValue, cardColor)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(other.toString(), fontSize = 36.sp, fontWeight = FontWeight.Bold)
                                DotGrid(other, Color(0xFF42A5F5))
                            }
                        }
                    }
                }
            }
        }
        game.level.quizOptions.forEachIndexed { index, option ->
            val eliminated = index in game.eliminatedQuizOptions
            val answered = game.quizAnswered || game.awaitingAdvance
            val selected = game.quizSelectedIndex == index
            val correct = index == game.level.quizCorrectIndex
            val showResult = game.awaitingAdvance && selected
            val bg = when {
                showResult && correct -> MaterialTheme.colorScheme.primaryContainer
                showResult && !correct -> MaterialTheme.colorScheme.errorContainer
                eliminated -> MaterialTheme.colorScheme.surface.copy(alpha = 0.4f)
                else -> cardColors[index % cardColors.size].copy(alpha = 0.35f)
            }
            Box(
                Modifier.fillMaxWidth().height(64.dp).clip(RoundedCornerShape(16.dp)).background(bg)
                    .border(2.dp, cardColor.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    .clickable(enabled = !eliminated && !answered) { onQuizAnswer(index) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    if (eliminated) "— $option" else option.toString(),
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun GameStatChip(label: String, value: String, modifier: Modifier = Modifier) {
    Text("$label: $value", style = MaterialTheme.typography.labelLarge, modifier = modifier)
}
