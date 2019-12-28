package com.maklumi.ui

import com.maklumi.InventoryItem
import com.maklumi.ui.MyDragAndDrop.*
import com.maklumi.ui.MyDragAndDrop.Target


class InventorySlotTarget(private var targetSlot: InventorySlot) : Target(targetSlot) {

    override fun drag(source: Source?, payload: Payload?, x: Float, y: Float, pointer: Int): Boolean {
        return true
    }

    override fun drop(source: Source?, payload: Payload?, x: Float, y: Float, pointer: Int) {
        if (payload?.dragActor == null) return

        val sourceActor = payload.dragActor as InventoryItem
        val targetActor = targetSlot.topItem

        if (!targetSlot.hasItem()) {
            targetSlot.add(sourceActor)
        } else {
            //If the same item and stackable, add
            if (sourceActor.isStackable() && sourceActor.isSameItemType(targetActor)) {
                targetSlot.add(sourceActor)
            } else {
                //If they aren't the same items or the items aren't stackable, then swap
                InventorySlot.swapSlots((source as InventorySlotSource).sourceSlot, targetSlot, sourceActor)
            }
        }
    }

    override fun reset(source: Source?, payload: Payload?) {
    }
}