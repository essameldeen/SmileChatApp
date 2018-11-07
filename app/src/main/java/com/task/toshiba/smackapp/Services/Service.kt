package com.task.toshiba.smackapp.Services

import android.content.Context
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.JsonIOException
import com.task.toshiba.smackapp.Controllers.App
import com.task.toshiba.smackapp.Utilits.*
import org.json.JSONException
import org.json.JSONObject
import java.lang.reflect.Method


object AuthService {
    fun register( email: String, password: String, complete: (Boolean) -> Unit) {
        val responseBody = JSONObject()
        responseBody.put("email", email)
        responseBody.put("password", password)


        val requestBody = responseBody.toString()
        var registerRequest = object : StringRequest(Method.POST, URL_REGISTER, Response.Listener { response ->
            println(response)
            complete(true)

        }, Response.ErrorListener { error ->
            Log.d("ERROR", "error happen ${error.localizedMessage}")
            complete(false)

        }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }
        }
         App.sharedPreferences.volley.add(registerRequest)
    }

    fun login(  email: String, password: String, complete: (Boolean) -> Unit) {

        val jsonBody = JSONObject()
        jsonBody.put("email", email)
        jsonBody.put("password", password)
        val jsonRequest = jsonBody.toString()

        val loginRequest = object : JsonObjectRequest(Method.POST, URL_LOGIN, null, Response.Listener {
            try {
                App.sharedPreferences.authToken = it.getString("token")
                App.sharedPreferences.userEmail = it.getString("user")
                App.sharedPreferences.isLoggedIn = true
                complete(true)
            } catch (e: JSONException) {
                Log.d("JSON ERROR", "EXC = ${e.localizedMessage}")
                complete(false)
            }


        }, Response.ErrorListener
        {
            Log.d("ERROR", "Error in log in $it")
            complete(false)
        }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                return jsonRequest.toByteArray()
            }
        }
         App.sharedPreferences.volley.add(loginRequest)
    }

    fun createUser( name: String, email: String, avatarName: String, avatarColor: String, complete: (Boolean) -> Unit) {
        val jsonBody = JSONObject()
        jsonBody.put("name", name)
        jsonBody.put("email", email)
        jsonBody.put("avatarName", avatarName)
        jsonBody.put("avatarColor", avatarColor)

        val jsonRequest = jsonBody.toString()

        val createUserRequest = object : JsonObjectRequest(Method.POST, URL_CREATE_USER, null, Response.Listener {
            try {
                UserDataService.name = it.getString("name")
                UserDataService.email = it.getString("email")
                UserDataService.avatarName = it.getString("avatarName")
                UserDataService.avatarColor = it.getString("avatarColor")
                UserDataService.id = it.getString("_id")
                complete(true)

                complete(true)
            } catch (e: JSONException) {
                Log.d("JSON ERROR", "EXC = ${e.localizedMessage}")
                complete(false)
            }

        }, Response.ErrorListener { error ->
            Log.d("ERROR", "Error in add user $error")
            complete(false)

        }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                return jsonRequest.toByteArray()
            }

            override fun getHeaders(): MutableMap<String, String> {
                var header = HashMap<String, String>()
                header.put("Authorization", "Bearer ${App.sharedPreferences.authToken}")
                return header
            }
        }
         App.sharedPreferences.volley.add(createUserRequest)
    }

    fun findUserByEmail(contex:Context, complete: (Boolean) -> Unit) {

        val requestFindUser = object : JsonObjectRequest(Method.GET, URL_FIND_USER +  App.sharedPreferences.userEmail, null,
                Response.Listener {
                    try {
                        UserDataService.name = it.getString("name")
                        UserDataService.email = it.getString("email")
                        UserDataService.avatarName = it.getString("avatarName")
                        //UserDataService.avatarColor = it.getString("avatarColor")
                        UserDataService.id = it.getString("_id")


                        var brodCast = Intent(BROADCAST_USER_DATA_CHANGE)
                        LocalBroadcastManager.getInstance(contex).sendBroadcast(brodCast)

                        complete(true)

                    } catch (e: JSONException) {
                        Log.d("JSON ERROR", "EXC = ${e.localizedMessage}")
                        complete(false)
                    }

                }, Response.ErrorListener {
            Log.d("ERROR", "Error in Find user $it")
            complete(false)

        }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getHeaders(): MutableMap<String, String> {
                var header = HashMap<String, String>()
                header.put("Authorization", "Bearer ${App.sharedPreferences.authToken}" )
                return header
            }

        }

         App.sharedPreferences.volley.add(requestFindUser)
    }

}