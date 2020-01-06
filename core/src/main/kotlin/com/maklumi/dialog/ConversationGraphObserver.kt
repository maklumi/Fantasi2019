package com.maklumi.dialog

interface ConversationGraphObserver {

    fun onNotify(graph: ConversationGraph, event: ConversationCommandEvent)

    enum class ConversationCommandEvent {
        LOAD_STORE_INVENTORY,
        EXIT_CONVERSATION,
        NONE
    }
}