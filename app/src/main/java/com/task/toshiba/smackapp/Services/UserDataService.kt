package com.task.toshiba.smackapp.Services

import android.graphics.Color
import java.util.*

object UserDataService {
    var name: String = ""
    var email: String = ""
    var avatarName: String = ""
    var avatarColor: String = ""
    var id: String = ""

    fun logOut() {
        name = ""
        email = ""
        avatarColor = ""
        avatarName = ""
        id = ""
        AuthService.userLogedIn = false
        AuthService.userEmail = ""
        AuthService.userToken = ""

    }

    fun convertStringColorToInt(colorString: String): Int {

        var color = colorString.replace("[", "")
                .replace("]", "")
                .replace(",", "")

        var r = 0
        var g = 0
        var b = 0

        var scanner = Scanner(color)
        if (scanner.hasNext()) {
            r = (scanner.nextDouble() * 255).toInt()
            g = (scanner.nextDouble() * 255).toInt()
            b = (scanner.nextDouble() * 255).toInt()

        }
        return Color.rgb(r, g, b)
    }

}