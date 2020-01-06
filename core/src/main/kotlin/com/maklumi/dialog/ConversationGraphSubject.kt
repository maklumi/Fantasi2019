package com.maklumi.dialog

import com.badlogic.gdx.utils.Array

open class ConversationGraphSubject {

    val conversationGraphObservers = Array<ConversationGraphObserver>()

    fun notify(graph: ConversationGraph, event: ConversationGraphObserver.ConversationCommandEvent) {
        for (observer in conversationGraphObservers) {
            observer.onNotify(graph, event)
        }
    }
}
