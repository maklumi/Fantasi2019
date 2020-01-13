package com.maklumi.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.List
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Window
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Array
import com.maklumi.MapManager
import com.maklumi.Utility
import com.maklumi.json
import com.maklumi.quest.QuestGraph
import ktx.actors.onClick
import ktx.json.fromJson

class QuestUI : Window("Quest Log", Utility.STATUSUI_SKIN, "solidbackground") {

    private val listQuests = List<QuestGraph>(skin)
    private val listTasks = List<String>(skin)

    val quests = Array<QuestGraph>()

    init {

        val questLabel = Label("Quests:", skin)
        add(questLabel).align(Align.left)

        val tasksLabel = Label("Tasks:", skin)
        add(tasksLabel).align(Align.left)
        row()

        val scrollPane = ScrollPane(listQuests, skin, "inventoryPane")
        scrollPane.setOverscroll(false, false)
        scrollPane.fadeScrollBars = false
        scrollPane.setForceScroll(true, false)
        add(scrollPane).padRight(5f)

        val scrollPaneTasks = ScrollPane(listTasks, skin, "inventoryPane")
        scrollPaneTasks.setOverscroll(false, false)
        scrollPaneTasks.fadeScrollBars = false
        scrollPaneTasks.setForceScroll(true, false)
        add(scrollPaneTasks).padLeft(5f)
        defaults().expand().fill()

//        debug()
        pack()

        //Listeners
        listQuests.onClick {
            populateQuestDialog(listQuests.selected)
        }
    }

    fun loadQuest(questConfigPath: String): QuestGraph? {
        if (questConfigPath.isEmpty() || !Gdx.files.internal(questConfigPath).exists()) {
            println("QuestUI62: Quest file does not exist!")
            return null
        }

        val graph = json.fromJson<QuestGraph>(Gdx.files.internal(questConfigPath))
        if (doesQuestAlreadyExist(graph.questID)) return null
        quests.add(graph)
        updateQuestsItemList()
        return graph
    }

    fun isQuestReadyForReturn(questID: String): Boolean {
        if (questID.isEmpty()) {
            println("QuestUI69: QuestID $questID not valid")
            return false
        }

        if (!doesQuestAlreadyExist(questID)) return false

        val graph = getQuestByID(questID) ?: return false

        if (graph.updateQuestForReturn()) {
            graph.isQuestComplete = "true"
        } else {
            return false
        }
        return true
    }

    fun getQuestByID(questGraphID: String): QuestGraph? {
        return quests.firstOrNull { it.questID.equals(questGraphID, true) }
    }

    private fun doesQuestAlreadyExist(questID: String): Boolean {
        return quests.any { it.questID.equals(questID, true) }
    }

    private fun updateQuestsItemList() {
        listQuests.clearItems()
        listQuests.setItems(quests)
        listQuests.selectedIndex = -1
    }

    private fun populateQuestDialog(graph: QuestGraph) {
        listTasks.clearItems()
        val tasks = graph.allQuestTasks().map { it.taskPhrase }.toTypedArray()
        listTasks.setItems(*tasks)
        listTasks.selectedIndex = -1
    }

    fun initQuests() {
        MapManager.clearAllMapQuestEntities()
        //populate items if quests have them
        for (quest in quests) {
            if (!quest.isQuestComplete.toBoolean()) {
                quest.init()
            }
        }
        updateQuestsItemList()
    }

    fun updateQuests() {
        for (quest in quests) {
            if (!quest.isQuestComplete.toBoolean()) {
                quest.update()
            }
        }
        updateQuestsItemList()
    }

    companion object {
        const val RETURN_QUEST = "conversations/return_quest.json"
        const val FINISHED_QUEST = "conversations/quest_finished.json"
    }
}