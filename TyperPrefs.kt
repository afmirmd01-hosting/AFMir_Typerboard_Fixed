package com.afmir.typerboard

import android.content.Context
import org.json.JSONObject

object TyperPrefs {
    private const val PREF_NAME = "afmir_typer_prefs"

    var autoNums = false
    var autoAlpha = true
    var autoWords = false
    var iphoneDrop = true

    val alphaMap = mutableMapOf(
        "s" to "Hey bro, let's play fast!",
        "r" to "Ready now, start the match!",
        "g" to "Good game, well played!"
    )

    private val phrases = listOf(
        "Roll the dice fast!",
        "Nice move!",
        "One more round?",
        "Clean win!"
    )
    private var phraseIndex = 0

    fun nextPhrase(): String {
        val phrase = phrases[phraseIndex % phrases.size]
        phraseIndex++
        return phrase
    }

    fun formatOutput(raw: String): String =
        if (iphoneDrop) " ✦ $raw ✦" else raw

    fun save(context: Context) {
        val json = JSONObject()
        alphaMap.forEach { (key, value) -> json.put(key, value) }
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit()
            .putBoolean("autoNums", autoNums)
            .putBoolean("autoAlpha", autoAlpha)
            .putBoolean("autoWords", autoWords)
            .putBoolean("iphoneDrop", iphoneDrop)
            .putString("alphaMap", json.toString())
            .apply()
    }

    fun load(context: Context) {
        val sp = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        autoNums = sp.getBoolean("autoNums", false)
        autoAlpha = sp.getBoolean("autoAlpha", true)
        autoWords = sp.getBoolean("autoWords", false)
        iphoneDrop = sp.getBoolean("iphoneDrop", true)
        sp.getString("alphaMap", null)?.let { raw ->
            try {
                val obj = JSONObject(raw)
                alphaMap.clear()
                obj.keys().forEach { key -> alphaMap[key] = obj.getString(key) }
            } catch (_: Exception) {
                // Keep built-in shortcuts if stored preferences are invalid.
            }
        }
    }
}
