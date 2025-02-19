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


class SZ_Exercise : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_sz)

        val toolbar: Toolbar = findViewById(R.id.myToolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val buttonNaglosie = findViewById<Button>(R.id.buttonNaglosie)
        val buttonSrodglosie = findViewById<Button>(R.id.buttonSrodglosie)
        val buttonWyglosie = findViewById<Button>(R.id.buttonWyglosie)


        val buttons = listOf<Button>(
            buttonNaglosie,
            buttonSrodglosie,
            buttonWyglosie,
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
                true 
            }
        }

        buttonNaglosie.setOnClickListener {
            val intent = Intent(this, SZ_Naglosie::class.java)
            startActivity(intent)
        }

        buttonSrodglosie.setOnClickListener {
            val intent = Intent(this, SZ_Srodglosie::class.java)
            startActivity(intent)
        }

        buttonWyglosie.setOnClickListener {
            val intent = Intent(this, SZ_Wyglosie::class.java)
            startActivity(intent)
        }
    }
}