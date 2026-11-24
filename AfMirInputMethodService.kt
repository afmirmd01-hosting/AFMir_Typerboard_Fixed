package com.afmir.typerboard

import android.inputmethodservice.InputMethodService
import android.view.LayoutInflater
import android.view.View
import android.widget.Button

class AfMirInputMethodService : InputMethodService() {

    override fun onCreate() {
        super.onCreate()
        TyperPrefs.load(this)
    }

    override fun onCreateInputView(): View {
        val view = LayoutInflater.from(this).inflate(R.layout.keyboard_view, null)
        val keys = listOf(
            "q","w","e","r","t","y","u","i","o","p",
            "a","s","d","f","g","h","j","k","l",
            "z","x","c","v","b","n","m",
            "1","2","3","4","5","6","7","8","9","0",
            "space","del"
        )

        keys.forEach { key ->
            val resId = resources.getIdentifier("btn_$key", "id", packageName)
            if (resId != 0) {
                view.findViewById<Button>(resId)?.setOnClickListener {
                    TyperPrefs.load(this)
                    handleKeyPress(key)
                }
            }
        }
        return view
    }

    private fun handleKeyPress(key: String) {
        val ic = currentInputConnection ?: return
        when (key) {
            "del" -> {
                ic.deleteSurroundingText(1, 0)
                return
            }
            "space" -> {
                val output = if (TyperPrefs.autoWords) {
                    TyperPrefs.formatOutput(TyperPrefs.nextPhrase())
                } else " "
                ic.commitText(if (output == " ") output else "$output ", 1)
                return
            }
        }

        val lower = key.lowercase()
        val output = when {
            TyperPrefs.autoAlpha && TyperPrefs.alphaMap.containsKey(lower) ->
                TyperPrefs.formatOutput(TyperPrefs.alphaMap[lower] ?: key)
            TyperPrefs.autoNums && lower.matches(Regex("[0-9]")) ->
                TyperPrefs.formatOutput("$lower > 10-20-30-40-50")
            TyperPrefs.autoWords ->
                TyperPrefs.formatOutput(TyperPrefs.nextPhrase())
            else -> key
        }
        ic.commitText(output, 1)
    }
}
