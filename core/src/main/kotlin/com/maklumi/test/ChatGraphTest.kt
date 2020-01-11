package com.maklumi.test

import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonWriter
import com.maklumi.dialog.Conversation
import com.maklumi.dialog.ConversationChoice
import com.maklumi.dialog.ConversationGraph
import com.maklumi.dialog.ConversationGraphObserver.ConversationCommandEvent.ADD_ENTITY_TO_INVENTORY
import com.maklumi.dialog.ConversationGraphObserver.ConversationCommandEvent.EXIT_CONVERSATION
import java.util.*

object ChatGraphTest {
    private var chats = Hashtable<String, Conversation>()
    private lateinit var graph: ConversationGraph

    private var prompt = ""

    @JvmStatic
    fun main(args: Array<String>) {
        val start = Conversation()
        start.id = "500"
        start.dialog = "Do you want to play a game?"
//        start.choicePhrase = "Go to beginning"

        val yesAnswer = Conversation()
        yesAnswer.id = "601"
        yesAnswer.dialog = "BOOM! Bombs dropping everywhere"
//        yesAnswer.choicePhrase = "YES"

        val noAnswer = Conversation()
        noAnswer.id = "802"
        noAnswer.dialog = "Too bad!"
//        noAnswer.choicePhrase = "NO"

        val unconnected = Conversation()
        unconnected.id = "250"
        unconnected.dialog = "I am unconnected"
//        unconnected.choicePhrase = "MUHAHAHAHA"

        chats[start.id] = start
        chats[noAnswer.id] = noAnswer
        chats[yesAnswer.id] = yesAnswer
        chats[unconnected.id] = unconnected

        graph = ConversationGraph(chats, start.id)

        val yesChoice = ConversationChoice()
        yesChoice.sourceId = start.id
        yesChoice.destinationId = yesAnswer.id
        yesChoice.choicePhrase = "YES"

        val noChoice = ConversationChoice()
        noChoice.sourceId = start.id
        noChoice.destinationId = noAnswer.id
        noChoice.choicePhrase = "NO"

        val startChoice = ConversationChoice()
        startChoice.sourceId = yesAnswer.id
        startChoice.destinationId = start.id
        startChoice.choicePhrase = "Go to beginning"

        val startChoice2 = ConversationChoice()
        startChoice2.sourceId = noAnswer.id
        startChoice2.destinationId = start.id
        startChoice2.choicePhrase = "Go to beginning"

        val unconnectedChoice = ConversationChoice()
        unconnectedChoice.sourceId = unconnected.id
        unconnectedChoice.choicePhrase = "MUHAHAHAHA"

        graph.addChoice(yesChoice)
        graph.addChoice(noChoice)
        graph.addChoice(startChoice)
        graph.addChoice(startChoice2)
        graph.addChoice(unconnectedChoice)

//        println(graph.toJson())
//        println(graph.toString())
//
//        println(graph.displayCurrentConversation())
//        while (prompt != "q") {
//            val nextChat = nextChoice()
//            if (nextChat != null) {
//                graph.currentConversationID = nextChat.id
//                println(graph.displayCurrentConversation())
//            }
//        }

        val first = Conversation()
        first.id = "1"
        first.dialog = "Would you like to pick up the item?"
        val second = Conversation()
        second.id = "2"
        second.dialog = "Ok"
        val third = Conversation()
        third.id = "3"
        third.dialog = "Ok"

        val chits = Hashtable<String, Conversation>()
        chits[first.id] = first
        chits[second.id] = second
        chits[third.id] = third

        val c1 = ConversationChoice()
        c1.sourceId = "1"
        c1.destinationId = "2"
        c1.choicePhrase = "Yes"
        c1.conversationCommandEvent = ADD_ENTITY_TO_INVENTORY
        val c2 = ConversationChoice()
        c2.sourceId = "1"
        c2.destinationId = "3"
        c2.choicePhrase = "No"
        c2.conversationCommandEvent = EXIT_CONVERSATION

        graph = ConversationGraph(chits, first.id)
        graph.addChoice(c1)
        graph.addChoice(c2)

        val j = Json()
        j.setOutputType(JsonWriter.OutputType.json)
        println(j.prettyPrint(graph))
    }

    private fun nextChoice(): Conversation? {
        val choices = graph.currentChoices() ?: return null
        for (chat in choices) {
            println("${chat.destinationId} ${chat.choicePhrase}")
        }

        prompt = readLine() ?: "q"
        if (prompt == "q") return null

        return try {
            graph.getConversationByID(prompt)
        } catch (nfe: NumberFormatException) {
            null
        }
    }
}
