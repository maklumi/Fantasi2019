package com.maklumi.quest

import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonWriter
import java.util.*

class QuestGraph {
    var questTitle = "No quest title"
    var questTasks = Hashtable<String, QuestTask>()
    private val questTaskDependencies = Hashtable<String, ArrayList<QuestTaskDependency>>()

    fun allQuestTasks(): Array<QuestTask> {
        return  questTasks.values.toTypedArray()
    }

    fun clear() {
        questTasks.clear()
        questTaskDependencies.clear()
    }

    fun addDependency(dependency: QuestTaskDependency) {
        var arrayList = questTaskDependencies[dependency.sourceId]
        if (arrayList == null) {
            questTaskDependencies[dependency.sourceId] = ArrayList()
            arrayList = questTaskDependencies[dependency.sourceId]!!
        }

        // will not add if creates cycles
        if (doesCycleExist(dependency)) {
            println("Cycle exists! Not adding dependency")
            return
        }

        arrayList.add(dependency)
    }

    private fun doesCycleExist(dependency: QuestTaskDependency): Boolean {
        return questTasks.keys.any { id ->
            doesQuestTaskHaveDependencies(id) && dependency.destinationId == id
        }
    }

    private fun doesQuestTaskHaveDependencies(id: String): Boolean {
        return !questTaskDependencies[id].isNullOrEmpty()
    }

//    private fun getQuestTaskByID(id: String): QuestTask? {
//        val task = questTasks[id]
//        if (task == null) println("Id $id is not valid!")
//        return task
//    }

    override fun toString(): String {
        return questTitle
    }

    fun toJson(): String {
        val j = Json()
        j.setOutputType(JsonWriter.OutputType.json)
        return j.prettyPrint(this)
    }
}
