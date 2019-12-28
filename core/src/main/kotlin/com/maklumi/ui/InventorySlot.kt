package com.maklumi.ui

import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Stack
import com.badlogic.gdx.utils.Align
import com.maklumi.InventoryItem
import kotlin.properties.Delegates
import com.badlogic.gdx.utils.Array as gdxArray

class InventorySlot : Stack() {

    //All slots have this default image
    private val imageBackground = Image(NinePatch(StatusUI.textureAtlas.createPatch("dialog")))
    var itemCount: Int by Delegates.observable(0) { _, _, newValue ->
        itemChanged(newValue)
    }
    private val numItemsLabel = Label("$itemCount", StatusUI.skin, "inventory-item-count")

    // used in drag Source, actor cannot be null
    val topItem: InventoryItem
        get() = if (hasItem()) children.peek() as InventoryItem else InventoryItem()


    init {
        add(imageBackground) // first item
        numItemsLabel.setAlignment(Align.bottomLeft)
        numItemsLabel.isVisible = false
        add(numItemsLabel) // second item
    }

    override fun add(actor: Actor?) {
        super.add(actor)
        if (actor != imageBackground && actor != numItemsLabel) itemCount++
    }

    fun add(actors: gdxArray<Actor>) {
        actors.forEach { add(it) }
    }

    fun hasItem(): Boolean = children.size > 2

    fun getAllInventoryItems(): gdxArray<Actor> {
        val items = gdxArray<Actor>()
        while (hasItem()) {
            items.add(children.pop())
            itemCount--
        }
        return items
    }

    private fun itemChanged(newValue: Int) {
        numItemsLabel.setText(newValue)
        numItemsLabel.isVisible = newValue > 0
    }

    companion object {
        fun swapSlots(source: InventorySlot, target: InventorySlot, dragActor: Actor) {
            val tempArray = source.getAllInventoryItems()
            tempArray.add(dragActor)
            source.add(target.getAllInventoryItems())
            target.add(tempArray)
        }
    }

}