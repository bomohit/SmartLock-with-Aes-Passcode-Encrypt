package com.example.smartlock

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log.d
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.smartlock.ui.notifications.ProfileFragment

class MainActivity : AppCompatActivity() {

    var email = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val passEmail = intent.getStringExtra("email").toString()
        email = passEmail
//        email = "test@gmail.com"

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_passcode, R.id.navigation_profile
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.action_signOut -> {
                signOut()
            }

            else -> super.onOptionsItemSelected(item)
        }


    }

    private fun signOut(): Boolean {
        d("bomohit", "sign out pressed")

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Signing Out?")
        builder.setMessage("Confirm?")

        val yes = "yes".toString()

        builder.setPositiveButton("YES", DialogInterface.OnClickListener {
            dialog, id ->
            Toast.makeText(applicationContext,"Bye", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, SelectionMain::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish() // finish the current activity
        })

        builder.setNegativeButton("No"){dialog, id ->
            Toast.makeText(applicationContext,"Cancelled", Toast.LENGTH_SHORT).show()
        }

        builder.setPositiveButtonIcon(resources.getDrawable(R.drawable.ic_check_black_24dp))
        builder.setNegativeButtonIcon(resources.getDrawable(R.drawable.ic_clear_black_24dp))

        val dialog: AlertDialog = builder.create()
        dialog.show()

        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }


}
