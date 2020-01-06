package com.maklumi.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.Window
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.maklumi.EntityConfig
import com.maklumi.MapManager
import com.maklumi.Utility
import com.maklumi.dialog.ConversationChoice
import com.maklumi.dialog.ConversationGraph
import com.maklumi.json
import ktx.actors.onClick
import ktx.json.fromJson
import com.badlogic.gdx.scenes.scene2d.ui.List as ListBox


class ConversationUI : Window("dialog", Utility.STATUSUI_SKIN, "solidbackground") {

    val listBox = ListBox<ConversationChoice>(Utility.STATUSUI_SKIN)
    private val dialogTextLabel = Label("No Conversation", Utility.STATUSUI_SKIN)
    var graph = ConversationGraph()
    var currentEntityID: String = ""
    private val closeButton = TextButton("X", Utility.STATUSUI_SKIN)

    init {
        dialogTextLabel.setWrap(true)
        dialogTextLabel.setAlignment(Align.center)

        val scrollPane = ScrollPane(listBox, Utility.STATUSUI_SKIN, "inventoryPane")
        scrollPane.setOverscroll(false, false)
        scrollPane.fadeScrollBars = false
        scrollPane.setScrollingDisabled(true, false)
        scrollPane.setForceScroll(true, false)
        scrollPane.setScrollBarPositions(false, true)
        scrollPane.setScrollbarsOnTop(true)

        closeButton.onClick {
            MapManager.clearCurrentSelectedEntity()
        }

        add()
        add(closeButton)
        row()

        defaults().expand().fill()
        add(dialogTextLabel).pad(10f, 10f, 10f, 10f)
        row()
        add(scrollPane).pad(10f, 10f, 10f, 10f)

//        debug()
        pack()

        listBox.addListener(
                object : ClickListener() {
                    override fun clicked(event: InputEvent, x: Float, y: Float) {
                        val choice = listBox.selected ?: return
                        updateConversationDialog(choice.destinationId)
                        graph.notify(graph, choice.conversationCommandEvent)
                    }
                }
        )
    }

    fun loadConversation(entityConfig: EntityConfig) {
        val fullFilenamePath = entityConfig.conversationConfigPath
        if (fullFilenamePath.isEmpty() || !Gdx.files.internal(fullFilenamePath).exists()) {
            println("ConversationUI-66: Conversation file for ${entityConfig.entityID} does not exist!")
            return
        }
        dialogTextLabel.setText("")
        listBox.clearItems()

        val graph = json.fromJson<ConversationGraph>(Gdx.files.internal(fullFilenamePath))
        currentEntityID = entityConfig.entityID
        setConversationGraph(graph)
        titleLabel.setText(currentEntityID)
    }

    private fun setConversationGraph(graph: ConversationGraph) {
        this.graph.conversationGraphObservers.clear()
        this.graph = graph
        updateConversationDialog(graph.currentConversationID)
    }

    fun updateConversationDialog(id: String) {
        val conversation = graph.getConversationByID(id) ?: return
        graph.currentConversationID = id
        dialogTextLabel.setText(conversation.dialog)
        val choices = graph.currentChoices()
        if (choices != null) listBox.setItems(*choices.toTypedArray())
        listBox.selectedIndex = -1
    }

}
