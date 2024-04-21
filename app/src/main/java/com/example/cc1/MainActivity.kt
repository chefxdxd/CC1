package com.example.cc1

import android.content.Context
import android.content.res.Configuration
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private var currentLanguage: String = ""
    private lateinit var resetButton: Button
    private lateinit var stealButton: Button
    private lateinit var scoreButton: Button
    private lateinit var scoreTextView: TextView
    private lateinit var languageSpinner: Spinner
    private var currentScore = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.activity_main_landscape)
        } else {
            setContentView(R.layout.activity_main)
        }

        currentLanguage = getCurrentLanguage()
        resetButton = findViewById(R.id.resetButton)
        stealButton = findViewById(R.id.stealButton)
        scoreButton = findViewById(R.id.scoreButton)
        scoreTextView = findViewById(R.id.scoreTextView)
        languageSpinner = findViewById(R.id.languageSpinner)

        val languageOptions = arrayOf("English", "日本語", "Ελληνικά")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, languageOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        languageSpinner.adapter = adapter

        languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                val languageCode = when (languageOptions[position]) {
                    "English" -> "en"
                    "日本語" -> "ja"
                    "Ελληνικά" -> "el"
                    else -> "en"
                }
                if (currentLanguage != languageCode) {
                    changeAppLanguage(this@MainActivity, languageCode)
                    currentLanguage = languageCode // Update current language
                    recreate() // Restart
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Does nothing
            }
        }

        updateScoreDisplay()

        resetButton.setOnClickListener {
            currentScore = 0
            updateScoreDisplay()
            playClickSound()
            Log.d("MainActivity", "Reset button clicked. Score reset to 0.")
            scoreTextView.setTextColor(resources.getColor(android.R.color.black))
        }

        stealButton.setOnClickListener {
            if (currentScore > 0) {
                currentScore -= 1
                updateScoreDisplay()
                playClickSound()
                Log.d("MainActivity", "Steal button clicked. Score decremented by 1. New score: $currentScore")
            }
            if (currentScore < 15) {
                scoreTextView.setTextColor(resources.getColor(android.R.color.black))
            }
        }

        scoreButton.setOnClickListener {
            currentScore += 1
            updateScoreDisplay()
            playClickSound()
            Log.d("MainActivity", "Score button clicked. Score incremented by 1. New score: $currentScore")

            if (currentScore >= 15) {
                scoreTextView.setTextColor(resources.getColor(android.R.color.holo_green_dark))
                playWinningSound()
            }
            if (currentScore < 15) {
                scoreTextView.setTextColor(resources.getColor(android.R.color.black))
            }
        }

        savedInstanceState?.let {
            currentScore = it.getInt("score")
            updateScoreDisplay()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("score", currentScore)
        outState.putString("language", currentLanguage)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        currentLanguage = savedInstanceState.getString("language", "")
        currentScore = savedInstanceState.getInt("score")
        updateScoreDisplay()
    }

    private fun getCurrentLanguage(): String {
        return resources.configuration.locale.language
    }

    private fun updateScoreDisplay() {
        scoreTextView.text = getString(R.string.score_default, currentScore)
    }

    private fun playClickSound() {
        val mediaPlayer = MediaPlayer.create(this, R.raw.button_click)
        mediaPlayer.start()
    }

    private fun playWinningSound() {
        val mediaPlayer = MediaPlayer.create(this, R.raw.win)
        mediaPlayer.start()
    }

    private fun changeAppLanguage(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }
}
