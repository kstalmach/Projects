package com.example.myapplication

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class SZ_Wyglosie : AppCompatActivity() {

    private val syllables = listOf("-asz", "-esz", "-isz", "-osz", "-usz")

    private val wordSets = listOf(
        listOf(
            "Apasz",
            "Gnasz",
            "Ufasz",
            "Znasz",
            "Grasz",
            "Dumasz",
            "Dobywasz",
            "Wydasz",
            "Okiwasz",
            "Zmywasz",
            "Gulasz"
        ),
        listOf(
            "Wiesz",
            "Jesz",
            "Ciesz",
            "Dowiesz",
            "Naciesz",
            "Olejesz",
            "Ugniesz",
            "Dylujesz",
            "Balujesz"
        ),
        listOf(
            "Bawisz",
            "Pisz",
            "Koisz",
            "Wpisz",
            "Zapisz",
            "Ucisz",
            "Kosisz",
            "Spalisz",
            "Dusisz",
            "Afisz"
        ),
        listOf("Kosz", "Długosz", "Grosz", "Klosz", "Łosz", "Płosz", "Kawosz", "Smakosz"),
        listOf(
            "Busz",
            "Susz",
            "Tusz",
            "Osusz",
            "Sojusz",
            "Fundusz",
            "Zagłusz",
            "Kapelusz",
            "Słabeusz"
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_training)

        val toolbar: Toolbar = findViewById(R.id.myToolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val buttonNext = findViewById<Button>(R.id.nextButton)
        val buttonPrevious = findViewById<Button>(R.id.previousButton)
        val textViewWord = findViewById<TextView>(R.id.exerciseTextView)
        val textViewWordHeadline = findViewById<TextView>(R.id.exerciseTextViewHeadline)

        var currentWordSetIndex = 0
        var currentWordIndex = 0

        fun showCurrentWord() {
            val currentSyllable = syllables[currentWordSetIndex]
            val currentWords = wordSets[currentWordSetIndex].joinToString("\n")
            textViewWordHeadline.text = "Segment: $currentSyllable"
            textViewWord.text = currentWords
        }

        buttonNext.setOnClickListener {
            currentWordIndex = 0 
            currentWordSetIndex++  
            if (currentWordSetIndex >= wordSets.size) {
                currentWordSetIndex = 0 
            }
            showCurrentWord()
        }

        buttonPrevious.setOnClickListener {
            currentWordIndex = 0 
            currentWordSetIndex--  
            if (currentWordSetIndex < 0) {
                currentWordSetIndex = wordSets.size - 1
            }
            showCurrentWord()
        }
        showCurrentWord()
    }

}