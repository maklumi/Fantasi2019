package com.maklumi

import com.badlogic.gdx.utils.Json

interface Component {

    enum class MESSAGE {
        INIT_START_POSITION,
        CURRENT_POSITION,
        CURRENT_DIRECTION,
        CURRENT_STATE,
        COLLISION_WITH_MAP,
        LOAD_ANIMATIONS,
        INIT_DIRECTION,
        INIT_STATE,
        COLLISION_WITH_ENTITY,
    }

    fun receiveMessage(message: String)

}

const val MESSAGE_TOKEN = ":::::"

val json = Json()