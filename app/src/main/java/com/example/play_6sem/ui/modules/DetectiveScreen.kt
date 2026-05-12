package com.example.play_6sem.ui.modules

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.play_6sem.data.GameContent
import com.example.play_6sem.model.PlayerProgress
import com.example.play_6sem.ui.GlassCard
import com.example.play_6sem.ui.GradientBackground

@Composable
fun DetectiveScreen(
    progress: PlayerProgress,
    onBack: () -> Unit,
    onCompleted: (points: Int, nextCase: Int) -> Unit
) {
    var currentCaseId by remember { mutableIntStateOf(progress.detectiveCase.coerceIn(1, GameContent.detectiveCases.size)) }
    val caseIndex = (currentCaseId - 1).coerceIn(0, GameContent.detectiveCases.lastIndex)
    val detectiveCase = GameContent.detectiveCases[caseIndex]
    var selected by remember(detectiveCase.id) { mutableIntStateOf(-1) }
    var feedback by remember(detectiveCase.id) { mutableStateOf("Выберите правильную версию") }
    var awarded by remember(detectiveCase.id) { mutableStateOf(false) }
    var paused by remember(detectiveCase.id) { mutableStateOf(false) }
    var isCorrectState by remember(detectiveCase.id) { mutableStateOf<Boolean?>(null) }
    var hintsLeft by remember(detectiveCase.id) { mutableIntStateOf(2) }
    var caseSolved by remember(detectiveCase.id) { mutableStateOf(false) }
    val feedbackBg by animateColorAsState(
        targetValue = when (isCorrectState) {
            true -> Color(0xFFD6F5E4)
            false -> Color(0xFFFFE0E0)
            null -> Color(0xFFEAF6FF)
        },
        label = "detective_feedback"
    )

    GradientBackground {
        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            item {
                LevelTopBar(
                    title = "Детектив: дело ${detectiveCase.id}",
                    timerText = null,
                    onPause = { paused = true },
                    onReset = {
                        selected = -1
                        feedback = "Уровень сброшен"
                        awarded = false
                        caseSolved = false
                    }
                )
            }
            if (paused) {
                item {
                    PauseMenu(
                        onContinue = { paused = false },
                        onReset = {
                            selected = -1
                            feedback = "Уровень сброшен"
                            awarded = false
                            caseSolved = false
                            paused = false
                        },
                        onExit = onBack
                    )
                }
            }
            item {
                GlassCard {
                    Text(detectiveCase.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
                    Text(
                        detectiveCase.story,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    )
                }
            }
            item {
                GlassCard {
                    Text("Кто виноват?", style = MaterialTheme.typography.titleLarge)
                    detectiveCase.options.forEachIndexed { index, option ->
                        val isSelected = selected == index
                        Button(
                            onClick = { selected = index },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) Color(0xFF2A9D62) else Color(0xFF5B8DEF),
                                contentColor = Color.White
                            ),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        ) {
                            ButtonLabel(if (isSelected) "✓ $option" else option)
                        }
                    }
                    OutlinedButton(onClick = {
                        if (hintsLeft <= 0) {
                            feedback = "Подсказки закончились"
                        } else {
                            hintsLeft--
                            feedback = "Подсказка: ищите противоречие в показаниях и лишние детали."
                        }
                    }, modifier = Modifier.padding(top = 6.dp)) { ButtonLabel("Подсказка ($hintsLeft)") }
                    OutlinedButton(onClick = {
                        if (selected == detectiveCase.correctIndex) {
                            feedback = "Верно! ${detectiveCase.explanation}"
                            isCorrectState = true
                            caseSolved = true
                            if (!awarded) {
                                val nextId = (detectiveCase.id + 1).coerceAtMost(GameContent.detectiveCases.size)
                                onCompleted(detectiveCase.scoreOnPass, nextId)
                                awarded = true
                            }
                        } else {
                            feedback = "Неверно. ${detectiveCase.explanation}"
                            isCorrectState = false
                        }
                    }, modifier = Modifier.padding(top = 6.dp)) { ButtonLabel("Проверить") }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 72.dp)
                            .padding(top = 8.dp)
                            .background(feedbackBg)
                            .padding(12.dp)
                    ) {
                        Text(feedback, color = Color(0xFF3A2418), style = MaterialTheme.typography.bodyLarge)
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                        if (caseSolved && detectiveCase.id < GameContent.detectiveCases.size) {
                            Button(
                                onClick = {
                                    currentCaseId = detectiveCase.id + 1
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) { ButtonLabel("Следующее дело") }
                        }
                        OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) { ButtonLabel("В меню") }
                    }
                }
            }
        }
    }
}
