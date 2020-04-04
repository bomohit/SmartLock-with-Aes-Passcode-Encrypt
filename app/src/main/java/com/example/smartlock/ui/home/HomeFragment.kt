package com.example.smartlock.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.smartlock.R
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

        var i = false


        // Progress Bar logic
        root.buttonSecurity.setOnClickListener {
            // Launch Coroutines
            GlobalScope.launch(context = Dispatchers.Main) {
                delay(2000)
                if ( i == false ) {
                    root.buttonSecurity.setBackgroundResource(R.drawable.unlock)
                    i = true
                    progressBar.visibility = View.INVISIBLE
                } else {
                    root.buttonSecurity.setBackgroundResource(R.drawable.lock_gen2)
                    i = false
                    progressBar.visibility = View.INVISIBLE
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