package com.maklumi.dialog

class ConversationGraph(
        private val conversations: MutableMap<Int, Conversation>,
        var currentConversation: Conversation
) {

    private val associatedChoices = mutableMapOf<Conversation, ArrayList<Conversation>>()

    private var numChoices = 0

    fun addChoice(sourceConversation: Conversation, targetConversation: Conversation) {
        var arrayList = associatedChoices[sourceConversation]
        if (arrayList == null) {
            associatedChoices[sourceConversation] = ArrayList()
            arrayList = associatedChoices[sourceConversation]!!
        }
        arrayList.add(targetConversation)
        numChoices++
    }

    fun currentChoices(): ArrayList<Conversation> = associatedChoices[currentConversation]!!

    fun getConversationByID(id: Int): Conversation? = conversations[id]

    override fun toString(): String {
        val builder = StringBuilder()
        builder.append("Number conversations: " + conversations.size + ", Number of choices:" + numChoices)
        builder.append(System.getProperty("line.separator"))
        for ((chat, arrayList) in associatedChoices) {
            builder.append(String.format("[%d]: ", chat.id))

            for (choice in arrayList) {
                builder.append(String.format("%d ", choice.id))
            }

            builder.append(System.getProperty("line.separator"))
        }
        return builder.toString()
    }
    /*
    Number conversations: 4, Number of choices:4
[500]: 601 802
[802]: 500
[601]: 500
[250]:
     */
}