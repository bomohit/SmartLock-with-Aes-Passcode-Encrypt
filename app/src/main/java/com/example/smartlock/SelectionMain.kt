package com.example.smartlock

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.smartlock.R
import kotlinx.android.synthetic.main.selection_main.*

class SelectionMain : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.selection_main)

        buttonLogin.setOnClickListener {
            startActivity(Intent(this, SelectionLogin::class.java))
        }

        buttonSignUp.setOnClickListener {
            startActivity(Intent(this, SelectionSignUp::class.java))
        }
    }


}