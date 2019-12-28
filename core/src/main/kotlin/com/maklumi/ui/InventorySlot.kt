package com.maklumi.ui

import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Stack
import com.badlogic.gdx.utils.Align
import kotlin.properties.Delegates

class InventorySlot : Stack() {

    //All slots have this default image
    private val imageBackground = Image(NinePatch(StatusUI.textureAtlas.createPatch("dialog")))
    var itemCount: Int by Delegates.observable(0) { _, _, newValue ->
        itemChanged(newValue)
    }
    private val numItemsLabel = Label("$itemCount", StatusUI.skin, "inventory-item-count")

    // used in drag Source, actor cannot be null
    val topItem: Actor
        get() = if (children.size > 2) children.peek() else Actor()


    init {
        add(imageBackground) // first item
        numItemsLabel.setAlignment(Align.bottomLeft)
        numItemsLabel.isVisible = true
        add(numItemsLabel) // second item
    }

    override fun add(actor: Actor?) {
        super.add(actor)
        itemCount++
    }

    private fun itemChanged(newValue: Int) {
        numItemsLabel.setText(newValue - 2)
        numItemsLabel.isVisible = newValue > 2
    }

}