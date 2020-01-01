package com.maklumi.ui

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Window
import com.maklumi.Utility.STATUSUI_SKIN

class InventorySlotTooltip : Window("", STATUSUI_SKIN) {

    private val description = Label("", skin, "default") //"inventory-item-count"

    init {
        add(description).padLeft(5f).padRight(5f)
        pack()
        isVisible = false
    }

    fun setVisible(slot: InventorySlot, visible: Boolean) {
        super.setVisible(visible)
        if (!slot.hasItem()) super.setVisible(false)
    }

    fun updateDescription(slot: InventorySlot) {
        description.setText(if (slot.hasItem()) slot.topItem.itemShortDescription else "")
        pack()
    }
}