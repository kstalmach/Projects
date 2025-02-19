package com.example.myapplication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar


class Exercises : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercises)

        val toolbar: Toolbar = findViewById(R.id.myToolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val button1 = findViewById<Button>(R.id.buttonSZ)
        val button2 = findViewById<Button>(R.id.buttonCZ)
        val button3 = findViewById<Button>(R.id.buttonR)

        button1.setOnClickListener {
            val intent = Intent(this, SZ_Exercise::class.java)
            startActivity(intent)
        }

        button2.setOnClickListener {
            Toast.makeText(this, "Wkrótce dostępne", Toast.LENGTH_SHORT).show()
        }

        button3.setOnClickListener {
            val intent = Intent(this, R_Exercise::class.java)
            startActivity(intent)
        }
    }
}