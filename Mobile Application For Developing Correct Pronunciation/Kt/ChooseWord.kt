package com.example.myapplication

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View.generateViewId
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar


class ChooseWord : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_word)

        val toolbar: Toolbar = findViewById(R.id.myToolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val buttonSzafa = findViewById<Button>(R.id.buttonSzafa)
        val buttonSzopa = findViewById<Button>(R.id.buttonSzopa)
        val buttonSzelki = findViewById<Button>(R.id.buttonSzelki)
        val buttonKasza = findViewById<Button>(R.id.buttonKasza)
        val buttonNosze = findViewById<Button>(R.id.buttonNosze)
        val buttonWieszak = findViewById<Button>(R.id.buttonWieszak)
        val buttonKosz = findViewById<Button>(R.id.buttonKosz)
        val buttonAfisz = findViewById<Button>(R.id.buttonAfisz)
        val buttonGulasz = findViewById<Button>(R.id.buttonGulasz)
        val buttonInstrukcja = findViewById<Button>(R.id.buttonInstrukcja)

        val buttons = listOf<Button>(
            buttonSzafa,
            buttonSzopa,
            buttonSzelki,
            buttonKasza,
            buttonNosze,
            buttonWieszak,
            buttonKosz,
            buttonAfisz,
            buttonGulasz,
        )

        for (button in buttons) {
            button.setOnClickListener {
                val intent = Intent(this, SpeechAnalizator::class.java)
                intent.putExtra("word", button.text)
                startActivity(intent)
            }
        }

        buttonInstrukcja.setOnClickListener {
            val intent = Intent(this, Instruction::class.java)
            startActivity(intent)
        }
    }
}