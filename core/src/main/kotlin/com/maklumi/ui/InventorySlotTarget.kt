package com.maklumi.ui

import com.maklumi.ui.MyDragAndDrop.Payload
import com.maklumi.ui.MyDragAndDrop.Source
import com.maklumi.ui.MyDragAndDrop.Target


class InventorySlotTarget(private var targetSlot: InventorySlot) : Target(targetSlot) {

    override fun drag(source: Source?, payload: Payload?, x: Float, y: Float, pointer: Int): Boolean {
        return true
    }

    override fun drop(source: Source?, payload: Payload?, x: Float, y: Float, pointer: Int) {
       if (payload?.dragActor == null) return

       targetSlot.add(payload.dragActor)
    }

    override fun reset(source: Source?, payload: Payload?) {
    }
}