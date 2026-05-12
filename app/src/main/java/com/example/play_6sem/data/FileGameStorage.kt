package com.example.play_6sem.data

import android.content.Context
import com.example.play_6sem.model.AppRating
import com.example.play_6sem.model.GameModule
import com.example.play_6sem.model.ModuleRating
import com.example.play_6sem.model.PlayerProgress
import org.json.JSONArray
import org.json.JSONObject

class FileGameStorage(private val context: Context) {
    private val ratingsFile = "ratings.json"
    private val progressFile = "progress.json"

    fun readRating(): AppRating {
        val text = readText(ratingsFile)
        if (text.isNullOrBlank()) {
            return defaultRating()
        }
        return try {
            val json = JSONObject(text)
            val word = json.optInt("word", 0)
            val math = json.optInt("math", 0)
            val detective = json.optInt("detective", 0)
            val daily = json.optInt("daily", 0)
            val total = word + math + detective + daily
            AppRating(
                moduleRatings = listOf(
                    ModuleRating(GameModule.WORD, word),
                    ModuleRating(GameModule.MATH, math),
                    ModuleRating(GameModule.DETECTIVE, detective),
                    ModuleRating(GameModule.DAILY, daily)
                ),
                totalPoints = total
            )
        } catch (_: Exception) {
            defaultRating()
        }
    }

    fun addPoints(module: GameModule, points: Int) {
        val current = readRating()
        val map = current.moduleRatings.associate { it.module to it.points }.toMutableMap()
        map[module] = (map[module] ?: 0) + points
        val json = JSONObject().apply {
            put("word", map[GameModule.WORD] ?: 0)
            put("math", map[GameModule.MATH] ?: 0)
            put("detective", map[GameModule.DETECTIVE] ?: 0)
            put("daily", map[GameModule.DAILY] ?: 0)
        }
        writeText(ratingsFile, json.toString())
    }

    fun readProgress(): PlayerProgress {
        val text = readText(progressFile)
        if (text.isNullOrBlank()) {
            return PlayerProgress()
        }
        return try {
            val json = JSONObject(text)
            PlayerProgress(
                wordLevel = json.optInt("wordLevel", 1),
                mathLevel = json.optInt("mathLevel", 1),
                detectiveCase = json.optInt("detectiveCase", 1),
                bonusSeconds = json.optInt("bonusSeconds", 0),
                dailyCompletedDate = json.optString("dailyCompletedDate", "").takeIf { it.isNotBlank() },
                wordFoundWords = readWordFound(json.optJSONObject("wordFoundWords")),
                wordStars = readWordStars(json.optJSONObject("wordStars"))
            )
        } catch (_: Exception) {
            PlayerProgress()
        }
    }

    fun writeProgress(progress: PlayerProgress) {
        val json = JSONObject().apply {
            put("wordLevel", progress.wordLevel)
            put("mathLevel", progress.mathLevel)
            put("detectiveCase", progress.detectiveCase)
            put("bonusSeconds", progress.bonusSeconds)
            put("dailyCompletedDate", progress.dailyCompletedDate ?: "")
            put("wordFoundWords", JSONObject().apply {
                progress.wordFoundWords.forEach { (levelId, words) ->
                    put(levelId.toString(), JSONArray(words))
                }
            })
            put("wordStars", JSONObject().apply {
                progress.wordStars.forEach { (levelId, stars) ->
                    put(levelId.toString(), stars)
                }
            })
        }
        writeText(progressFile, json.toString())
    }

    private fun readWordFound(json: JSONObject?): Map<Int, List<String>> {
        if (json == null) return emptyMap()
        val result = mutableMapOf<Int, List<String>>()
        json.keys().forEach { key ->
            val wordsJson = json.optJSONArray(key) ?: JSONArray()
            val words = (0 until wordsJson.length()).mapNotNull { index ->
                wordsJson.optString(index).takeIf { it.isNotBlank() }
            }
            result[key.toIntOrNull() ?: return@forEach] = words
        }
        return result
    }

    private fun readWordStars(json: JSONObject?): Map<Int, Int> {
        if (json == null) return emptyMap()
        val result = mutableMapOf<Int, Int>()
        json.keys().forEach { key ->
            result[key.toIntOrNull() ?: return@forEach] = json.optInt(key, 0)
        }
        return result
    }

    private fun defaultRating(): AppRating {
        return AppRating(
            moduleRatings = listOf(
                ModuleRating(GameModule.WORD, 0),
                ModuleRating(GameModule.MATH, 0),
                ModuleRating(GameModule.DETECTIVE, 0),
                ModuleRating(GameModule.DAILY, 0)
            ),
            totalPoints = 0
        )
    }

    private fun readText(name: String): String? {
        return runCatching {
            context.openFileInput(name).bufferedReader().use { it.readText() }
        }.getOrNull()
    }

    private fun writeText(name: String, content: String) {
        context.openFileOutput(name, Context.MODE_PRIVATE).bufferedWriter().use { writer ->
            writer.write(content)
        }
    }
}
