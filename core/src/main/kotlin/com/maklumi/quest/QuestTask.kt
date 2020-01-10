package com.maklumi.quest

import com.badlogic.gdx.utils.ObjectMap

class QuestTask {
    var id: String = ""
    var taskPhrase: String = ""
    var questType: QuestType = QuestType.NOTYPE
    var taskProperties: ObjectMap<String, String> = ObjectMap()

    enum class QuestType {
        FETCH, RETURN, DISCOVER, NOTYPE
    }

    enum class QuestTaskPropertyType {
        IS_TASK_COMPLETE, TARGET_TYPE, TARGET_NUM, TARGET_LOCATION
    }

    fun isTaskComplete(): Boolean {
        val isComplete = taskProperties[QuestTaskPropertyType.IS_TASK_COMPLETE.toString()]
        if (isComplete == null) {
            taskProperties.put(QuestTaskPropertyType.IS_TASK_COMPLETE.toString(), "false")
            return false
        }
        return isComplete.toBoolean()
    }
}