package com.maklumi.test

import com.maklumi.dialog.Conversation
import com.maklumi.dialog.ConversationGraph

object ChatGraphTest {
    private var chats = mutableMapOf<Int, Conversation>()
    private lateinit var graph: ConversationGraph

    private var prompt = ""

    @JvmStatic
    fun main(args: Array<String>) {
        val start = Conversation()
        start.id = 500
        start.dialog = "Do you want to play a game?"
        start.choicePhrase = "Go to beginning"

        val yesAnswer = Conversation()
        yesAnswer.id = 601
        yesAnswer.dialog = "BOOM! Bombs dropping everywhere"
        yesAnswer.choicePhrase = "YES"

        val noAnswer = Conversation()
        noAnswer.id = 802
        noAnswer.dialog = "Too bad!"
        noAnswer.choicePhrase = "NO"

        val unconnected = Conversation()
        unconnected.id = 250
        unconnected.dialog = "I am unconnected"
        unconnected.choicePhrase = "MUHAHAHAHA"

        chats[start.id] = start
        chats[noAnswer.id] = noAnswer
        chats[yesAnswer.id] = yesAnswer
        chats[unconnected.id] = unconnected

        graph = ConversationGraph(chats, start)

        graph.addChoice(start, yesAnswer)
        graph.addChoice(start, noAnswer)
        graph.addChoice(noAnswer, start)
        graph.addChoice(yesAnswer, start)
        graph.addChoice(unconnected, unconnected)

        println(graph.toString())

        println(graph.currentConversation.dialog)
        while (prompt != "q") {
            val nextChat = nextChoice()
            if (nextChat != null) {
                graph.currentConversation = nextChat
                println(graph.currentConversation.dialog)
            }
        }
    }

    private fun nextChoice(): Conversation? {
        val choices = graph.currentChoices()
        for (chat in choices) {
            println("${chat.id} ${chat.choicePhrase}")
        }

        prompt = readLine() ?: "q"
        if (prompt == "q") return null

        return try {
            graph.getConversationByID(Integer.parseInt(prompt))
        } catch (nfe: NumberFormatException) {
            null
        }
    }
}
