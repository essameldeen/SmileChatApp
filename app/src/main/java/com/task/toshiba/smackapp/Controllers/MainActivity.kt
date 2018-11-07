package com.task.toshiba.smackapp.Controllers

import android.content.*
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import com.task.toshiba.smackapp.Adapter.MessagesAdapter
import com.task.toshiba.smackapp.Model.Channel
import com.task.toshiba.smackapp.Model.Message
import com.task.toshiba.smackapp.R
import com.task.toshiba.smackapp.R.id.*
import com.task.toshiba.smackapp.Services.AuthService
import com.task.toshiba.smackapp.Services.MessageService
import com.task.toshiba.smackapp.Services.UserDataService
import com.task.toshiba.smackapp.Utilits.BROADCAST_USER_DATA_CHANGE
import com.task.toshiba.smackapp.Utilits.SOCKET_URL
import io.socket.client.IO
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_log_in.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_main.*

class MainActivity : AppCompatActivity() {

    val socket = IO.socket(SOCKET_URL)
    lateinit var channelAdapter: ArrayAdapter<Channel>
    lateinit var messageAdapter: MessagesAdapter
    var selectedChannel: Channel? = null
    private fun setupAdapter() {
        channelAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, MessageService.Channels)
        channels_list.adapter = channelAdapter
    }

    private fun setupAdapterMessage() {
        messageAdapter = MessagesAdapter(this, MessageService.messages)
        val layoutManager = LinearLayoutManager(this)
        rv_messages.layoutManager = layoutManager
        rv_messages.adapter = messageAdapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)


        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        setupAdapter()

        socket.connect()
        socket.on("channelCreated", onNewChannel)
        socket.on("messageCreated", onNewMessage)


        if (App.sharedPreferences.isLoggedIn) {
            AuthService.findUserByEmail(this) {}
        }

        channels_list.setOnItemClickListener { _, _, position, _ ->
            selectedChannel = MessageService.Channels[position]
            downloadMessageForChannel()
            drawer_layout.closeDrawer(GravityCompat.START)

        }

    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(this).registerReceiver(userDataChangeListener, IntentFilter(
                BROADCAST_USER_DATA_CHANGE))

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
            if (App.sharedPreferences.isLoggedIn) {

                userName.text = UserDataService.name
                userEmail.text = UserDataService.email
                var resurseId = resources.getIdentifier(UserDataService.avatarName, "mipmap", packageName)
                userProfile.setImageResource(resurseId)
                userProfile.setBackgroundColor(UserDataService.convertStringColorToInt(UserDataService.avatarColor))
                nav_btn_login.text = "LOG OUT"
                getAllChannel()
            }
        }
    }

    private fun getAllChannel() {
        MessageService.getAllChannels(complete = {
            if (it) {
                if (MessageService.Channels.count() > 0) {
                    selectedChannel = MessageService.Channels[0]
                    channelAdapter.notifyDataSetChanged()
                    downloadMessageForChannel()
                }

            } else {

            }
        })

    }

    private fun downloadMessageForChannel() {
        nameChannel.text = selectedChannel?.name

        if (selectedChannel != null) {
            MessageService.getAllMessages(selectedChannel!!.id, complete = {
                if (it) {
                    if (MessageService.messages.count() > 0) {
                        setupAdapterMessage()
                        messageAdapter.notifyDataSetChanged()
                        rv_messages.smoothScrollToPosition(messageAdapter.itemCount - 1)
                    }


                } else {
                    Toast.makeText(this, "Failed To Get All Message.", Toast.LENGTH_LONG).show()
                }
            })
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
        if (App.sharedPreferences.isLoggedIn && et_message.text.isNotEmpty() && selectedChannel != null) {
            var channelId = selectedChannel!!.id
            var userId = UserDataService.id

            socket.emit("newMessage", et_message.text.toString(), userId, channelId, UserDataService.name
                    , UserDataService.avatarName, UserDataService.avatarColor)
            et_message.text.clear()
            hideeyboard()

        }


    }

    fun addChanel(view: View) {
        if (App.sharedPreferences.isLoggedIn) {
            val builder = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.create_channel_dialog, null)
            builder.setView(dialogView)
                    .setPositiveButton("ADD")
                    { dialog: DialogInterface?, _: Int ->
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

            drawer_layout.closeDrawer(GravityCompat.START)

        } else {

        }
    }

    private val onNewChannel = Emitter.Listener { args ->
        if (App.sharedPreferences.isLoggedIn) {
            runOnUiThread {
                var name = args[0] as String
                var description = args[1] as String
                var id = args[2] as String

                val newChannel = Channel(name, description, id)
                MessageService.Channels.add(newChannel)
                channelAdapter.notifyDataSetChanged()
            }
        }


    }
    private val onNewMessage = Emitter.Listener { args ->
        if (App.sharedPreferences.isLoggedIn) {
            runOnUiThread {
                val channelId = args[2] as String
                if (channelId == selectedChannel?.id) {
                    val message = args[0] as String
                    val userName = args[3] as String
                    val avatarName = args[4] as String
                    val avatarColor = args[5] as String
                    val id = args[6] as String
                    val timeStamp = args[7] as String
                    val newMessage = Message(message, userName, channelId, avatarName, avatarColor, timeStamp, id)
                    MessageService.messages.add(newMessage)
                    messageAdapter.notifyDataSetChanged()
                    rv_messages.smoothScrollToPosition(messageAdapter.itemCount - 1)
                }

            }
        }


    }

    fun createChannel(name: String, description: String) {
        socket.emit("newChannel", name, description)

    }

    fun logInFunction(view: View) {
        if (App.sharedPreferences.isLoggedIn) {
            messageAdapter.notifyDataSetChanged()
            channelAdapter.notifyDataSetChanged()
            userName.text = "Log In"
            userProfile.setImageResource(R.mipmap.profiledefault)
            userProfile.setBackgroundColor(Color.TRANSPARENT)
            nav_btn_login.text = "LOG IN"
            UserDataService.logOut()


        } else {
            var goToLogIn = Intent(this, activity_logIn::class.java)
            startActivity(goToLogIn)
        }

    }



    fun hideeyboard() {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        if (inputManager.isAcceptingText) {
            inputManager.hideSoftInputFromWindow(currentFocus.windowToken, 0)
        }

    }

}
