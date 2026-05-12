package com.example.play_6sem.ui.modules

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.play_6sem.data.GameContent
import com.example.play_6sem.model.PlayerProgress
import com.example.play_6sem.model.WordLevelConfig
import com.example.play_6sem.model.WordStarRule
import com.example.play_6sem.ui.GlassCard
import com.example.play_6sem.ui.GradientBackground
import kotlinx.coroutines.delay

private enum class WordScreenMode { LEVELS, GAME }

@Composable
fun SlovomanScreen(
    progress: PlayerProgress,
    onBack: () -> Unit,
    onProgressChanged: (levelId: Int, foundWords: List<String>, stars: Int, unlockedLevel: Int, pointsDelta: Int) -> Unit
) {
    val activity = LocalContext.current as? Activity
    DisposableEffect(activity) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        onDispose {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    var mode by remember { mutableStateOf(WordScreenMode.LEVELS) }
    var selectedLevelId by remember { mutableIntStateOf(1) }
    var localProgress by remember(progress) { mutableStateOf(progress) }

    when (mode) {
        WordScreenMode.LEVELS -> WordLevelMenu(
            progress = localProgress,
            onBack = onBack,
            onStart = { levelId ->
                selectedLevelId = levelId
                mode = WordScreenMode.GAME
            }
        )

        WordScreenMode.GAME -> {
            val level = GameContent.wordLevels.firstOrNull { it.id == selectedLevelId } ?: GameContent.wordLevels.first()
            WordLevelGame(
                level = level,
                progress = localProgress,
                onExitToLevels = { mode = WordScreenMode.LEVELS },
                onNextLevel = {
                    selectedLevelId = (level.id + 1).coerceAtMost(GameContent.wordLevels.size)
                },
                onSave = { found, stars, pointsDelta ->
                    val unlockedLevel = if (stars > 0) (level.id + 1).coerceAtMost(GameContent.wordLevels.size) else localProgress.wordLevel
                    val nextProgress = localProgress.copy(
                        wordLevel = unlockedLevel.coerceAtLeast(localProgress.wordLevel),
                        wordFoundWords = localProgress.wordFoundWords + (level.id to found),
                        wordStars = localProgress.wordStars + (level.id to stars)
                    )
                    localProgress = nextProgress
                    onProgressChanged(level.id, found, stars, nextProgress.wordLevel, pointsDelta)
                }
            )
        }
    }
}

@Composable
private fun WordLevelMenu(
    progress: PlayerProgress,
    onBack: () -> Unit,
    onStart: (Int) -> Unit
) {
    var selectedLevel by remember { mutableStateOf<WordLevelConfig?>(null) }
    var notice by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(notice) {
        if (notice != null) {
            delay(1400)
            notice = null
        }
    }

    GradientBackground {
        Box(Modifier.fillMaxSize()) {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedButton(onClick = onBack, modifier = Modifier.size(48.dp)) {
                            Text("←", style = MaterialTheme.typography.titleLarge)
                        }
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text("Слово из слова", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
                            Text("Выберите доступный уровень")
                        }
                    }
                }
                item {
                    GlassCard {
                        Text("Рейтинг модуля: ${progress.wordStars.values.sum() * 5} очков")
                        Text("Следующий уровень открывается после 1 звезды.")
                    }
                }
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        GameContent.wordLevels.chunked(3).forEach { rowLevels ->
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                                rowLevels.forEach { level ->
                                    val stars = progress.wordStars[level.id] ?: 0
                                    val isUnlocked = level.id <= progress.wordLevel
                                    LevelButton(
                                        levelId = level.id,
                                        stars = stars,
                                        isUnlocked = isUnlocked,
                                        onClick = {
                                            if (isUnlocked) selectedLevel = level else notice = "Уровень недоступен"
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = notice != null,
                enter = fadeIn(tween(120)),
                exit = fadeOut(tween(200)),
                modifier = Modifier.align(Alignment.BottomCenter).padding(18.dp)
            ) {
                Text(
                    text = notice.orEmpty(),
                    color = Color.White,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFB53A3A))
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                )
            }
        }
    }

    if (selectedLevel != null) {
        val level = selectedLevel ?: return
        val stars = progress.wordStars[level.id] ?: 0
        AlertDialog(
            onDismissRequest = { selectedLevel = null },
            title = { Text("Уровень ${level.id}: ${level.mainWord}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    StarRow(stars = stars)
                    Text(level.ruleText)
                    Text("Найдено: ${progress.wordFoundWords[level.id]?.size ?: 0}/${level.words.size}")
                    level.starRules.forEach { Text("• ${it.title}") }
                }
            },
            confirmButton = {
                Button(onClick = {
                    selectedLevel = null
                    onStart(level.id)
                }) { Text("ИГРАТЬ") }
            },
            dismissButton = {
                OutlinedButton(onClick = { selectedLevel = null }) { Text("Закрыть") }
            }
        )
    }
}

@Composable
private fun LevelButton(
    levelId: Int,
    stars: Int,
    isUnlocked: Boolean,
    onClick: () -> Unit
) {
    val container = when {
        !isUnlocked -> Color(0xFFBFC7CE)
        stars > 0 -> Color(0xFF79D69B)
        else -> Color(0xFF5B8DEF)
    }
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = container, contentColor = Color.White),
        modifier = Modifier.size(width = 86.dp, height = 72.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(levelId.toString(), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            StarRow(stars = stars, small = true)
        }
    }
}

@Composable
private fun WordLevelGame(
    level: WordLevelConfig,
    progress: PlayerProgress,
    onExitToLevels: () -> Unit,
    onNextLevel: () -> Unit,
    onSave: (foundWords: List<String>, stars: Int, pointsDelta: Int) -> Unit
) {
    val startFound = progress.wordFoundWords[level.id].orEmpty().map { it.uppercase() }
    val found = remember(level.id) { mutableStateListOf<String>().apply { addAll(startFound) } }
    val selectedIndexes = remember(level.id) { mutableStateListOf<Int>() }
    val hintLetters = remember(level.id) { mutableStateMapOf<String, MutableSet<Int>>() }
    var message by remember(level.id) { mutableStateOf("Соберите слово из плиток") }
    var isErrorMessage by remember(level.id) { mutableStateOf(false) }
    var showMenu by remember(level.id) { mutableStateOf(false) }
    var showStarsInfo by remember(level.id) { mutableStateOf(false) }
    var showResult by remember(level.id) { mutableStateOf(false) }
    var resultPassed by remember(level.id) { mutableStateOf(false) }
    var hintsLeft by remember(level.id) { mutableIntStateOf(level.hintLimit) }
    var highlightWord by remember(level.id) { mutableStateOf<String?>(null) }
    var duplicateWord by remember(level.id) { mutableStateOf<String?>(null) }
    var colorIndex by remember(level.id) { mutableIntStateOf(0) }
    var savedStars by remember(level.id) { mutableIntStateOf(progress.wordStars[level.id] ?: 0) }
    val inputShake = remember(level.id) { Animatable(0f) }

    val currentWord = selectedIndexes.joinToString("") { level.mainWord[it].toString() }
    val stars = calculateStars(level, found)
    val progressValue = found.size.toFloat() / level.words.size.toFloat()
    val activeColor = wordColors[colorIndex % wordColors.size]

    fun saveIfNeeded(newStars: Int = stars) {
        val pointsDelta = (newStars - savedStars).coerceAtLeast(0) * 5
        savedStars = savedStars.coerceAtLeast(newStars)
        onSave(found.toList(), newStars, pointsDelta)
    }

    fun clearSelection(nextColor: Boolean = false) {
        selectedIndexes.clear()
        if (nextColor) colorIndex++
    }

    fun showDuplicate(word: String) {
        message = "Такое слово уже найдено"
        isErrorMessage = false
        highlightWord = word
        duplicateWord = word
    }

    LaunchedEffect(duplicateWord) {
        if (duplicateWord != null) {
            repeat(3) {
                inputShake.animateTo(-8f, tween(45))
                inputShake.animateTo(8f, tween(45))
            }
            inputShake.animateTo(0f, tween(60))
            delay(1000)
            highlightWord = null
            duplicateWord = null
        }
    }

    LaunchedEffect(stars) {
        if (stars > savedStars) {
            val wasNotPassed = savedStars == 0
            saveIfNeeded(stars)
            if (wasNotPassed) {
                resultPassed = true
                showResult = true
            }
        }
    }

    fun tryAutoSubmit() {
        val word = selectedIndexes.joinToString("") { level.mainWord[it].toString() }.uppercase()
        if (word.isBlank()) return
        if (found.contains(word)) {
            showDuplicate(word)
            return
        }
        if (level.words.any { it.uppercase() == word }) {
            val wasNotPassed = savedStars == 0
            found.add(word)
            message = "Найдено: $word"
            isErrorMessage = false
            clearSelection(nextColor = true)
            val newStars = calculateStars(level, found)
            saveIfNeeded(newStars)
            if (wasNotPassed && newStars > 0) {
                resultPassed = true
                showResult = true
            }
            return
        }
        val hasPossibleContinuation = level.words.any { candidate ->
            candidate.uppercase().startsWith(word) && candidate.length > word.length
        }
        if (!hasPossibleContinuation && word.length >= 3) {
            message = "Слово не найдено в словаре"
            isErrorMessage = true
        }
    }

    GradientBackground {
        Box(Modifier.fillMaxSize()) {
            LazyColumn(
                contentPadding = PaddingValues(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    GlassCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(Modifier.weight(1f)) {
                                FittedWordTitle(level.mainWord)
                                Text("Найдено: ${found.size}/${level.words.size}")
                            }
                            OutlinedButton(onClick = { showStarsInfo = true }, modifier = Modifier.heightIn(min = 42.dp)) {
                                StarRow(stars = stars, small = true)
                            }
                            Spacer(Modifier.width(8.dp))
                            Button(onClick = { showMenu = true }, modifier = Modifier.size(48.dp), contentPadding = PaddingValues(0.dp)) {
                                Text("☰", style = MaterialTheme.typography.titleLarge)
                            }
                        }
                        LinearProgressIndicator(progress = { progressValue.coerceIn(0f, 1f) }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
                    }
                }
                item {
                    WordTemplates(level = level, found = found, hintLetters = hintLetters, highlightWord = highlightWord)
                }
                item {
                    GlassCard {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                level.mainWord.toList().withIndex().forEach { indexedLetter ->
                                    val index = indexedLetter.index
                                    val letter = indexedLetter.value
                                    val selected = selectedIndexes.contains(index)
                                    OutlinedButton(
                                        enabled = !selected,
                                        onClick = {
                                            selectedIndexes.add(index)
                                            tryAutoSubmit()
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            containerColor = if (selected) activeColor else Color.White,
                                            contentColor = Color(0xFF14233A),
                                            disabledContainerColor = activeColor,
                                            disabledContentColor = Color.White
                                        ),
                                        modifier = Modifier.weight(1f).height(48.dp),
                                        contentPadding = PaddingValues(0.dp)
                                    ) {
                                        Text(letter.toString(), fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
                        ) {
                            OutlinedButton(
                                onClick = { if (selectedIndexes.isNotEmpty()) selectedIndexes.removeAt(selectedIndexes.lastIndex) },
                                modifier = Modifier.size(48.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) { Text("←") }
                            Button(
                                onClick = { clearSelection(nextColor = true) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD84C4C)),
                                modifier = Modifier.size(48.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) { Text("×", color = Color.White, fontWeight = FontWeight.Black) }
                            OutlinedButton(
                                onClick = {
                                    if (hintsLeft <= 0) {
                                        message = "Подсказки закончились"
                                        isErrorMessage = true
                                    } else {
                                        val target = level.words.firstOrNull { !found.contains(it.uppercase()) }
                                        if (target != null) {
                                            val opened = hintLetters.getOrPut(target) { mutableSetOf() }
                                            val nextIndex = target.indices.firstOrNull { !opened.contains(it) } ?: 0
                                            opened.add(nextIndex)
                                            hintsLeft--
                                            message = "Открыта буква в слове из ${target.length} букв"
                                            isErrorMessage = false
                                        }
                                    }
                                },
                                modifier = Modifier.size(48.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) { Text("💡") }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .offset(x = inputShake.value.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(
                                        width = if (duplicateWord == currentWord && currentWord.isNotBlank()) 3.dp else 1.dp,
                                        color = if (duplicateWord == currentWord && currentWord.isNotBlank()) Color(0xFFFFC83D) else Color(0xFF8BA7C4),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .background(Color.White),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(currentWord.ifBlank { "Поле ввода" }, color = if (currentWord.isBlank()) Color(0xFF8BA7C4) else Color(0xFF14233A))
                            }
                        }
                        Text("Подсказки: $hintsLeft/${level.hintLimit}", modifier = Modifier.padding(top = 6.dp))
                    }
                }
            }

            AnimatedVisibility(
                visible = message.isNotBlank(),
                enter = fadeIn(tween(120)),
                exit = fadeOut(tween(200)),
                modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp)
            ) {
                Text(
                    text = message,
                    color = if (isErrorMessage) Color.White else Color(0xFF14324C),
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isErrorMessage) Color(0xFFB53A3A) else Color(0xFFD8F8E4))
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                )
            }
        }
    }

    if (showMenu) {
        AlertDialog(
            onDismissRequest = { showMenu = false },
            title = { Text("Меню уровня") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    Text("Выберите действие")
                    Button(onClick = { showMenu = false }, modifier = Modifier.fillMaxWidth()) {
                        Text("Продолжить")
                    }
                    OutlinedButton(
                        onClick = {
                            showMenu = false
                            resultPassed = stars > 0
                            showResult = true
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Завершить уровень") }
                    OutlinedButton(
                        onClick = {
                            saveIfNeeded()
                            showMenu = false
                            onExitToLevels()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("В меню уровней") }
                }
            },
            confirmButton = {},
            dismissButton = {}
        )
    }

    if (showStarsInfo) {
        AlertDialog(
            onDismissRequest = { showStarsInfo = false },
            title = { Text("Условия звезд") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    level.starRules.forEachIndexed { index, rule ->
                        Text("${index + 1}. ${rule.title}")
                    }
                    Text("Каждая звезда дает 5 очков рейтинга.")
                }
            },
            confirmButton = { Button(onClick = { showStarsInfo = false }) { Text("Понятно") } }
        )
    }

    if (showResult) {
        WordResultDialog(
            passed = resultPassed,
            stars = stars,
            hasNextLevel = level.id < GameContent.wordLevels.size && stars > 0,
            onRetry = {
                found.clear()
                clearSelection()
                showResult = false
                message = "Попробуйте снова"
                isErrorMessage = false
                saveIfNeeded(0)
            },
            onContinue = { showResult = false },
            onNext = {
                saveIfNeeded()
                showResult = false
                onNextLevel()
            },
            onLevels = {
                saveIfNeeded()
                showResult = false
                onExitToLevels()
            }
        )
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun WordTemplates(
    level: WordLevelConfig,
    found: List<String>,
    hintLetters: Map<String, Set<Int>>,
    highlightWord: String?
) {
    GlassCard {
        level.words.sortedWith(compareBy<String> { it.length }.thenBy { it }).groupBy { it.length }.forEach { (length, words) ->
            Text("$length букв", fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                words.forEach { word ->
                    val upper = word.uppercase()
                    val isFound = found.contains(upper)
                    val isHighlighted = highlightWord == upper
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isHighlighted) Color(0xFFFFE28A) else Color.Transparent)
                            .padding(2.dp),
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        upper.forEachIndexed { index, letter ->
                            val hinted = hintLetters[upper]?.contains(index) == true
                            Box(
                                modifier = Modifier
                                    .size(25.dp)
                                    .clip(RoundedCornerShape(5.dp))
                                    .background(if (isFound) Color(0xFF7E8FF2) else Color.White)
                                    .border(1.dp, Color(0xFFC1CEE0), RoundedCornerShape(5.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (isFound || hinted) letter.toString() else "",
                                    color = if (isFound) Color.White else Color(0xFF243B5A),
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
@Composable
private fun WordResultDialog(
    passed: Boolean,
    stars: Int,
    hasNextLevel: Boolean,
    onRetry: () -> Unit,
    onContinue: () -> Unit,
    onNext: () -> Unit,
    onLevels: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onContinue,
        title = { Text(if (passed) "Уровень пройден" else "Уровень не пройден") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                StarRow(stars = stars)
                Text(if (passed) "Получено звезд: $stars. Можно продолжить собирать слова или перейти дальше." else "Нужно получить хотя бы одну звезду.")
            }
        },
        confirmButton = {
            if (passed && hasNextLevel) {
                Button(onClick = onNext) { Text("Следующий уровень") }
            } else if (passed) {
                Button(onClick = onLevels) { Text("Меню уровней") }
            } else {
                Button(onClick = onRetry) { Text("Попробовать снова") }
            }
        },
        dismissButton = {
            Column {
                if (passed) OutlinedButton(onClick = onContinue) { Text("Продолжить собирать") }
                OutlinedButton(onClick = onLevels, modifier = Modifier.padding(top = 6.dp)) { Text("Меню уровней") }
            }
        }
    )
}

@Composable
private fun StarRow(stars: Int, small: Boolean = false) {
    val style = if (small) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.titleLarge
    Row(horizontalArrangement = Arrangement.spacedBy(if (small) 1.dp else 4.dp)) {
        repeat(3) { index ->
            Text(
                text = "★",
                style = style,
                color = if (index < stars) Color(0xFFFFC83D) else Color(0xFFB7C0CB)
            )
        }
    }
}

@Composable
private fun FittedWordTitle(word: String) {
    val size: TextUnit = when {
        word.length >= 11 -> 26.sp
        word.length >= 9 -> 30.sp
        else -> 36.sp
    }
    Text(
        text = word,
        fontSize = size,
        lineHeight = size,
        fontWeight = FontWeight.Black,
        maxLines = 1,
        softWrap = false,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.fillMaxWidth()
    )
}

private fun calculateStars(level: WordLevelConfig, found: List<String>): Int {
    return level.starRules.count { rule -> rule.isCompleted(found) }
}

private fun WordStarRule.isCompleted(found: List<String>): Boolean {
    foundCount?.let { if (found.size >= it) return true }
    val letter = startsWith
    val count = startsWithCount
    if (letter != null && count != null) {
        return found.count { it.firstOrNull()?.uppercaseChar() == letter.uppercaseChar() } >= count
    }
    return false
}

private val wordColors = listOf(
    Color(0xFF5B8DEF),
    Color(0xFF41B883),
    Color(0xFFE68A4A),
    Color(0xFF9B6ADE),
    Color(0xFFE05E8A)
)
