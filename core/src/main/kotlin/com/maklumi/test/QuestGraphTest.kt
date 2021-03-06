package com.maklumi.test

import com.badlogic.gdx.math.GridPoint2
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonWriter
import com.maklumi.AnimationConfig
import com.maklumi.AnimationType
import com.maklumi.Entity
import com.maklumi.EntityConfig
import com.maklumi.quest.QuestGraph
import com.maklumi.quest.QuestTask
import com.maklumi.quest.QuestTask.QuestTaskPropertyType.*
import com.maklumi.quest.QuestTaskDependency
import java.util.*
import com.badlogic.gdx.utils.Array as gdxArray

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

//        println(graph.toString())
//        println(graph.toJson())

        questTasks.clear()
        graph.clear()

        val q1 = QuestTask()
        q1.id = "1"
        q1.taskPhrase = "Come back to me with the items"
        q1.taskProperties.put(IS_TASK_COMPLETE.toString(), "FALSE")
        q1.taskProperties.put(TARGET_TYPE.toString(), "TOWN_FOLK02")
        q1.taskProperties.put(TARGET_LOCATION.toString(), "TOWN")
        q1.questType = QuestTask.QuestType.RETURN

        val q2 = QuestTask()
        q2.id = "2"
        q2.taskPhrase = "Collect 5 horns"
        q2.taskProperties.put(IS_TASK_COMPLETE.toString(), "FALSE")
        q2.taskProperties.put(TARGET_TYPE.toString(), "HORNS01")
        q2.taskProperties.put(TARGET_NUM.toString(), "5")
        q2.taskProperties.put(TARGET_LOCATION.toString(), "TOP_WORLD")
        q2.questType = QuestTask.QuestType.FETCH

        val q3 = QuestTask()
        q3.id = "3"
        q3.taskPhrase = "Collect 5 furs"
        q3.taskProperties.put(IS_TASK_COMPLETE.toString(), "FALSE")
        q3.taskProperties.put(TARGET_TYPE.toString(), "FUR01")
        q3.taskProperties.put(TARGET_NUM.toString(), "5")
        q3.taskProperties.put(TARGET_LOCATION.toString(), "TOP_WORLD")
        q3.questType = QuestTask.QuestType.FETCH

        val q4 = QuestTask()
        q4.id = "4"
        q4.taskPhrase = "Find the area where the Tuskan beast feasts"
        q4.taskProperties.put(IS_TASK_COMPLETE.toString(), "FALSE")
        q4.taskProperties.put(TARGET_TYPE.toString(), "BEAST_AREA")
        q4.taskProperties.put(TARGET_LOCATION.toString(), "TOP_WORLD")
        q4.questType = QuestTask.QuestType.DISCOVER

        questTasks[q1.id] = q1
        questTasks[q2.id] = q2
        questTasks[q3.id] = q3
        questTasks[q4.id] = q4

        graph.questTitle = "Beast Feast Leftovers"
        graph.questID = "1"
        graph.isQuestComplete = "FALSE"
        graph.questTasks = questTasks

        val qDep1 = QuestTaskDependency()
        qDep1.sourceId = q1.id
        qDep1.destinationId = q2.id

        val qDep2 = QuestTaskDependency()
        qDep2.sourceId = q1.id
        qDep2.destinationId = q3.id

        val qDep3 = QuestTaskDependency()
        qDep3.sourceId = q2.id
        qDep3.destinationId = q4.id

        val qDep4 = QuestTaskDependency()
        qDep4.sourceId = q3.id
        qDep4.destinationId = q4.id

        graph.addDependency(qDep1)
        graph.addDependency(qDep2)
        graph.addDependency(qDep3)
        graph.addDependency(qDep4)

//        println(graph.toJson())

        questTasks.clear()
        graph.clear()

        val q01 = QuestTask()
        q01.id = "1"
        q01.taskPhrase = "Come back to me with the herbs"
        q01.taskProperties.put(IS_TASK_COMPLETE.toString(), "FALSE")
        q01.taskProperties.put(TARGET_TYPE.toString(), "TOWN_FOLK1")
        q01.taskProperties.put(TARGET_LOCATION.toString(), "TOWN")
        q01.questType = QuestTask.QuestType.RETURN

        val q02 = QuestTask()
        q02.id = "2"
        q02.taskPhrase = "Please collect 5 herbs for cooking"
        q02.taskProperties.apply {
            put(IS_TASK_COMPLETE.toString(), "FALSE")
            put(TARGET_TYPE.toString(), "scripts/quest_herbs.json")
            put(TARGET_NUM.toString(), "5")
            put(TARGET_LOCATION.toString(), "TOWN")
        }
        q02.questType = QuestTask.QuestType.FETCH

        questTasks[q01.id] = q01
        questTasks[q02.id] = q02

        graph.questTitle = "Herbs for Cooking"
        graph.questID = "2"
        graph.isQuestComplete = "FALSE"
        graph.questTasks = questTasks

        val qDep01 = QuestTaskDependency()
        qDep01.sourceId = q01.id
        qDep01.destinationId = q02.id

        graph.addDependency(qDep01)

        println(graph.toJson())

        // lets do quest's herb entity
        val pathArray = gdxArray<String>()
        pathArray.add("sprites/items/Food.png")
        val gp = gdxArray<GridPoint2>()
        gp.add(GridPoint2(3, 2))
        val animConfig = AnimationConfig(1.01f, AnimationType.IMMOBILE, pathArray, gp)
        val herb = EntityConfig()
        herb.apply {
            entityID = "HERB001"
            state = Entity.State.IMMOBILE
            direction = Entity.Direction.UP
            this.animationConfig.add(animConfig)
            inventory = gdxArray()
            conversationConfigPath = ""
            questConfigPath = ""
        }
        val j = Json()
        j.setOutputType(JsonWriter.OutputType.json)
//        println(j.prettyPrint(herb))

    }
}