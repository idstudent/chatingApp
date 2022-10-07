package com.example.chatapp.util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class Util {
    fun getLastMessageTime(time: String): String {
        val currentTime = LocalDateTime.now().atZone(TimeZone.getDefault().toZoneId()) //현재 시각
        val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")

        val messageMonth: Int =
            time.substring(4, 6).toInt()
        val messageDate = time.substring(6, 8).toInt()
        val messageHour = time.substring(8, 10).toInt()
        val messageMinute = time.substring(10, 12).toInt()

        val formattedCurrentTimeString =
            currentTime.format(dateTimeFormatter)
        val currentMonth = formattedCurrentTimeString.substring(4, 6).toInt()
        val currentDate = formattedCurrentTimeString.substring(6, 8).toInt()
        val currentHour = formattedCurrentTimeString.substring(8, 10).toInt()
        val currentMinute = formattedCurrentTimeString.substring(10, 12).toInt()

        val monthAgo = currentMonth - messageMonth
        val dayAgo = currentDate - messageDate
        val hourAgo = currentHour - messageHour
        val minuteAgo = currentMinute - messageMinute

        if (monthAgo > 0)
            return monthAgo.toString() + "개월 전"
        else {
            return if (dayAgo > 0) {
                if (dayAgo == 1)
                    "어제"
                else
                    dayAgo.toString() + "일 전"
            } else {
                if (hourAgo > 0)
                    hourAgo.toString() + "시간 전"
                else {
                    if (minuteAgo > 0)
                        minuteAgo.toString() + "분 전"
                    else
                        "방금"
                }
            }
        }
    }
}