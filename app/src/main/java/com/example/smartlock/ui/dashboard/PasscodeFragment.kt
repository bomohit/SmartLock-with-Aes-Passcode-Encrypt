package com.example.smartlock.ui.dashboard

import android.content.Context
import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log.d
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.smartlock.MainActivity
import com.example.smartlock.R
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.getField
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_passcode.*
import kotlinx.android.synthetic.main.fragment_passcode.view.*
import java.security.KeyStore
import java.util.*
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

class PasscodeFragment : Fragment() {

    private lateinit var passcodeViewModel: PasscodeViewModel

    var valid = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        passcodeViewModel =
            ViewModelProviders.of(this).get(PasscodeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_passcode, container, false)
        val buttonSetPasscode = root.findViewById<Button>(R.id.buttonSetPasscode)
        val inputPasscode = root.findViewById<EditText>(R.id.inputPasscode)
        val progressBarPasscode = root.findViewById<ProgressBar>(R.id.progressBarPasscode)

        inputPasscode.isEnabled = true
        progressBarPasscode.visibility = View.VISIBLE

        // Shared preference
        val sharedPreferences = context?.getSharedPreferences("SP_INFO", Context.MODE_PRIVATE)

        val db = Firebase.firestore

        //Check if the passcode already exist or not
        // Get Data from DB Firestore
        val passEmail = (activity as MainActivity).email
        db.collection("User").document(passEmail)
            .get()
            .addOnSuccessListener { result ->
                val passcode = result.getField<String>("passcode")
                d("bomohit", "$passcode")

                if (passcode!= null) {
                    // get the key
                    val key = sharedPreferences?.getString("KEY", "")
                    // get the code
                    db.collection("User").document(passEmail)
                        .get()
                        .addOnSuccessListener { result ->
                            val code = result.getField<String>("passcode").toString()
                            // decode key and code
                            val decodeKey = Base64.getDecoder().decode(key)
                            val decodeCode = Base64.getDecoder().decode(code)

                            // decrypt
                            val decryptPasscode = decryptData(decodeKey, decodeCode)

                            // display in input passcode
                            inputPasscode.setText(decryptPasscode)

                            valid = false
                            inputPasscode.isEnabled = false
                            buttonSetPasscode.setText("CHANGE PASSCODE")

                            progressBarPasscode.visibility = View.INVISIBLE
                        }
                } else {
                    inputPasscode.setHint("Set Passcode")
                    inputPasscode.error = "Set Passcode"

                    progressBarPasscode.visibility = View.INVISIBLE
                }

            }
            .addOnFailureListener { e ->
                d("bomohit", "error ", e)
            }

        fun validate(): Boolean { // checking if the passcode is entered
            var check = true
            val passcode = inputPasscode.text.toString()
            if (passcode.isEmpty()) {
                check = false
                inputPasscode.error = "Set Passcode"
                inputPasscode.setHint("Set Passcode")
            } else {
                inputPasscode.error = null
                inputPasscode.isEnabled = true
            }
            return check
        }

        fun setPasscode() {
            inputPasscode.isEnabled = false
            val pass = inputPasscode.text.toString()

            // encrypt key
            // For generating Key ( only once / enable this code if not yet run )
            val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
            val keyGenParameterSpec = KeyGenParameterSpec.Builder("MyKeyAlias", KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .build()

            keyGenerator.init(keyGenParameterSpec)
            keyGenerator.generateKey()

            // Encrypt
            val encrypt = encryptData(pass)
            // Hash
            val hashEncryptKey = Base64.getEncoder().encodeToString(encrypt.first)
            val hashEncryptCode = Base64.getEncoder().encodeToString(encrypt.second)

            // update to db

            db.collection("User").document(passEmail)
                .update("passcode", hashEncryptCode)
                .addOnSuccessListener {
                    d("bomohit", "Added Passcode")

                    // add to shared preference
                    val editor = sharedPreferences!!.edit()

                    editor!!.putString("KEY", hashEncryptKey)
                    editor.apply()

                    // Decrypt
                    val decrypt = decryptData(encrypt.first, encrypt.second)

                    inputPasscode.setText(decrypt)
                    // disable the input
                    inputPasscode.isEnabled = false
                    buttonSetPasscode.setText("CHANGE PASSCODE")
                    valid = false
                }
                .addOnFailureListener { e ->
                    d("bomohit", "error ", e)
                }
        }

        root.buttonSetPasscode.setOnClickListener {
            progressBarPasscode.visibility = View.VISIBLE
            if (valid) {

                if (!validate()) {
                    progressBarPasscode.visibility = View.INVISIBLE
                } else {
                    setPasscode()
                    progressBarPasscode.visibility = View.INVISIBLE
                }

            } else {
                inputPasscode.isEnabled = true
                valid = true
                progressBarPasscode.visibility = View.INVISIBLE
            }

        }



//        val textView: TextView = root.findViewById(R.id.text_dashboard)
        passcodeViewModel.text.observe(this, Observer {
//            textView.text = it
        })
        return root
    }



    // Fetch the key
    fun getKey(): SecretKey {
        val keystore = KeyStore.getInstance("AndroidKeyStore")

        keystore.load(null)

        val secretKeyEntry = keystore.getEntry("MyKeyAlias", null) as KeyStore.SecretKeyEntry

        return secretKeyEntry.secretKey
    }

    fun encryptData(data: String): Pair<ByteArray, ByteArray> {
        val cipher = Cipher.getInstance("AES/CBC/NoPadding")

        var temp = data
        while (temp.toByteArray().size % 16 != 0)
            temp += "\u0020"
        cipher.init(Cipher.ENCRYPT_MODE, getKey())

        val ivBytes = cipher.iv

        val encryptedBytes = cipher.doFinal(temp.toByteArray(Charsets.UTF_8))

        return Pair(ivBytes, encryptedBytes)
    }

    fun decryptData(ivBytes: ByteArray, data: ByteArray): String {
        val cipher = Cipher.getInstance("AES/CBC/NoPadding")
        val spec = IvParameterSpec(ivBytes)

        cipher.init(Cipher.DECRYPT_MODE, getKey(), spec)

        return cipher.doFinal(data).toString(Charsets.UTF_8).trim()
    }
}