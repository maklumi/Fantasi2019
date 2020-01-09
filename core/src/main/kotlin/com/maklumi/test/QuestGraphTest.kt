package com.maklumi.test

import com.maklumi.quest.QuestGraph
import com.maklumi.quest.QuestTask
import com.maklumi.quest.QuestTaskDependency
import java.util.*

object QuestGraphTest {
    private val questTasks = Hashtable<String, QuestTask>()
    private val graph = QuestGraph()

    @JvmStatic
    fun main(args: Array<String>) {
        val firstTask = QuestTask()
        firstTask.id = "500"
        firstTask.taskPhrase = "Come back to me with the bones"

        val secondTask = QuestTask()
        secondTask.id = "601"
        secondTask.taskPhrase = "Pickup 5 bones from the Isle of Dolgor"

        questTasks[firstTask.id] = firstTask
        questTasks[secondTask.id] = secondTask

        graph.questTitle = "Pikachu"
        graph.questTasks = questTasks

        val firstDep = QuestTaskDependency()
        firstDep.sourceId = firstTask.id
        firstDep.destinationId = secondTask.id

        val cycleDep = QuestTaskDependency()
        cycleDep.sourceId = secondTask.id
        cycleDep.destinationId = firstTask.id

        graph.addDependency(firstDep)
        graph.addDependency(cycleDep)

        println(graph.toString())
        println(graph.toJson())
    }
}