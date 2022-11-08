package tasklist

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.ToJson
import kotlinx.datetime.LocalDateTime


class TaskHeadlineAdapter {

    @ToJson
    fun toJson(taskHeadline: TaskHeadline): String {
        val date = "${taskHeadline.taskDeadline.date}"
        val time = taskHeadline.getTimePrint()
        return "$date|$time|${taskHeadline.taskPriority}"
    }

    @FromJson
    fun fromJson(taskHeadline: String): TaskHeadline? {

        return try {
            val dateTimeAndPriority = taskHeadline.split("|")

            val date = dateTimeAndPriority[0]
            val time = dateTimeAndPriority[1]
            val priority = dateTimeAndPriority[2]

            val yearMonthAndDay = date.split("-")
            val hourAndMinute = time.split(":")

            val localDateTime = LocalDateTime(
                yearMonthAndDay[0].toInt(),
                yearMonthAndDay[1].toInt(),
                yearMonthAndDay[2].toInt(),
                hourAndMinute[0].toInt(),
                hourAndMinute[1].toInt()
            )

            TaskHeadline(localDateTime, Priority.valueOf(priority))

        } catch (e: Exception) {
            throw JsonDataException("Could not parse TaskHeadline")
        }

    }

}