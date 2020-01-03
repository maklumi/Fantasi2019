package com.maklumi.dialog

class ConversationChoice {
    var sourceId: String = ""
    var destinationId: String = ""
    var choicePhrase: String = ""

    override fun toString(): String = choicePhrase
}