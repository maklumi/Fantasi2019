package com.maklumi.ui

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.maklumi.ui.MyDragAndDrop.*
import com.maklumi.ui.MyDragAndDrop.Target


class InventorySlotSource(var sourceSlot: InventorySlot) : Source(sourceSlot.topItem) {

    override fun dragStart(event: InputEvent?, x: Float, y: Float, pointer: Int): Payload? {
        if (actor.parent == null) return null
        sourceSlot = actor.parent as InventorySlot
        sourceSlot.reduceItemCount()

        val payload = Payload()
        payload.dragActor = actor // i.e the sourceSlot.topItem
        return payload
    }

    override fun dragStop(event: InputEvent?, x: Float, y: Float, pointer: Int, payload: Payload?, target: Target?) {
        if (target == null) sourceSlot.add(payload!!.dragActor)
    }

    override fun drag(event: InputEvent?, x: Float, y: Float, pointer: Int) {
    }
}