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
        val str = StringBuilder()
        val item = slot.topItem
        str.append(item.itemShortDescription)
        if (item.isInventoryItemOffensive()) {
            str.append(System.getProperty("line.separator"))
            str.append(String.format("Attack Points: %s", item.itemUseTypeValue))
        }
        if (item.isInventoryItemDefensive()) {
            str.append(System.getProperty("line.separator"))
            str.append(String.format("Defence Points: %s", item.itemUseTypeValue))
        }
        str.append(System.getProperty("line.separator"))
        str.append(String.format("Original Value: %s GP", item.itemValue))
        str.append(System.getProperty("line.separator"))
        str.append(String.format("Trade Value: %s GP", item.tradeInValue()))
        description.setText(if (slot.hasItem()) str.toString() else "")
        pack()
    }
}