package com.maklumi.dialog

import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonWriter
import java.util.*

class ConversationGraph(
        private val conversations: Hashtable<String, Conversation> = Hashtable(),
        var currentConversationID: String = ""
) : ConversationGraphSubject() {

    private val associatedChoices = Hashtable<String, ArrayList<ConversationChoice>>()

    private fun currentConversation(): Conversation? = getConversationByID(currentConversationID)

    fun addChoice(conversationChoice: ConversationChoice) {
        var arrayList = associatedChoices[conversationChoice.sourceId]
        if (arrayList == null) {
            associatedChoices[conversationChoice.sourceId] = ArrayList()
            arrayList = associatedChoices[conversationChoice.sourceId]!!
        }
        arrayList.add(conversationChoice)
    }

    fun currentChoices(): ArrayList<ConversationChoice>? = associatedChoices[currentConversationID]

    fun getConversationByID(id: String): Conversation? {
        val conversation = conversations[id]
        return if (conversation == null) {
            println("Conversation $id is not valid.")
            null
        } else conversation
    }

    fun displayCurrentConversation(): String? = currentConversation()?.dialog

    override fun toString(): String {
        val numChoices = associatedChoices.size

        val builder = StringBuilder()
        builder.append("Number conversations: " + conversations.size + ", Number of choices:" + numChoices)
        builder.append(System.getProperty("line.separator"))
        for ((chat, arrayList) in associatedChoices) {
            builder.append(String.format("[%s]: ", chat))

            for (choice in arrayList) {
                builder.append(String.format("%s ", choice.destinationId))
            }

            builder.append(System.getProperty("line.separator"))
        }
        return builder.toString()
    }

    fun toJson(): String {
        val j = Json()
        j.setOutputType(JsonWriter.OutputType.minimal)
        return j.prettyPrint(this)
    }
}