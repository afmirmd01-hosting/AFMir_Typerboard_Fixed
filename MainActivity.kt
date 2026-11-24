package com.afmir.typerboard

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TyperPrefs.load(this)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btnEnableIme).setOnClickListener {
            startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))
        }

        findViewById<Button>(R.id.btnSelectIme).setOnClickListener {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showInputMethodPicker()
        }

        findViewById<Button>(R.id.btnStartOverlay).setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                startActivity(intent)
            } else {
                startService(Intent(this, FloatingTyperService::class.java))
            }
        }

        val keyInput = findViewById<EditText>(R.id.etShortcutKey)
        val valueInput = findViewById<EditText>(R.id.etShortcutVal)
        findViewById<Button>(R.id.btnSaveShortcut).setOnClickListener {
            val key = keyInput.text.toString().trim().lowercase()
            val value = valueInput.text.toString().trim()
            if (key.length == 1 && value.isNotEmpty()) {
                TyperPrefs.alphaMap[key] = value
                TyperPrefs.save(this)
                keyInput.text.clear()
                valueInput.text.clear()
                Toast.makeText(this, "Shortcut saved", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Enter one key and some text", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
