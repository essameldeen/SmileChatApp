package com.task.toshiba.smackapp.Controllers


import android.content.Intent
import android.graphics.Color
import android.opengl.Visibility
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.view.View
import android.widget.Toast
import com.task.toshiba.smackapp.R
import com.task.toshiba.smackapp.Services.AuthService
import com.task.toshiba.smackapp.Utilits.BROADCAST_USER_DATA_CHANGE
import kotlinx.android.synthetic.main.layout_activity_register.*
import java.util.*

class activity_register : AppCompatActivity() {

    var avtarImage = "profileDefault"
    var avtarColor = "[0.5,0.5,0.5,1]"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_activity_register)
    }

    fun createUserFun(view: View) {
        registerLoading.visibility = View.VISIBLE
        AuthService.register(email = et_email.text.toString(), password = et_password.text.toString(), complete = {
            if (it) {
                AuthService.login( email = et_email.text.toString(), password = et_password.text.toString(), complete = {
                    if (it) {
                        registerLoading.visibility = View.INVISIBLE
                        AuthService.createUser( et_name.text.toString(), et_email.text.toString(), avtarImage, avtarColor) {
                            if (it) {
                                sendBrodCast()
                                finish()
                            } else {
                                registerLoading.visibility = View.INVISIBLE
                                Toast.makeText(this, "Add  user  Failed", Toast.LENGTH_LONG).show()
                            }
                        }

                    } else {
                        registerLoading.visibility = View.INVISIBLE
                        Toast.makeText(this, "Log In  Error", Toast.LENGTH_LONG).show()
                    }
                })

            } else {
                registerLoading.visibility = View.INVISIBLE
                Toast.makeText(this, "Register Error", Toast.LENGTH_LONG).show()

            }
        })


    }

    private fun sendBrodCast() {

        val intent = Intent(BROADCAST_USER_DATA_CHANGE)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)


    }

    fun generateProfileFun(view: View) {
        val random = Random()
        val color = random.nextInt(2)
        val image = random.nextInt(28)
        if (color == 0) {
            avtarImage = "light$image"
        } else {
            avtarImage = "dark$image"

        }
        val resourseId = resources.getIdentifier(avtarImage, "mipmap", packageName)
        iv_profile.setImageResource(resourseId)
    }

    fun generateColorFun(view: View) {
        val random = Random()
        var r = random.nextInt(255)
        var g = random.nextInt(255)
        var b = random.nextInt(255)

        iv_profile.setBackgroundColor(Color.rgb(r, g, b))
        var saveR = r.toDouble() / 255
        var saveG = g.toDouble() / 255
        var saveB = b.toDouble() / 255
        avtarColor = "[$saveR,$saveG,$saveR,1]"
    }
}
