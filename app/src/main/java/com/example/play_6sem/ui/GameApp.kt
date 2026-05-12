package com.example.play_6sem.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import com.example.play_6sem.data.FileGameStorage
import com.example.play_6sem.model.AppRating
import com.example.play_6sem.model.GameModule
import com.example.play_6sem.model.PlayerProgress
import com.example.play_6sem.ui.theme.BerryJam
import com.example.play_6sem.ui.theme.Caramel
import com.example.play_6sem.ui.theme.Cocoa
import com.example.play_6sem.ui.theme.CookieCream
import com.example.play_6sem.ui.theme.CookieSurface
import com.example.play_6sem.ui.theme.DarkChocolate
import com.example.play_6sem.ui.theme.MilkFoam
import com.example.play_6sem.ui.theme.ToastedSugar
import com.example.play_6sem.ui.modules.DailyChallengeScreen
import com.example.play_6sem.ui.modules.DetectiveScreen
import com.example.play_6sem.ui.modules.MathMarathonScreen
import com.example.play_6sem.ui.modules.SlovomanScreen
import java.time.LocalDate
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private enum class AppScreen {
    SPLASH, MENU, WORD, MATH, DETECTIVE, DAILY
}

private enum class NoticeType { ERROR, SUCCESS }

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun GameApp() {
    val context = LocalContext.current
    val storage = remember { FileGameStorage(context) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var noticeType by remember { mutableStateOf(NoticeType.SUCCESS) }
    var errorDialogMessage by remember { mutableStateOf<String?>(null) }
    var retryAction by remember { mutableStateOf<(() -> Unit)?>(null) }

    var screen by remember { mutableStateOf(AppScreen.SPLASH) }
    var rating by remember { mutableStateOf(storage.readRating()) }
    var progress by remember { mutableStateOf(storage.readProgress()) }

    fun popNotice(message: String, type: NoticeType) {
        noticeType = type
        scope.launch { snackbarHostState.showSnackbar(message) }
    }

    Scaffold(
        containerColor = Color.Transparent,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = if (noticeType == NoticeType.ERROR) Color(0xFFFFD9D9) else Color(0xFFD8F8E4),
                    contentColor = Color(0xFF14324C)
                )
            }
        }
    ) {
        when (screen) {
            AppScreen.SPLASH -> SplashScreen(onDone = { screen = AppScreen.MENU })
            AppScreen.MENU -> MainMenuScreen(
                rating = rating,
                progress = progress,
                onOpen = { module ->
                    screen = when (module) {
                        GameModule.WORD -> AppScreen.WORD
                        GameModule.MATH -> AppScreen.MATH
                        GameModule.DETECTIVE -> AppScreen.DETECTIVE
                        GameModule.DAILY -> AppScreen.DAILY
                    }
                }
            )

            AppScreen.WORD -> SlovomanScreen(
                progress = progress,
                onBack = {
                    rating = storage.readRating()
                    progress = storage.readProgress()
                    screen = AppScreen.MENU
                },
                onProgressChanged = { levelId, foundWords, stars, unlockedLevel, pointsDelta ->
                    runCatching {
                        if (pointsDelta > 0) storage.addPoints(GameModule.WORD, pointsDelta)
                        val current = storage.readProgress()
                        storage.writeProgress(
                            current.copy(
                                wordLevel = unlockedLevel.coerceAtLeast(current.wordLevel),
                                wordFoundWords = current.wordFoundWords + (levelId to foundWords),
                                wordStars = current.wordStars + (levelId to stars)
                            )
                        )
                        rating = storage.readRating()
                        progress = storage.readProgress()
                        if (pointsDelta > 0) {
                            popNotice("Словоман: +$pointsDelta очков", NoticeType.SUCCESS)
                        }
                    }.onFailure {
                        errorDialogMessage = "Ошибка сохранения Словомана: ${it.message}"
                        retryAction = {
                            if (pointsDelta > 0) storage.addPoints(GameModule.WORD, pointsDelta)
                            storage.writeProgress(
                                progress.copy(
                                    wordLevel = unlockedLevel.coerceAtLeast(progress.wordLevel),
                                    wordFoundWords = progress.wordFoundWords + (levelId to foundWords),
                                    wordStars = progress.wordStars + (levelId to stars)
                                )
                            )
                        }
                        popNotice("Ошибка сохранения Словомана", NoticeType.ERROR)
                    }
                }
            )

            AppScreen.MATH -> MathMarathonScreen(
                progress = progress,
                onBack = {
                    rating = storage.readRating()
                    progress = storage.readProgress()
                    screen = AppScreen.MENU
                },
                onCompleted = { points, nextLevel, bonusSec ->
                    runCatching {
                        storage.addPoints(GameModule.MATH, points)
                        storage.writeProgress(
                            progress.copy(
                                mathLevel = nextLevel.coerceAtLeast(progress.mathLevel),
                                bonusSeconds = (progress.bonusSeconds + bonusSec).coerceAtMost(30)
                            )
                        )
                        rating = storage.readRating()
                        progress = storage.readProgress()
                        popNotice("Math: прогресс сохранен", NoticeType.SUCCESS)
                    }.onFailure {
                        errorDialogMessage = "Ошибка сохранения математики: ${it.message}"
                        retryAction = {
                            storage.addPoints(GameModule.MATH, points)
                            storage.writeProgress(
                                progress.copy(
                                    mathLevel = nextLevel.coerceAtLeast(progress.mathLevel),
                                    bonusSeconds = (progress.bonusSeconds + bonusSec).coerceAtMost(30)
                                )
                            )
                        }
                        popNotice("Ошибка сохранения математики", NoticeType.ERROR)
                    }
                }
            )

            AppScreen.DETECTIVE -> DetectiveScreen(
                progress = progress,
                onBack = {
                    rating = storage.readRating()
                    progress = storage.readProgress()
                    screen = AppScreen.MENU
                },
                onCompleted = { points, nextCase ->
                    runCatching {
                        storage.addPoints(GameModule.DETECTIVE, points)
                        storage.writeProgress(progress.copy(detectiveCase = nextCase.coerceAtLeast(progress.detectiveCase)))
                        rating = storage.readRating()
                        progress = storage.readProgress()
                        popNotice("Детектив: дело сохранено", NoticeType.SUCCESS)
                    }.onFailure {
                        errorDialogMessage = "Ошибка сохранения детектива: ${it.message}"
                        retryAction = {
                            storage.addPoints(GameModule.DETECTIVE, points)
                            storage.writeProgress(progress.copy(detectiveCase = nextCase.coerceAtLeast(progress.detectiveCase)))
                        }
                        popNotice("Ошибка сохранения детектива", NoticeType.ERROR)
                    }
                }
            )

            AppScreen.DAILY -> DailyChallengeScreen(
                progress = progress,
                onBack = {
                    rating = storage.readRating()
                    progress = storage.readProgress()
                    screen = AppScreen.MENU
                },
                onAward = { points, bonusSec ->
                    runCatching {
                        storage.addPoints(GameModule.DAILY, points)
                        storage.writeProgress(
                            progress.copy(
                                bonusSeconds = (progress.bonusSeconds + bonusSec).coerceAtMost(45),
                                dailyCompletedDate = LocalDate.now().toString()
                            )
                        )
                        rating = storage.readRating()
                        progress = storage.readProgress()
                        popNotice("Daily: награда выдана", NoticeType.SUCCESS)
                    }.onFailure {
                        errorDialogMessage = "Ошибка сохранения daily: ${it.message}"
                        retryAction = {
                            storage.addPoints(GameModule.DAILY, points)
                            storage.writeProgress(
                                progress.copy(
                                    bonusSeconds = (progress.bonusSeconds + bonusSec).coerceAtMost(45),
                                    dailyCompletedDate = LocalDate.now().toString()
                                )
                            )
                        }
                        popNotice("Ошибка сохранения daily", NoticeType.ERROR)
                    }
                }
            )
        }

        if (errorDialogMessage != null) {
            AlertDialog(
                onDismissRequest = { errorDialogMessage = null },
                title = { Text("Ошибка") },
                text = { Text(errorDialogMessage ?: "") },
                confirmButton = {
                    Button(onClick = {
                        runCatching { retryAction?.invoke() }
                            .onSuccess {
                                popNotice("Повтор выполнен успешно", NoticeType.SUCCESS)
                                errorDialogMessage = null
                            }
                            .onFailure { popNotice("Повтор не удался", NoticeType.ERROR) }
                    }) { Text("Повторить") }
                },
                dismissButton = {
                    Button(onClick = { errorDialogMessage = null }) { Text("Закрыть") }
                }
            )
        }
    }
}

@Composable
private fun SplashScreen(onDone: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(2600)
        onDone()
    }
    GradientBackground {
        val transition = rememberInfiniteTransition(label = "splash")
        val scale = transition.animateFloat(
            initialValue = 0.98f,
            targetValue = 1.02f,
            animationSpec = infiniteRepeatable(animation = tween(1300), repeatMode = RepeatMode.Reverse),
            label = "splash_scale"
        )
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding((8 * scale.value).dp)) {
                Text("MIND ARENA", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Black)
                Text("Words • Math • Logic", color = Color(0xFF3A6D9D))
            }
        }
    }
}

@Composable
private fun MainMenuScreen(
    rating: AppRating,
    progress: PlayerProgress,
    onOpen: (GameModule) -> Unit
) {
    GradientBackground {
        Scaffold(containerColor = Color.Transparent, modifier = Modifier.safeDrawingPadding()) { padding ->
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(padding)
            ) {
                item {
                    GlassCard {
                        Text("Общий рейтинг: ${rating.totalPoints}", style = MaterialTheme.typography.headlineSmall)
                        Text(
                            "Словоман ${rating.moduleRatings[0].points} • Math ${rating.moduleRatings[1].points} • Детектив ${rating.moduleRatings[2].points} • Daily ${rating.moduleRatings[3].points}"
                        )
                        Text("Прогресс: W${progress.wordLevel} / M${progress.mathLevel} / D${progress.detectiveCase}")
                        Text("Бонус таймера для математики: +${progress.bonusSeconds} сек")
                    }
                }
                item { AnimatedVisibility(visible = true, enter = fadeIn() + slideInVertically()) { ModuleCard("Словоман", "Собирайте слова из букв", onClick = { onOpen(GameModule.WORD) }) } }
                item { AnimatedVisibility(visible = true, enter = fadeIn() + slideInVertically()) { ModuleCard("Math Marathon", "Быстрые примеры с таймером", onClick = { onOpen(GameModule.MATH) }) } }
                item { AnimatedVisibility(visible = true, enter = fadeIn() + slideInVertically()) { ModuleCard("Детектив", "Логические загадки и дела", onClick = { onOpen(GameModule.DETECTIVE) }) } }
                item { AnimatedVisibility(visible = true, enter = fadeIn() + slideInVertically()) { ModuleCard("Daily Challenge", "Ежедневный вызов и бонусы", onClick = { onOpen(GameModule.DAILY) }) } }
            }
        }
    }
}

@Composable
private fun ModuleCard(title: String, subtitle: String, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = CookieSurface.copy(alpha = 0.98f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(subtitle, color = Color(0xFF53779E))
            }
            Button(onClick = onClick) {
                Text("Играть", maxLines = 1, softWrap = false, overflow = TextOverflow.Ellipsis, fontSize = 14.sp, color = MilkFoam)
            }
        }
    }
}

@Composable
fun GlassCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 7.dp),
        colors = CardDefaults.cardColors(containerColor = CookieSurface.copy(alpha = 0.97f))
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp), content = content)
    }
}

@Composable
fun GradientBackground(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(CookieCream, Color(0xFFFFE7BA), ToastedSugar.copy(alpha = 0.72f))
                )
            )
    ) { content() }
}
