package com.maklumi.ui

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener


class InventorySlotTooltipListener(private val tooltip: InventorySlotTooltip) : InputListener() {

    private var inside = false
    private val currentCoords = Vector2(0f, 0f)
    private val offset = Vector2(20f, 10f)

    override fun mouseMoved(event: InputEvent, x: Float, y: Float): Boolean {
        val inventorySlot = event.listenerActor as InventorySlot
        if (inside) {
            currentCoords.set(x, y)
            inventorySlot.localToStageCoordinates(currentCoords)
            tooltip.setPosition(currentCoords.x + offset.x, currentCoords.y + offset.y)
        }
        return true
    }

    override fun touchDragged(event: InputEvent, x: Float, y: Float, pointer: Int) {
        val inventorySlot = event.listenerActor as InventorySlot
        tooltip.setVisible(inventorySlot, false)
    }

    override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean {
        return true
    }

    override fun enter(event: InputEvent, x: Float, y: Float, pointer: Int, fromActor: Actor?) {
        inside = true
        val inventorySlot = event.listenerActor as InventorySlot
        currentCoords.set(x, y)
        inventorySlot.localToStageCoordinates(currentCoords)

        tooltip.updateDescription(inventorySlot)
        tooltip.setPosition(currentCoords.x + offset.x, currentCoords.y + offset.y)
        tooltip.toFront()
        tooltip.setVisible(inventorySlot, true)
    }

    override fun exit(event: InputEvent, x: Float, y: Float, pointer: Int, toActor: Actor?) {
        val inventorySlot = event.listenerActor as InventorySlot
        tooltip.setVisible(inventorySlot, false)
        inside = false

        currentCoords.set(x, y)
        inventorySlot.localToStageCoordinates(currentCoords)
    }

}

