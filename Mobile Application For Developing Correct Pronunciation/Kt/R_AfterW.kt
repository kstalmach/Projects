package com.example.myapplication

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class R_AfterW : AppCompatActivity() {

    private val syllables = listOf("Wra-", "Wre-","Wro-","Wró-","Wry-")

    private val wordSets = listOf(
        listOf("Wrażliwość", "Wrażliwy", "Wrażliwa", "Wraca", "Wrażliwie", "Wracam", "Wraz"),
        listOf("Wreszcie", "Wręcz", "Wręczać", "Wręczenie", "Wręczono", "Wręczony", "Wręczona"),
        listOf("Wroga", "Wrogi", "Wrogość", "Wrogo", "Wrogowie", "Wroniec", "Wrota"),
        listOf("Wrócić", "Wróci", "Wrócił", "Wróciła", "Wróciłem", "Wróciłam", "Wróciłeś"),
        listOf("Wryć", "Wrypy", "Wryczać", "Wryty", "Wrywać", "Wryczenie", "Wryliwy")
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