package com.example.smartlock.ui.home

import android.content.Context
import android.os.Bundle
import android.util.Log.d
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.smartlock.MainActivity
import com.example.smartlock.R
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.getField
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
//        val textView: TextView = root.findViewById(R.id.text_home)
        val progressBar: ProgressBar = root.findViewById(R.id.progressBarLock)

        val sharedPreferences = context?.getSharedPreferences("SP_INFO", Context.MODE_PRIVATE)
        val key = sharedPreferences?.getString("KEY", "")
        val db = Firebase.firestore

        val passEmail = (activity as MainActivity).email
        var code = ""

        var i = false

        db.collection("User").document(passEmail)
            .get()
            .addOnSuccessListener { result ->
                code = result.getField<String>("passcode").toString()
                d("bomohit", "pass: $code")
            }


        // Progress Bar logic
        root.buttonSecurity.setOnClickListener {
            // Launch Coroutines
            GlobalScope.launch(context = Dispatchers.Main) {
                delay(2000)
                if ( i == false ) {
                    if (code != "null") {
                        root.buttonSecurity.setBackgroundResource(R.drawable.unlock)
                        i = true
                        progressBar.visibility = View.INVISIBLE
                        Toast.makeText(context, "Opened!", Toast.LENGTH_SHORT).show()
                    } else {
                        progressBar.visibility = View.INVISIBLE
                        Toast.makeText(context, "Please Set The Passcode!", Toast.LENGTH_SHORT)
                            .show()
                    }

                } else {
                    root.buttonSecurity.setBackgroundResource(R.drawable.lock_gen2)
                    i = false
                    progressBar.visibility = View.INVISIBLE
                    Toast.makeText(context, "Locked!", Toast.LENGTH_SHORT).show()
                }
            }

            GlobalScope.launch(context = Dispatchers.Main) {
                progressBar.visibility = View.VISIBLE
            }

        }

        homeViewModel.text.observe(this, Observer {
//            textView.text = it
        })
        return root
    }
}