package com.example.play_6sem.ui.modules

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.play_6sem.model.PlayerProgress
import com.example.play_6sem.ui.GlassCard
import com.example.play_6sem.ui.GradientBackground
import java.time.LocalDate

@Composable
fun DailyChallengeScreen(
    progress: PlayerProgress,
    onBack: () -> Unit,
    onAward: (points: Int, bonusSec: Int) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val today = remember { LocalDate.now().toString() }
    val daySeed = remember { LocalDate.now().dayOfYear }
    val type = remember(daySeed) { if (daySeed % 2 == 0) "math" else "word" }
    val alreadyCompleted = progress.dailyCompletedDate == today
    var answer by remember { mutableStateOf("") }
    var tries by remember { mutableIntStateOf(0) }
    var done by remember { mutableStateOf(false) }
    var text by remember { mutableStateOf("Сегодняшний вызов готов") }
    var successState by remember { mutableStateOf<Boolean?>(null) }
    var hintsLeft by remember { mutableIntStateOf(2) }
    val inputShake = remember { Animatable(0f) }
    val feedbackBg by animateColorAsState(
        targetValue = when (successState) {
            true -> Color(0xFFD6F5E4)
            false -> Color(0xFFFFE0E0)
            null -> Color(0xFFEAF6FF)
        },
        label = "daily_feedback"
    )
    val borderColor by animateColorAsState(
        targetValue = when (successState) {
            true -> Color(0xFF2A9D62)
            false -> Color(0xFFD84C4C)
            null -> Color(0xFF8BA7C4)
        },
        label = "daily_border"
    )

    LaunchedEffect(successState) {
        if (successState == false) {
            repeat(3) {
                inputShake.animateTo(-8f, tween(45))
                inputShake.animateTo(8f, tween(45))
            }
            inputShake.animateTo(0f, tween(60))
        }
    }

    fun checkAnswer() {
        if (done || alreadyCompleted) return
        tries++
        val ok = if (type == "math") {
            answer.toIntOrNull() == 42
        } else {
            answer.equals("СИЛА", ignoreCase = true)
        }
        if (ok) {
            done = true
            val points = if (type == "math") 25 else 20
            val bonus = if (type == "math") 3 else 2
            onAward(points, bonus)
            text = "Успех! +$points очков Daily, +$bonus сек к таймеру математики."
            successState = true
        } else {
            text = "Пока неверно. Попытка $tries"
            successState = false
        }
        keyboardController?.hide()
    }

    GradientBackground {
        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            item {
                GlassCard {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("Daily Challenge", style = MaterialTheme.typography.titleLarge)
                            Text("Тип: ${if (type == "math") "Математика" else "Слово"}")
                        }
                        OutlinedButton(onClick = onBack) { ButtonLabel("В меню") }
                    }
                }
            }
            if (alreadyCompleted) {
                item {
                    GlassCard {
                        Text("Челлендж уже пройден сегодня", style = MaterialTheme.typography.titleLarge)
                        Text("Возвращайтесь завтра за новым заданием и бонусом.")
                    }
                }
            } else {
                item {
                    GlassCard {
                        Text("Правило и бонус", style = MaterialTheme.typography.titleLarge)
                        if (type == "math") {
                            Text("Решите: 27 + 15 = ?")
                            Text("Бонус: +25 очков Daily")
                            Text("Дополнительное время: +3 сек к таймеру математики")
                        } else {
                            Text("Соберите слово из букв Л И С А: ответ из 4 букв")
                            Text("Бонус: +20 очков Daily")
                            Text("Дополнительное время: +2 сек к таймеру математики")
                        }
                        Text("Ваш бонус таймера сейчас: +${progress.bonusSeconds} сек")
                        OutlinedButton(
                            onClick = {
                                if (hintsLeft <= 0) {
                                    text = "Подсказки закончились"
                                    successState = false
                                } else {
                                    hintsLeft--
                                    text = if (type == "math") {
                                        "Подсказка: сумма больше 40 и меньше 45"
                                    } else {
                                        "Подсказка: слово начинается на С"
                                    }
                                    successState = null
                                }
                            },
                            modifier = Modifier.padding(top = 6.dp)
                        ) { ButtonLabel("Подсказка ($hintsLeft)") }
                    }
                }
                item {
                    GlassCard {
                        OutlinedTextField(
                            value = answer,
                            onValueChange = {
                                answer = if (type == "math") {
                                    it.filterIndexed { index, char -> char.isDigit() || (char == '-' && index == 0) }.take(8)
                                } else {
                                    it.uppercase().take(6)
                                }
                            },
                            modifier = Modifier.fillMaxWidth().offset(x = inputShake.value.dp),
                            label = { Text(if (type == "math") "Введите число" else "Введите слово") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = if (type == "math") KeyboardType.Number else KeyboardType.Text,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(onDone = { checkAnswer() }),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = feedbackBg,
                                unfocusedContainerColor = feedbackBg,
                                focusedBorderColor = borderColor,
                                unfocusedBorderColor = borderColor
                            )
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(onClick = { answer = "" }, modifier = Modifier.weight(1f)) { ButtonLabel("Очистить") }
                            Button(onClick = { checkAnswer() }, modifier = Modifier.weight(1f)) { ButtonLabel("Проверить") }
                        }
                        Text(
                            text,
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .fillMaxWidth()
                                .background(feedbackBg)
                                .padding(8.dp)
                        )
                    }
                }
            }
        }
    }
}
