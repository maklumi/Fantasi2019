package com.maklumi.dialog

interface ConversationGraphObserver {

    fun onNotify(graph: ConversationGraph, event: ConversationCommandEvent)

    enum class ConversationCommandEvent {
        LOAD_STORE_INVENTORY,
        EXIT_CONVERSATION,
        ACCEPT_QUEST,
        NONE,
        ADD_ENTITY_TO_INVENTORY,
        RETURN_QUEST
    }
}