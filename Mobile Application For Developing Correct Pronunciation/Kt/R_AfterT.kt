package com.example.myapplication

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class R_AfterT : AppCompatActivity() {

    private val syllables = listOf("Tra-", "Tre-","Tro-","Tru-","Try-")

    private val wordSets = listOf(
        listOf("Tracić", "Trafić", "Tragedia", "Trakt", "Tradycja", "Trampolina", "Trawer", "Trapez"),
        listOf("Trener", "Trenować", "Trening", "Treść", "Trema"),
        listOf("Trochę", "Trojak", "Trojka", "Trofeum", "Troska", "Tropy", "Tropiciel", "Tropić"),
        listOf("Trucizna", "Truć", "Trudno", "Trudny", "Trudność", "Truchło", "Trunek", "Truchlejący"),
        listOf("Tryb", "Trybuna", "Trybun", "Trykać", "Tryumf", "Trylion", "Trywialny", "Trybunał")
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