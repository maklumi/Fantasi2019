package com.maklumi.ui

import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Stack
import com.badlogic.gdx.utils.Align
import com.maklumi.InventoryItem
import com.badlogic.gdx.utils.Array as gdxArray

class InventorySlot(
        private var filterItemType: Int = 0,
        private var customDecal: Image = Image()
) : Stack() {

    private val background = Stack()
    private val imageBackground = Image(NinePatch(StatusUI.textureAtlas.createPatch("dialog")))
    private var itemCount: Int = 0
    private val numItemsLabel = Label("$itemCount", StatusUI.skin, "inventory-item-count")
    val numItems: Int
        get() = maxOf(children.size - 2, 0)

    // used in drag Source, actor cannot be null
    val topItem: InventoryItem
        get() = if (hasItem()) children.peek() as InventoryItem else InventoryItem()


    init {
        background.add(imageBackground)
        background.add(customDecal)
        add(background) // first item
        numItemsLabel.setAlignment(Align.bottomRight)
        numItemsLabel.isVisible = false
        add(numItemsLabel) // second item
    }

    override fun add(actor: Actor?) {
        super.add(actor)
        if (actor != background && actor != numItemsLabel) increaseItemCount()
    }

    fun add(actors: gdxArray<Actor>) {
        actors.forEach { add(it) }
    }

    fun hasItem(): Boolean = children.size > 2

    fun getAllInventoryItems(): gdxArray<Actor> {
        val items = gdxArray<Actor>()
        while (hasItem()) {
            items.add(children.pop())
            reduceItemCount()
        }
        return items
    }

    fun doesAcceptItemUseType(itemUseType: Int): Boolean {
        return if (filterItemType == 0) true else filterItemType.and(itemUseType) == itemUseType
    }

    fun reduceItemCount() {
        itemCount--
        numItemsLabel.setText(itemCount)
        if (background.children.size == 1) background.children.add(customDecal)
        checkVisibilityOfItemCount()
    }

    fun clearAllInventoryItems() {
        if (hasItem()) {
            repeat(numItems) {
                children.pop()
                reduceItemCount()
            }
        }
    }

    private fun increaseItemCount() {
        itemCount++
        numItemsLabel.setText(itemCount)
        if (background.children.size > 1) background.children.pop()
        checkVisibilityOfItemCount()
    }

    private fun checkVisibilityOfItemCount() {
        numItemsLabel.isVisible = itemCount > 1
    }

    companion object {
        fun swapSlots(source: InventorySlot, target: InventorySlot, dragActor: InventoryItem) {
            //check if items can accept each other, otherwise, no swap
            if (!target.doesAcceptItemUseType(dragActor.itemUseType) ||
                    !source.doesAcceptItemUseType(target.topItem.itemUseType)) {
                source.add(dragActor)
                return
            }
            val tempArray = source.getAllInventoryItems()
            tempArray.add(dragActor)
            source.add(target.getAllInventoryItems())
            target.add(tempArray)
        }
    }

}