package com.task.toshiba.smackapp.Controllers

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.task.toshiba.smackapp.R
import com.task.toshiba.smackapp.Services.AuthService
import kotlinx.android.synthetic.main.activity_log_in.*
import kotlinx.android.synthetic.main.layout_activity_register.*

class activity_logIn : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)
    }

    fun logInFun(view: View) {
        loginLoading.visibility = View.VISIBLE
        if (AuthService.userLogedIn) {
            mainPage()
        }
        AuthService.login(this, et_userName.text.toString(), et_passWord.text.toString(), complete = {
            loginLoading.visibility = View.INVISIBLE
            if (it) {
                mainPage()
            } else {
                Toast.makeText(this, "Failed To Log In ", Toast.LENGTH_LONG).show()
            }
        })

    }


    fun mainPage() {
        var mainActivity = Intent(this, MainActivity::class.java)
        startActivity(mainActivity)
        finish()
    }


    fun registerFun(view: View) {
        var register = Intent(this, activity_register::class.java)
        startActivity(register)
        finish()

    }
}
