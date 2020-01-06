package com.maklumi.dialog

import com.maklumi.dialog.ConversationGraphObserver.ConversationCommandEvent
import com.maklumi.dialog.ConversationGraphObserver.ConversationCommandEvent.*

class ConversationChoice {
    var sourceId: String = ""
    var destinationId: String = ""
    var choicePhrase: String = ""
    var conversationCommandEvent: ConversationCommandEvent = NONE

    override fun toString(): String = choicePhrase
}