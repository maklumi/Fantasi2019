package com.maklumi.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Window
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.maklumi.EntityConfig
import com.maklumi.Utility
import com.maklumi.dialog.ConversationChoice
import com.maklumi.dialog.ConversationGraph
import com.maklumi.dialog.UIObserver
import com.maklumi.dialog.UIObserver.UIEvent
import com.maklumi.dialog.UIObserver.UIEvent.*
import com.maklumi.json
import ktx.json.fromJson
import com.badlogic.gdx.scenes.scene2d.ui.List as ListBox


class ConversationUI : Window("dialog", Utility.STATUSUI_SKIN, "solidbackground"),
        UIObserver {

    private val listBox = ListBox<ConversationChoice>(Utility.STATUSUI_SKIN)
    private val dialogTextLabel = Label("No Conversation", Utility.STATUSUI_SKIN)
    private var graph = ConversationGraph()
    private var currentEntityID: String = ""

    init {
        dialogTextLabel.setWrap(true)
        dialogTextLabel.setAlignment(Align.center)

        val scrollPane = ScrollPane(listBox)
        scrollPane.setOverscroll(false, false)
        scrollPane.fadeScrollBars = false
        scrollPane.setScrollingDisabled(true, false)
        scrollPane.setScrollbarsOnTop(true)

        defaults().expand().fill()
        add(dialogTextLabel).pad(10f, 10f, 10f, 10f)
        row()
        add(scrollPane)

//        debug()
        pack()

        listBox.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                val choice = listBox.selected ?: return
                graph.currentConversationID = choice.destinationId
                dialogTextLabel.setText(graph.getConversationByID(choice.destinationId)!!.dialog)
                if (graph.currentChoices() != null) {
                    listBox.setItems(*graph.currentChoices()!!.toTypedArray())
                    listBox.selectedIndex = -1
                }
            }
        }
        )
    }

    private fun loadConversation(entityConfig: EntityConfig) {
        val fullFilenamePath = entityConfig.conversationConfigPath
        if (fullFilenamePath.isEmpty() || !Gdx.files.internal(fullFilenamePath).exists()) {
            println("ConversationUI-66: Conversation file for ${entityConfig.entityID} does not exist!")
            return
        }
        val graph = json.fromJson<ConversationGraph>(Gdx.files.internal(fullFilenamePath))
        setConversationGraph(graph)

        currentEntityID = entityConfig.entityID
    }

    private fun setConversationGraph(graph: ConversationGraph) {
        this.graph = graph
        val id = this.graph.currentConversationID
        val conversation = this.graph.getConversationByID(id) ?: return
        this.dialogTextLabel.setText(conversation.dialog)
        if (this.graph.currentChoices() != null)
            this.listBox.setItems(*this.graph.currentChoices()!!.toTypedArray())
        listBox.selectedIndex = -1
    }

    override fun onNotify(value: String, event: UIEvent) {
        val config = json.fromJson<EntityConfig>(value)
        when (event) {
            LOAD_CONVERSATION -> {
                loadConversation(config)
            }
            SHOW_CONVERSATION -> {
                if (config.entityID == currentEntityID) isVisible = true
            }
            HIDE_CONVERSATION -> {
                if (config.entityID == currentEntityID) isVisible = false
            }
        }
    }
}
