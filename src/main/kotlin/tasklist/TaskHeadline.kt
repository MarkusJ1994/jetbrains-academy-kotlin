package tasklist

import kotlinx.datetime.*

enum class Priority {
    C, H, N, L
}

enum class DueTag {
    I, T, O
}

class TaskHeadline (val taskDeadline: LocalDateTime, val taskPriority: Priority) {

    private fun checkDueTag(): DueTag {
        val currentDate = Clock.System.now().toLocalDateTime(TimeZone.of("UTC+0")).date
        val numberOfDays = currentDate.daysUntil(taskDeadline.date)
        return when {
            numberOfDays > 0 -> DueTag.I
            numberOfDays < 0 -> DueTag.O
            else -> DueTag.T
        }
    }

    fun getTime(): Pair<Int, Int> {
        return Pair(taskDeadline.hour, taskDeadline.minute)
    }

    fun getTimePrint(): String {
        return String.format("%02d:%02d", taskDeadline.hour, taskDeadline.minute)
    }

    fun getPriorityColor(): String {
        return when(taskPriority) {
            Priority.C -> "\u001B[101m \u001B[0m"
            Priority.N -> "\u001B[102m \u001B[0m"
            Priority.H -> "\u001B[103m \u001B[0m"
            Priority.L -> "\u001B[104m \u001B[0m"
        }
    }

    fun getDueTagColor(): String {
        return when(checkDueTag()) {
            DueTag.O -> "\u001B[101m \u001B[0m"
            DueTag.I -> "\u001B[102m \u001B[0m"
            DueTag.T -> "\u001B[103m \u001B[0m"
        }
    }

    override fun toString(): String {
        return ("${taskDeadline.date} ${getTimePrint()} $taskPriority ${checkDueTag()}")
    }

}