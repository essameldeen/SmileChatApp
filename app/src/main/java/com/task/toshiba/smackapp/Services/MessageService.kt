package com.task.toshiba.smackapp.Services

import android.content.Context
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.task.toshiba.smackapp.Controllers.App
import com.task.toshiba.smackapp.Model.Channel
import com.task.toshiba.smackapp.Model.Message
import com.task.toshiba.smackapp.Utilits.URL_GET_CHANNELS
import com.task.toshiba.smackapp.Utilits.URL_GET_MESSAGES
import org.json.JSONException

object MessageService {

    var Channels = ArrayList<Channel>()
    var messages = ArrayList<Message>()

    fun getAllChannels(complete: (Boolean) -> Unit) {
        clearChannels()
        val getAllChannelRequest = object : JsonArrayRequest(Method.GET, URL_GET_CHANNELS, null, Response.Listener {
            try {
                for (itArray in 0 until it.length()) {
                    var obj = it.getJSONObject(itArray)
                    var name = obj.getString("name")
                    var description = obj.getString("description")
                    var id = obj.getString("_id")
                    val tempChannel = Channel(name, description, id)
                    this.Channels.add(tempChannel)
                }
                complete(true)

            } catch (e: JSONException) {
                Log.d("ERROR", "EXC: ${e.localizedMessage}")
                complete(false)
            }

        }, Response.ErrorListener {
            Log.d("ERROR", "Error Get Channels $it")
            complete(false)
        }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }


            override fun getHeaders(): MutableMap<String, String> {
                var header = HashMap<String, String>()
                header.put("Authorization", "Bearer " + App.sharedPreferences.authToken)
                return header
            }

        }
        App.sharedPreferences.volley.add(getAllChannelRequest)
    }

    fun getAllMessages(channelId: String, complete: (Boolean) -> Unit) {
        var url = "$URL_GET_MESSAGES$channelId"
        val getAllMessageRequest = object : JsonArrayRequest(Method.GET, url, null, Response.Listener {
            clearMessage()
            try {

                for (itArray in 0 until it.length()) {
                    var message = it.getJSONObject(itArray)
                    var name = message.getString("userName")
                    var messageBody = message.getString("messageBody")
                    var id = message.getString("_id")
                    var channelId = message.getString("channelId")
                    var userAvatar = message.getString("userAvatar")
                    var userAvatarColor = message.getString("userAvatarColor")
                    var timeStamp = message.getString("timeStamp")
                    val tempMessage = Message(messageBody, name, channelId, userAvatar, userAvatarColor, timeStamp, id)
                    this.messages.add(tempMessage)
                }
                complete(true)

            } catch (e: JSONException) {
                Log.d("ERROR", "EXC: ${e.localizedMessage}")
                complete(false)
            }

        }, Response.ErrorListener {
            Log.d("ERROR", "Error Get Message $it")
            complete(false)
        }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }


            override fun getHeaders(): MutableMap<String, String> {
                var header = HashMap<String, String>()
                header.put("Authorization", "Bearer " + App.sharedPreferences.authToken)
                return header
            }

        }
        App.sharedPreferences.volley.add(getAllMessageRequest)
    }

    fun clearMessage() {
        this.messages.clear()
    }

    fun clearChannels() {
        this.Channels.clear()
    }


}