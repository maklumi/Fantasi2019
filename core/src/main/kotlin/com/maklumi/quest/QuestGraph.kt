package com.maklumi.quest

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonWriter
import com.maklumi.Entity
import com.maklumi.Map
import com.maklumi.MapManager
import com.maklumi.profile.ProfileManager
import com.maklumi.quest.QuestTask.QuestTaskPropertyType.TARGET_LOCATION
import com.maklumi.quest.QuestTask.QuestTaskPropertyType.TARGET_TYPE
import com.maklumi.quest.QuestTask.QuestType.*
import java.util.*
import com.badlogic.gdx.utils.Array as gdxArray

class QuestGraph {
    var questTitle = "No quest title"
    var questTasks = Hashtable<String, QuestTask>()
    private val questTaskDependencies = Hashtable<String, ArrayList<QuestTaskDependency>>()
    var questID: String = ""
    var isQuestComplete: String = "false"

    fun update() {
        for (quest in allQuestTasks()) {
            //We first want to make sure the task is available and is relevant to current location
            if (isQuestTaskAvailable(quest.id)) continue

            val taskLocation = quest.taskProperties.get(TARGET_LOCATION.toString())
            if (!taskLocation.equals(MapManager.currentMapType.toString(), ignoreCase = true) ||
                    taskLocation.isNullOrEmpty()
            ) continue

            val taskConfig = quest.taskProperties.get(TARGET_TYPE.toString())
            if (taskConfig.isNullOrEmpty()) continue

            when (quest.questType) {
                FETCH -> {
                    val questItems = gdxArray<Entity>()
                    val positions = MapManager.getQuestItemSpawnPositions(questID, quest.id)
                    val config = Entity.getEntityConfig(taskConfig)
                    @Suppress("UNCHECKED_CAST")
                    val questPosition = ProfileManager.properties.get(config.itemTypeID.toString()) as gdxArray<Vector2>?
                    if (questPosition == null) {
                        for (pos in positions) {
                            val item = Map.initEntityNPC(pos, config)
                            questItems.add(item)
                        }
                    } else {
                        for (pos in questPosition) {
                            val item = Map.initEntityNPC(pos, config)
                            questItems.add(item)
                        }
                    }
                    MapManager.addMapQuestEntities(questItems)
                    ProfileManager.properties.put(config.itemTypeID.toString(), positions)
                    println("QuestGraph56 " + ProfileManager.properties.get(config.itemTypeID.toString()))
                }
                RETURN -> {
                }
                DISCOVER -> {
                }
                NOTYPE -> {
                }
            }
        }
    }

    private fun isQuestTaskAvailable(id: String): Boolean {
        getQuestTaskByID(id) ?: return false
        val list = questTaskDependencies[id] ?: return false
        for (dep in list) {
            val depTask = getQuestTaskByID(dep.destinationId) ?: continue
            if (depTask.isTaskComplete()) continue
            if (dep.sourceId.equals(id, true)) return false
        }
        return true
    }

    private fun getQuestTaskByID(id: String): QuestTask? {
        val task = questTasks[id]
        if (task == null) println("Id $id is not valid!")
        return task
    }

    fun allQuestTasks(): gdxArray<QuestTask> {
        val quests = gdxArray<QuestTask>()
        questTasks.values.forEach { quests.add(it) }
        return quests
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

    override fun toString(): String {
        return questTitle
    }

    fun toJson(): String {
        val j = Json()
        j.setOutputType(JsonWriter.OutputType.json)
        return j.prettyPrint(this)
    }
}
