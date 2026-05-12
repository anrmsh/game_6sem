package com.example.play_6sem.model

enum class GameModule(val title: String) {
    WORD("Словоман"),
    MATH("Math Marathon"),
    DETECTIVE("Детектив"),
    DAILY("Daily Challenge")
}

data class ModuleRating(
    val module: GameModule,
    val points: Int
)

data class AppRating(
    val moduleRatings: List<ModuleRating>,
    val totalPoints: Int
)

data class PlayerProgress(
    val wordLevel: Int = 1,
    val mathLevel: Int = 1,
    val detectiveCase: Int = 1,
    val bonusSeconds: Int = 0,
    val dailyCompletedDate: String? = null,
    val wordFoundWords: Map<Int, List<String>> = emptyMap(),
    val wordStars: Map<Int, Int> = emptyMap()
)

data class WordStarRule(
    val title: String,
    val foundCount: Int? = null,
    val startsWith: Char? = null,
    val startsWithCount: Int? = null
)

data class WordLevelConfig(
    val id: Int,
    val mainWord: String,
    val words: List<String>,
    val starRules: List<WordStarRule>,
    val ruleText: String,
    val hintLimit: Int = 2
)

data class MathTask(
    val expression: String,
    val answer: Int,
    val shownAnswer: Int? = null
)

data class MathLevelConfig(
    val id: Int,
    val title: String,
    val description: String = "",
    val tasks: List<MathTask>,
    val perTaskTimeSec: Int,
    val lives: Int,
    val manualInput: Boolean,
    val requiredCorrect: Int,
    val scoreOnPass: Int,
    val maxSpeedScore: Int = 40,
    val correctBonus: Int = 10,
    val errorPenalty: Int = 20,
    val passScore: Int? = null
)

data class DetectiveCase(
    val id: Int,
    val title: String,
    val story: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String,
    val scoreOnPass: Int
)
