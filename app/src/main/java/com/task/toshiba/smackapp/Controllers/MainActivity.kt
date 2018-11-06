package com.task.toshiba.smackapp.Controllers

import android.content.*
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.task.toshiba.smackapp.R
import com.task.toshiba.smackapp.R.id.*
import com.task.toshiba.smackapp.Services.AuthService
import com.task.toshiba.smackapp.Services.UserDataService
import com.task.toshiba.smackapp.Utilits.BROADCAST_USER_DATA_CHANGE
import com.task.toshiba.smackapp.Utilits.SOCKET_URL
import io.socket.client.IO
import kotlinx.android.synthetic.main.activity_log_in.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.*

class MainActivity : AppCompatActivity() {

    val socket = IO.socket(SOCKET_URL)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)


        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()


    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(this).registerReceiver(userDataChangeListener, IntentFilter(
                BROADCAST_USER_DATA_CHANGE))
        socket.connect()
    }

    override fun onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(userDataChangeListener)
        super.onPause()
    }

    override fun onDestroy() {
        socket.disconnect()
        super.onDestroy()
    }


    private val userDataChangeListener = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (AuthService.userLogedIn) {

                userName.text = UserDataService.name
                userEmail.text = UserDataService.email
                var resurseId = resources.getIdentifier(UserDataService.avatarName, "mipmap", packageName)
                userProfile.setImageResource(resurseId)
                userProfile.setBackgroundColor(UserDataService.convertStringColorToInt(UserDataService.avatarColor))
                btn_login.text = "LOG OUT"
            }
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    fun sendMessage(view: View) {

    }

    fun addChanel(view: View) {
        if (AuthService.userLogedIn) {
            val builder = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.create_channel_dialog, null)
            builder.setView(dialogView)
                    .setPositiveButton("ADD") { dialog: DialogInterface?, which: Int ->
                        val nameEt = dialogView.findViewById<EditText>(R.id.name)
                        val descriptionEt = dialogView.findViewById<EditText>(R.id.description)
                        var name = nameEt.text.toString()
                        var description = descriptionEt.text.toString()

                        createChannel(name, description)
                        dialog!!.dismiss()
                    }
                    .setNegativeButton("CANCEL") { dialog: DialogInterface?, which: Int ->

                        dialog!!.dismiss()
                    }.show()


        } else {

        }


    }

    fun createChannel(name: String, description: String) {
        socket.emit("newChannel", name, description)

    }

    fun logInFunction(view: View) {
        if (AuthService.userLogedIn) {
            logOutFun()

        } else {
            var goToLogIn = Intent(this, activity_logIn::class.java)
            startActivity(goToLogIn)
        }

    }

    private fun logOutFun() {
        UserDataService.logOut()
        userName.text = "Log In"
        userProfile.setImageResource(R.mipmap.profiledefault)
        userProfile.setBackgroundColor(Color.TRANSPARENT)
        bt_logIn.text = "LOG IN"

    }

    fun hideeyboard() {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        if (inputManager.isAcceptingText) {
            inputManager.hideSoftInputFromWindow(currentFocus.windowToken, 0)
        }

    }

}
