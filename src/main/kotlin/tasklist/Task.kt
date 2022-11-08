package tasklist

class Task(val headLine: TaskHeadline, val tasks: MutableList<String> = mutableListOf()) {

    fun addTasks(_tasks: List<String>) {
        tasks.addAll(_tasks)
    }

    fun getIndexPrint(index: Int): String {
        return when (index) {
            in 0..9 -> {
                "$index  "
            }

            else -> {
                "$index "
            }
        }
    }

    private fun addTaskToSection(task: String): String {
        return task.padEnd(44)
    }

    fun printTaskLine(line: String) {
        print("|    |            |       |   |   |${addTaskToSection(line)}|\n")
    }

    fun print(index: Int) {

        val chunkedTasks = tasks.flatMap { task -> task.chunked(44) }

        val firstTask = chunkedTasks[0]
        val remainingTasks = chunkedTasks.subList(1, chunkedTasks.size)

        print("| ${getIndexPrint(index)}| ${headLine.taskDeadline.date} | ${headLine.getTimePrint()} | ${headLine.getPriorityColor()} | ${headLine.getDueTagColor()} |${addTaskToSection(firstTask)}|\n")

        remainingTasks.forEach { t -> (printTaskLine(t)) }
    }

}