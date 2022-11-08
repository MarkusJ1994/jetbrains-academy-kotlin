package tasklist

import com.squareup.moshi.*
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.datetime.*
import java.io.File


private val jsonFileName = "tasklist.json"

fun printInput(taskList: List<Task>): Boolean {
    return if (taskList.isEmpty()) {
        println("No tasks have been input")
        false
    } else {
        print(
            "+----+------------+-------+---+---+--------------------------------------------+\n" +
                    "| N  |    Date    | Time  | P | D |                   Task                     |\n" +
                    "+----+------------+-------+---+---+--------------------------------------------+\n"
        )
        taskList.forEachIndexed { index, elem ->
            run {
                elem.print(index + 1)
                print("+----+------------+-------+---+---+--------------------------------------------+\n")
            }
        }
        true
    }
}

fun getPriority(): Priority {
    println("Input the task priority (C, H, N, L):")
    val input = readLine().orEmpty().uppercase()

    return try {
        Priority.valueOf(input)
    } catch (e: IllegalArgumentException) {
        getPriority()
    }
}

fun getDeadlineDate(): LocalDate {
    println("Input the date (yyyy-mm-dd):")
    val input = readLine().orEmpty()

    return try {
        val yearMonthDay = input.split('-')
        LocalDate(yearMonthDay[0].toInt(), yearMonthDay[1].toInt(), yearMonthDay[2].toInt())
    } catch (e: Exception) {
        println("The input date is invalid")
        getDeadlineDate()
    }
}

fun getDeadlineTime(): Pair<Int, Int> {
    println("Input the time (hh:mm):")
    val input = readLine().orEmpty()

    return try {
        val hourAndMinute = input.split(':')
        LocalDateTime(1, 1, 1, hourAndMinute[0].toInt(), hourAndMinute[1].toInt())
        Pair(hourAndMinute[0].toInt(), hourAndMinute[1].toInt())
    } catch (e: Exception) {
        println("The input time is invalid")
        getDeadlineTime()
    }
}

fun getDeadline(
    deadlineDate: LocalDate = getDeadlineDate(),
    deadlineTime: Pair<Int, Int> = getDeadlineTime()
): LocalDateTime {
    return LocalDateTime(
        deadlineDate.year,
        deadlineDate.month,
        deadlineDate.dayOfMonth,
        deadlineTime.first,
        deadlineTime.second
    )
}

fun instantiateTask(): Task {
    val priority = getPriority()

    val deadLine: LocalDateTime = getDeadline()

    return Task(TaskHeadline(deadLine, priority))
}

fun addEntryToTask(task: Task): Task {
    println("Input a new task (enter a blank line to end):")

    val collectedInput: MutableList<String> = mutableListOf()

    var input = readLine().orEmpty()

    while (!input.isBlank()) {
        collectedInput.add(input.trim())
        input = readLine().orEmpty()
    }

    if (collectedInput.isEmpty()) {
        println("The task is blank")
    } else {
        task.addTasks(collectedInput)
    }
    return task
}

fun addTaskFromInput(): Task {

    val task = instantiateTask()

    return addEntryToTask(task)
}

fun getTaskNumberToModify(nrOfTasks: Int): Int {
    println("Input the task number (1-$nrOfTasks):")
    val input = readLine().orEmpty()

    return try {
        val taskNr = input.toInt()
        if (taskNr < 1 || taskNr > (nrOfTasks)) {
            throw IllegalArgumentException()
        }
        taskNr
    } catch (e: Exception) {
        println("Invalid task number")
        getTaskNumberToModify(nrOfTasks)
    }
}

fun deleteTaskFromInput(taskList: MutableList<Task>) {
    val taskToDelete: Int = getTaskNumberToModify(taskList.size)
    taskList.removeAt(taskToDelete - 1)
    println("The task is deleted")
}

fun getEditedTask(task: Task): Task {
    println("Input a field to edit (priority, date, time, task):")

    val input = readLine().orEmpty()

    return when (input.lowercase()) {
        "priority" -> {
            val newPrio = getPriority()
            Task(TaskHeadline(task.headLine.taskDeadline, newPrio), task.tasks)
        }

        "date" -> {
            val newDeadlineDate = getDeadline(deadlineTime = task.headLine.getTime())
            Task(TaskHeadline(newDeadlineDate, task.headLine.taskPriority), task.tasks)
        }

        "time" -> {
            val newDeadlineDate = getDeadline(deadlineDate = task.headLine.taskDeadline.date)
            Task(TaskHeadline(newDeadlineDate, task.headLine.taskPriority), task.tasks)
        }

        "task" -> {
            addEntryToTask(Task(task.headLine))
        }

        else -> {
            println("Invalid field")
            getEditedTask(task)
        }
    }
}

fun editTaskFromInput(taskList: MutableList<Task>) {

    val taskToEdit: Int = getTaskNumberToModify(taskList.size)

    val editedTask: Task = getEditedTask(taskList[taskToEdit - 1])

    taskList[taskToEdit - 1] = editedTask

    println("The task is changed")
}

fun endTaskListInput(taskList: List<Task>) {
    readToFile(taskList)
    println("Tasklist exiting!")
}

fun readInput(taskList: MutableList<Task>) {
    println("Input an action (add, print, edit, delete, end):")

    val input = readLine().orEmpty()

    when (input.lowercase()) {
        "end" -> {
            endTaskListInput(taskList)
        }

        "add" -> {
            taskList.add(addTaskFromInput())
            readInput(taskList)
        }

        "edit" -> {
            if (printInput(taskList.toList())) {
                editTaskFromInput(taskList)
            }
            readInput(taskList)
        }

        "delete" -> {
            if (printInput(taskList.toList())) {
                deleteTaskFromInput(taskList)
            }
            readInput(taskList)
        }

        "print" -> {
            printInput(taskList.toList())
            readInput(taskList)
        }

        else -> {
            println("The input action is invalid")
            readInput(taskList)
        }
    }
}

fun getMoshi(): Moshi {
    return Moshi.Builder().add(TaskHeadlineAdapter()).addLast(KotlinJsonAdapterFactory()).build()
}

@OptIn(ExperimentalStdlibApi::class)
fun readToFile(taskList: List<Task>) {
    val moshi: Moshi = getMoshi()
    val jsonAdapter: JsonAdapter<List<Task>> = moshi.adapter()

    val json: String = jsonAdapter.toJson(taskList)
    val jsonFile = File(jsonFileName)
    jsonFile.writeText(json)
}

@OptIn(ExperimentalStdlibApi::class)
fun readFromFile(): List<Task> {
    return try {
        val json = File(jsonFileName).readText()

        val moshi: Moshi = getMoshi()
        val jsonAdapter: JsonAdapter<List<Task>> = moshi.adapter()

        jsonAdapter.fromJson(json).orEmpty()
    } catch (e: Exception) {
        listOf()
    }

}

fun main() {
    readInput(readFromFile().toMutableList())
}


