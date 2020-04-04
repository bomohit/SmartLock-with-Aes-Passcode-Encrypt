package com.example.smartlock

import android.content.Intent
import android.os.Bundle
import android.util.Log.d
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.getField
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.selection_login.*
import java.util.*

class SelectionLogin : AppCompatActivity(), View.OnClickListener {

    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.selection_login)

        buttonSignIn.setOnClickListener(this)
        progressBar.visibility = View.INVISIBLE

    }

    override fun onClick(v: View) {
        val i = v.id

        when (i) {
            R.id.buttonSignIn -> loginAccount(editEmail.text.toString(), editPwd.text.toString())
        }

    }

    private fun loginAccount(email: String, password: String) {
        d("bomohit", "pressed")

        if (!validateAcc()) {
            return
        }
        progressBar.visibility = View.VISIBLE

        db.collection("User").document(email)
            .get()
            .addOnSuccessListener { result ->
                if (result.getField<String>("email") == null) {

                    progressBar.visibility = View.INVISIBLE
                    Toast.makeText(applicationContext, "invalid Account", Toast.LENGTH_SHORT).show()
                } else {

                    val pwd = result.getField<String>("password").toString()
                    val decodePwd = String(Base64.getDecoder().decode(pwd))

                    if (decodePwd == password) {
                        progressBar.visibility = View.INVISIBLE
                        Toast.makeText(applicationContext, "Welcome ${result.getField<String>("full name")}", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java))
                    } else {
                        progressBar.visibility = View.INVISIBLE
                        Toast.makeText(applicationContext, "invalid Account", Toast.LENGTH_SHORT).show()
                    }

                }
            }

    }

    fun validateAcc(): Boolean {
        var valid = true

        val email = editEmail.text.toString()
        if (email.isEmpty()) {
            editEmail.error = "Required"
            valid = false
        } else {
            editEmail.error = null
        }

        val pwd = editPwd.text.toString()
        if (pwd.isEmpty()) {
            editPwd.error = "Required"
            valid = false
        } else {
            editPwd.error = null
        }

        return valid

    }
}