package com.example.play_6sem.ui.modules

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.play_6sem.data.GameContent
import com.example.play_6sem.model.MathLevelConfig
import com.example.play_6sem.model.PlayerProgress
import com.example.play_6sem.ui.GlassCard
import com.example.play_6sem.ui.GradientBackground
import kotlin.math.roundToInt
import kotlin.random.Random
import kotlinx.coroutines.delay

private enum class MathMode { LEVELS, GAME }

@Composable
fun MathMarathonScreen(
    progress: PlayerProgress,
    onBack: () -> Unit,
    onCompleted: (points: Int, nextLevel: Int, bonusSec: Int) -> Unit
) {
    var mode by remember { mutableStateOf(MathMode.LEVELS) }
    var selectedLevel by remember { mutableIntStateOf(progress.mathLevel.coerceIn(1, 7)) }
    var runSeed by remember { mutableIntStateOf(0) }

    when (mode) {
        MathMode.LEVELS -> MathLevelMenu(
            unlockedLevel = progress.mathLevel.coerceAtLeast(1),
            onBack = onBack,
            onStart = { levelId ->
                selectedLevel = levelId
                runSeed++
                mode = MathMode.GAME
            }
        )

        MathMode.GAME -> MathLevelGame(
            levelId = selectedLevel,
            runSeed = runSeed,
            progress = progress,
            onBackToLevels = { mode = MathMode.LEVELS },
            onCompleted = onCompleted
        )
    }
}

@Composable
private fun MathLevelMenu(
    unlockedLevel: Int,
    onBack: () -> Unit,
    onStart: (Int) -> Unit
) {
    var selectedConfig by remember { mutableStateOf<MathLevelConfig?>(null) }

    GradientBackground {
        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedButton(onClick = onBack, modifier = Modifier.size(48.dp), contentPadding = PaddingValues(0.dp)) {
                        Text("←", style = MaterialTheme.typography.titleLarge)
                    }
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text("Математика", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
                        Text("Выберите доступный уровень")
                    }
                }
            }
            item {
                GlassCard {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        (1..7).chunked(3).forEach { row ->
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                                row.forEach { levelId ->
                                    val unlocked = levelId <= unlockedLevel
                                    Button(
                                        onClick = {
                                            if (unlocked) selectedConfig = GameContent.generateMathLevel(levelId)
                                        },
                                        enabled = unlocked,
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (unlocked) Color(0xFF5B8DEF) else Color(0xFFBFC7CE),
                                            disabledContainerColor = Color(0xFFBFC7CE)
                                        ),
                                        modifier = Modifier.weight(1f).height(72.dp)
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(levelId.toString(), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                                            Text(if (unlocked) "Доступен" else "Закрыт")
                                        }
                                    }
                                }
                                repeat(3 - row.size) { Spacer(Modifier.weight(1f)) }
                            }
                        }
                    }
                }
            }
        }
    }

    selectedConfig?.let { config ->
        AlertDialog(
            onDismissRequest = { selectedConfig = null },
            title = { Text("Уровень ${config.id}. ${config.title}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(config.description)
                    Text("Примеров: ${config.tasks.size}")
                    Text("Таймер: ${config.perTaskTimeSec} сек.")
                    Text("Пройти: ${config.requiredCorrect} правильных")
                }
            },
            confirmButton = {
                Button(onClick = {
                    selectedConfig = null
                    onStart(config.id)
                }) { Text("Играть") }
            },
            dismissButton = {
                OutlinedButton(onClick = { selectedConfig = null }) { Text("Закрыть") }
            }
        )
    }
}

@Composable
private fun MathLevelGame(
    levelId: Int,
    runSeed: Int,
    progress: PlayerProgress,
    onBackToLevels: () -> Unit,
    onCompleted: (points: Int, nextLevel: Int, bonusSec: Int) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var restartKey by remember(levelId, runSeed) { mutableIntStateOf(0) }
    val level = remember(levelId, runSeed, restartKey) { GameContent.generateMathLevel(levelId) }
    var idx by remember(levelId, runSeed, restartKey) { mutableIntStateOf(0) }
    var correct by remember(levelId, runSeed, restartKey) { mutableIntStateOf(0) }
    var errors by remember(levelId, runSeed, restartKey) { mutableIntStateOf(0) }
    var score by remember(levelId, runSeed, restartKey) { mutableIntStateOf(0) }
    var timeBonusSum by remember(levelId, runSeed, restartKey) { mutableIntStateOf(0) }
    var paused by remember(levelId, runSeed, restartKey) { mutableStateOf(false) }
    var showExitDialog by remember(levelId, runSeed, restartKey) { mutableStateOf(false) }
    var timeLeft by remember(levelId, runSeed, restartKey) { mutableIntStateOf(level.perTaskTimeSec + progress.bonusSeconds) }
    var input by remember(levelId, runSeed, restartKey) { mutableStateOf("") }
    var result by remember(levelId, runSeed, restartKey) { mutableStateOf("") }
    var awarded by remember(levelId, runSeed, restartKey) { mutableStateOf(false) }
    var isCorrectState by remember(levelId, runSeed, restartKey) { mutableStateOf<Boolean?>(null) }
    val inputShake = remember(levelId, runSeed, restartKey) { Animatable(0f) }
    val task = level.tasks.getOrNull(idx)
    val finished = task == null || errors >= level.lives
    val optionList = remember(task?.expression, task?.answer) {
        if (level.id == 7 || task == null || level.manualInput) {
            emptyList()
        } else {
            val options = mutableSetOf(task.answer)
            while (options.size < 4) {
                options += task.answer + Random.nextInt(-9, 10)
            }
            options.shuffled()
        }
    }
    val feedbackBg by animateColorAsState(
        targetValue = when (isCorrectState) {
            true -> Color(0xFFD6F5E4)
            false -> Color(0xFFFFE0E0)
            null -> Color.White
        },
        label = "math_input_feedback"
    )
    val feedbackBorder by animateColorAsState(
        targetValue = when (isCorrectState) {
            true -> Color(0xFF2A9D62)
            false -> Color(0xFFD84C4C)
            null -> Color(0xFF8BA7C4)
        },
        label = "math_input_border"
    )

    LaunchedEffect(isCorrectState) {
        if (isCorrectState == false) {
            repeat(3) {
                inputShake.animateTo(-8f, tween(45))
                inputShake.animateTo(8f, tween(45))
            }
            inputShake.animateTo(0f, tween(60))
        }
        if (isCorrectState != null) {
            delay(450)
            isCorrectState = null
        }
    }

    LaunchedEffect(idx, paused, finished) {
        timeLeft = level.perTaskTimeSec + progress.bonusSeconds
        while (!paused && timeLeft > 0 && !finished) {
            delay(1000)
            timeLeft--
        }
        if (timeLeft == 0 && !finished) {
            errors++
            score -= level.errorPenalty
            isCorrectState = false
            idx++
            input = ""
        }
    }

    fun handleAnswer(answer: Int) {
        val currentTask = task ?: return
        val ok = answer == currentTask.answer
        if (ok) {
            correct++
            val speedScore = (level.maxSpeedScore * (timeLeft.toFloat() / (level.perTaskTimeSec + progress.bonusSeconds).coerceAtLeast(1))).roundToInt()
            score += level.correctBonus + speedScore
            timeBonusSum += timeLeft
            isCorrectState = true
        } else {
            errors++
            score -= level.errorPenalty
            isCorrectState = false
        }
        idx++
        input = ""
    }

    fun resetLevel() {
        restartKey++
    }

    GradientBackground {
        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            item {
                LevelTopBar(
                    title = "Уровень ${level.id}: ${level.title}",
                    timerText = "Время: $timeLeft | Ошибки: $errors/${level.lives}",
                    onPause = { paused = true },
                    onReset = { showExitDialog = true }
                )
            }
            if (paused) {
                item {
                    PauseMenu(onContinue = { paused = false }, onReset = {
                        resetLevel()
                        paused = false
                    }, onExit = { showExitDialog = true })
                }
            }
            if (!finished) {
                item {
                    GlassCard {
                        Text("Задание ${idx + 1}/${level.tasks.size}", style = MaterialTheme.typography.titleMedium)
                        Text("${task?.expression ?: "..."} = ?", style = MaterialTheme.typography.headlineSmall)
                        Text("Очки: $score | Верно: $correct")
                    }
                }
                item {
                    GlassCard {
                        when {
                            level.id == 7 -> {
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                                    Button(onClick = { handleAnswer(1) }, modifier = Modifier.weight(1f)) { ButtonLabel("Верно") }
                                    OutlinedButton(onClick = { handleAnswer(0) }, modifier = Modifier.weight(1f)) { ButtonLabel("Неверно") }
                                }
                            }

                            !level.manualInput -> {
                                optionList.forEach { option ->
                                    Button(onClick = { handleAnswer(option) }, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                                        ButtonLabel(option.toString())
                                    }
                                }
                            }

                            else -> {
                                OutlinedTextField(
                                    value = input,
                                    onValueChange = { raw ->
                                        val normalized = raw.filterIndexed { index, char ->
                                            char.isDigit() || (char == '-' && index == 0)
                                        }
                                        input = normalized.take(9)
                                    },
                                    modifier = Modifier.fillMaxWidth().offset(x = inputShake.value.dp),
                                    label = { Text("Введите ответ") },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                                    keyboardActions = KeyboardActions(onDone = {
                                        handleAnswer(input.toIntOrNull() ?: Int.MIN_VALUE)
                                        keyboardController?.hide()
                                    }),
                                    colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = feedbackBg,
                                        unfocusedContainerColor = feedbackBg,
                                        focusedBorderColor = feedbackBorder,
                                        unfocusedBorderColor = feedbackBorder
                                    )
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedButton(onClick = { input = "" }, modifier = Modifier.weight(1f)) { ButtonLabel("Очистить") }
                                    Button(onClick = {
                                        handleAnswer(input.toIntOrNull() ?: Int.MIN_VALUE)
                                        keyboardController?.hide()
                                    }, modifier = Modifier.weight(1f)) { ButtonLabel("Проверить") }
                                }
                            }
                        }
                    }
                }
            } else {
                item {
                    val passed = correct >= level.requiredCorrect && (level.passScore == null || score >= level.passScore)
                    if (passed && !awarded) {
                        val reward = level.scoreOnPass + correct * level.correctBonus + timeBonusSum
                        val bonusTimer = if (level.id % 2 == 0) 2 else 1
                        onCompleted(reward, (level.id + 1).coerceAtMost(7), bonusTimer)
                        awarded = true
                        result = "Победа: +$reward очков, бонус таймера +$bonusTimer сек."
                    } else if (!passed && result.isBlank()) {
                        result = "Уровень не пройден. При повторе примеры будут новыми."
                    }
                    GlassCard {
                        Text(result, style = MaterialTheme.typography.titleMedium)
                        Text("Верно: $correct из ${level.tasks.size}, ошибок: $errors, очки: $score")
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                            Button(onClick = { resetLevel() }, modifier = Modifier.weight(1f)) { ButtonLabel("Повторить") }
                            OutlinedButton(onClick = onBackToLevels, modifier = Modifier.weight(1f)) { ButtonLabel("Меню уровней") }
                        }
                    }
                }
            }
        }
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("Выйти из уровня?") },
            text = { Text("Прогресс текущей попытки не сохранится. Можно продолжить или вернуться в меню уровней.") },
            confirmButton = {
                Button(onClick = {
                    showExitDialog = false
                    onBackToLevels()
                }) { Text("Выйти") }
            },
            dismissButton = {
                OutlinedButton(onClick = { showExitDialog = false }) { Text("Продолжить") }
            }
        )
    }
}
