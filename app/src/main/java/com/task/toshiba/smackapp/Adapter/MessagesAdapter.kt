package com.task.toshiba.smackapp.Adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.ImageView
import android.widget.TextView
import com.task.toshiba.smackapp.Model.Message
import com.task.toshiba.smackapp.R
import com.task.toshiba.smackapp.Services.UserDataService
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class MessagesAdapter(val context: Context, val messages: List<Message>) : RecyclerView.Adapter<MessagesAdapter.ViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        var view = LayoutInflater.from(context).inflate(R.layout.single_message, p0, false)
        return ViewHolder(viewItem = view)
    }

    override fun getItemCount(): Int {
        return messages.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = messages.get(position)
        holder.bindData(item)

    }


    inner class ViewHolder(viewItem: View?) : RecyclerView.ViewHolder(viewItem!!) {

        var image = viewItem!!.findViewById<ImageView>(R.id.image)
        var name = viewItem!!.findViewById<TextView>(R.id.name)
        var time = viewItem!!.findViewById<TextView>(R.id.time)
        var messageBody = viewItem!!.findViewById<TextView>(R.id.message)

        fun bindData(message: Message) {
            var resourseID = context.resources.getIdentifier(message.avatarName, "mipmap", context.packageName)
            image?.setImageResource(resourseID)
            image?.setBackgroundColor(UserDataService.convertStringColorToInt(message.avatarColor))

            name?.text = message.userName
            time?.text = formateTime(message.timeStamp)
            messageBody?.text = message.message
        }

        fun formateTime(time: String): String {

            var formate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            formate.timeZone = TimeZone.getTimeZone("UTC")
            var convert = Date()
            try {
                convert = formate.parse(time)
            } catch (e: ParseException) {
                Log.d("Error", "exc to parse ")
            }
            val outDataString = SimpleDateFormat("E, h:mm a", Locale.getDefault())

            return outDataString.format(convert)
        }

    }
}