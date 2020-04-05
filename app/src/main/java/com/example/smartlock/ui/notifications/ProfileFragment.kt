package com.example.smartlock.ui.notifications

import android.os.Bundle
import android.text.InputType
import android.util.Log.d
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.smartlock.MainActivity
import com.example.smartlock.R
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.getField
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*

class ProfileFragment : Fragment() {

    private lateinit var profileViewModel: ProfileViewModel

    private val db = Firebase.firestore

    var editOrConfirm = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        profileViewModel =
            ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_profile, container, false)
        val progressBarProfile = root.findViewById<ProgressBar>(R.id.progressBarProfile)

        val buttonEditConfirm = root.findViewById<Button>(R.id.buttonEdit_Confirm)

        progressBarProfile.visibility = View.VISIBLE
        buttonEditConfirm.text = "EDIT"

        // Disable Edit Text
        root.profileFullName.isFocusable = true
        root.profileFullName.isFocusableInTouchMode = true
        root.profileFullName.inputType = InputType.TYPE_NULL

        root.profileEmail.isFocusable = true
        root.profileEmail.isFocusableInTouchMode = true
        root.profileEmail.inputType = InputType.TYPE_NULL

        root.profilePhone.isFocusable = true
        root.profilePhone.isFocusableInTouchMode = true
        root.profilePhone.inputType = InputType.TYPE_NULL
        //

        // Get Data from DB Firestore
        val passEmail = (activity as MainActivity).email
        db.collection("User").document(passEmail)
            .get()
            .addOnSuccessListener { result ->
                val fullName = result.getField<String>("full name")
                val email = result.getField<String>("email")
                var phoneNo = ""
                if (result.getField<String>("phone no").isNullOrBlank()) {

                }
                else {
                    phoneNo = result.getField<String>("phone no").toString()
                }

                root.profileFullName.setText(fullName)
                root.profileEmail.setText(email)
                root.profilePhone.setText(phoneNo)
                progressBarProfile.visibility = View.INVISIBLE
            }

        fun validateForm(): Boolean {
            var valid = true

            val fullname = root.profileFullName.text
            val email = root.profileEmail.text
            val phoneNo = root.profilePhone.text

            if (fullname.isEmpty()) {
                root.profileFullName.error = "Required"
                valid = false
            } else {
                root.profileEmail.error = null
            }
            if (email.isEmpty()) {
                root.profileEmail.error = "Required"
                valid = false
            } else {
                root.profileEmail.error = null
            }
            if (phoneNo.isEmpty()) {
                root.profilePhone.error = "Required"
                valid = false
            } else {
                root.profileEmail.error = null
            }

            return valid
        }

        buttonEditConfirm.setOnClickListener {
            if(editOrConfirm) {
                root.profileFullName.inputType = InputType.TYPE_CLASS_TEXT
                root.profileEmail.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                root.profilePhone.inputType = InputType.TYPE_CLASS_PHONE
                buttonEditConfirm.text = "CONFIRM"
                editOrConfirm = false
            } else {

                if (!validateForm()) {
                    editOrConfirm = true
                } else {
                    progressBarProfile.visibility = View.VISIBLE
                    val fullname = root.profileFullName.text.toString()
                    val email = root.profileEmail.text.toString()
                    val phoneNo = root.profilePhone.text.toString()

                    // update to db
                    val update = hashMapOf(
                        "email" to email,
                        "full name" to fullname,
                        "phone no" to phoneNo
                    )

                    d("bomohit", "pass email: $passEmail")

                    db.collection("User").document(passEmail)
                        .set(update, SetOptions.merge())
                        .addOnSuccessListener {
                            d("bomohit", "Successfully update")
                            editOrConfirm = true
                            buttonEditConfirm.text = "EDIT"
                            root.profileFullName.inputType = InputType.TYPE_NULL
                            root.profileEmail.inputType = InputType.TYPE_NULL
                            root.profilePhone.inputType = InputType.TYPE_NULL

                            progressBarProfile.visibility = View.INVISIBLE
                            Toast.makeText(context, "Information Updated!", Toast.LENGTH_SHORT).show()

                        }
                        .addOnFailureListener { e ->
                            d("bomohit", "error ",e)
                        }

                }

            }
        }


//        val textView: TextView = root.findViewById(R.id.text_notifications)
        profileViewModel.text.observe(this, Observer {
//            textView.text = it
        })
        return root
    }

}