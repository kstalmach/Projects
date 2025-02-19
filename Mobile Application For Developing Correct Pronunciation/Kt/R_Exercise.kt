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


class R_Exercise : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_r)

        val toolbar: Toolbar = findViewById(R.id.myToolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val buttonD = findViewById<Button>(R.id.buttonD)
        val buttonT = findViewById<Button>(R.id.buttonT)
        val buttonF = findViewById<Button>(R.id.buttonF)
        val buttonW = findViewById<Button>(R.id.buttonW)
        val buttonCH = findViewById<Button>(R.id.buttonCH)
        val buttonM = findViewById<Button>(R.id.buttonM)
        val buttonS = findViewById<Button>(R.id.buttonS)
        val buttonSs = findViewById<Button>(R.id.buttonSs)

        val buttons = listOf<Button>(
            buttonD,
            buttonT,
            buttonF,
            buttonW,
            buttonCH,
            buttonM,
            buttonS,
            buttonSs
        )

        buttons.forEach { button ->
            button.setOnLongClickListener {
                val alertDialogBuilder = AlertDialog.Builder(this)
                alertDialogBuilder.setMessage("Czy chcesz usunąć przycisk?")
                    .setPositiveButton("Tak", DialogInterface.OnClickListener { dialog, which ->
                        val parentLayout = button.parent as? ViewGroup
                        parentLayout?.removeView(button)
                        dialog.dismiss()
                    })
                    .setNegativeButton("Nie", DialogInterface.OnClickListener { dialog, which ->
                        dialog.dismiss()
                    }).show()
                true // Wymagane zwrócenie wartości true, aby wskazać, że zdarzenie zostało obsłużone
            }
        }


        buttonD.setOnClickListener {
            val intent = Intent(this, R_AfterD::class.java)
            startActivity(intent)
        }

        buttonT.setOnClickListener {
            val intent = Intent(this, R_AfterT::class.java)
            startActivity(intent)
        }

        buttonF.setOnClickListener {
            val intent = Intent(this, R_AfterF::class.java)
            startActivity(intent)
        }

        buttonW.setOnClickListener {
            val intent = Intent(this, R_AfterW::class.java)
            startActivity(intent)
        }

        buttonCH.setOnClickListener {
            val intent = Intent(this, R_AfterCH::class.java)
            startActivity(intent)
        }

        buttonM.setOnClickListener {
            val intent = Intent(this, R_AfterM::class.java)
            startActivity(intent)
        }

        buttonS.setOnClickListener {
            val intent = Intent(this, R_AfterS::class.java)
            startActivity(intent)
        }

        buttonSs.setOnClickListener {
            val intent = Intent(this, R_AfterSs::class.java)
            startActivity(intent)
        }
    }
}