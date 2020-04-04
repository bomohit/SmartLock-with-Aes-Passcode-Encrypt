package com.example.smartlock

import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log.d
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.getField
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.selection_signup.*
import java.util.*

class SelectionSignUp : AppCompatActivity(), View.OnClickListener {

    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.selection_signup)

        buttonSignup.setOnClickListener(this)
        d("bomohit", "on create")

    }

    override fun onClick(v: View) {
        val i = v.id

        when (i) {
            R.id.buttonSignup -> createAccount(editFullName.text.toString(), editEmail.text.toString(), editPassword.text.toString())
        }

    }

    private fun createAccount(fullName: String, email: String, password: String) {

        if (!validateForm()) {
            d("bomohit", "Failed")
            return
        }

        val encodePassword = encodePassword(password)

        val User = hashMapOf(
            "full name" to fullName,
            "email" to email,
            "password" to encodePassword
        )
        val email = editEmail.text.toString()

        db.collection("User").document("$email")
            .get()
            .addOnSuccessListener { result ->
                if (result.getField<String>("email")!= null) {
                    editEmail.error = "Email Already Use"
                } else {
                    editEmail.error = null

                    db.collection("User").document("$email")
                        .set(User)
                        .addOnSuccessListener {
                            d("bomohit", "successfully added")
                        }
                        .addOnFailureListener { e ->
                            d("bomohit", "error occur ", e)
                        }
                }
            }



    }

    private fun encodePassword(password: String): String {
        val encodePwd = Base64.getEncoder().encodeToString(password.toByteArray())
        return encodePwd
    }

    fun validateForm(): Boolean {
        // Validate the form
        var valid = true

        val fullName = editFullName.text.toString()
        if (fullName.isEmpty()) {
            editFullName.error = "Required"
            valid = false
        } else {
            editFullName.error = null
        }

        val email = editEmail.text.toString()
        if (email.isEmpty()) {
            editEmail.error = "Required"
            valid = false
        } else {
            editEmail.error = null
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editEmail.error = "Invalid Email"
            valid = false
        } else {
            editEmail.error = null
        }

        val password = editPassword.text.toString()
        val password2 = editPassword2.text.toString()
        if (password.isEmpty()) {
            editPassword.error = "Required"
            valid = false
        } else {
            editPassword.error = null
        }
        if (password2.isEmpty()) {
            editPassword2.error = "Required"
            valid = false
        } else {
            editPassword2.error = null
        }
        if (password != password2) {
            editPassword2.error = "Not same"
            valid = false
        } else {
            editPassword2.error = null
        }
        if (password.length < 6) {
            editPassword.error = "Must be more than 6"
            editPassword2.error = "Must be more than 6"
            valid = false
        } else {
            editPassword.error = null
            editPassword2.error = null
        }

        return valid

    }

}