package com.maklumi.ui

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.Window
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Array
import com.maklumi.MapManager
import com.maklumi.Utility

class StoreInventoryUI : Window("Inventory Transaction", Utility.STATUSUI_SKIN, "solidbackground"),
        InventorySlotObserver {

    private val storeInv = "Store_Inventory"
    private val playerInv = "Player_Inventory"
    private val sell = "SELL"
    private val buy = "BUY"
    private val gp = " GP"
    private var tradeInVal = 0
    private var fullValue = 0

    private val inventorySlotTable = Table()
    private val inventorySlotTooltip = InventorySlotTooltip()
    private val dragAndDrop = MyDragAndDrop()
    private val closeButton = TextButton("X", Utility.STATUSUI_SKIN)
    private val buttonTable = Table()
    private val sellButton = TextButton(sell, Utility.STATUSUI_SKIN, "inventory")
    private val buyButton = TextButton(buy, Utility.STATUSUI_SKIN, "inventory")
    private val totalLabels = Table()
    private val sellTotalLabel = Label("$sell : $tradeInVal $gp", Utility.STATUSUI_SKIN)
    private val buyTotalLabel = Label("$buy : $fullValue $gp", Utility.STATUSUI_SKIN)

    private val playerInventorySlotTable = Table()
    val inventoryActors = Array<Actor>()

    init {
//        setFillParent(true)

        for (i in 1..30) {
            val slot = InventorySlot()
            slot.addListener(InventorySlotTooltipListener(inventorySlotTooltip))
            slot.inventorySlotObservers.add(this)
            slot.name = storeInv

            dragAndDrop.addTarget(InventorySlotTarget(slot))
            inventorySlotTable.add(slot).size(52f, 52f)
            if (i % 10 == 0) inventorySlotTable.row()
        }

        sellButton.isDisabled = true
        sellButton.touchable = Touchable.disabled
        buyButton.isDisabled = true
        buyButton.touchable = Touchable.disabled
        buttonTable.defaults().expand().fill()
        buttonTable.add(sellButton).padLeft(10f).padRight(10f)
        buttonTable.add(buyButton).padLeft(10f).padRight(10f)

        sellTotalLabel.setAlignment(Align.center)
        buyTotalLabel.setAlignment(Align.center)
        totalLabels.defaults().expand().fill()
        totalLabels.add(sellTotalLabel).padLeft(40f)
        totalLabels.add(buyTotalLabel).padRight(40f)

        for (i in 1..InventoryUI.numSlots) {
            val slot = InventorySlot()
            slot.addListener(InventorySlotTooltipListener(inventorySlotTooltip))
            slot.inventorySlotObservers.add(this)
            slot.name = playerInv

            dragAndDrop.addTarget(InventorySlotTarget(slot))
            playerInventorySlotTable.add(slot).size(52f, 52f)
            if (i % 10 == 0) playerInventorySlotTable.row()
        }

        inventoryActors.add(inventorySlotTooltip)

        // layout
//        debug()
//        defaults().expand().fill()
        add(Label("Store's inventory", skin)).padLeft(10f)
        add(closeButton).align(Align.right)
        row()
        add(inventorySlotTable).pad(10f, 10f, 10f, 10f)
        row()
        add(buttonTable)
        row()
        add(totalLabels)
        row()
        add(Label("Player's inventory", skin)).padLeft(10f)
        row()
        add(playerInventorySlotTable).pad(10f, 10f, 10f, 10f)
        pack()

        closeButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                this@StoreInventoryUI.isVisible = false
                MapManager.clearCurrentSelectedEntity()
            }
        })
    }

    override fun onNotify(slot: InventorySlot, event: InventorySlotObserver.SlotEvent) {
        when (event) {
            InventorySlotObserver.SlotEvent.ADDED_ITEM -> {
                if (slot.topItem.name == playerInv && slot.name == storeInv) {
                    tradeInVal += slot.topItem.tradeInValue()
                    sellTotalLabel.setText("$sell : $tradeInVal $gp")
                    if (tradeInVal > 0) {
                        sellButton.isDisabled = false
                        sellButton.touchable = Touchable.enabled
                    }
                }

                if (slot.topItem.name == storeInv && slot.name == playerInv) {
                    fullValue += slot.topItem.itemValue
                    buyTotalLabel.setText("$buy : $fullValue $gp")
                    if (fullValue > 0) {
                        buyButton.isDisabled = false
                        buyButton.touchable = Touchable.enabled
                    }
                }
            }

            InventorySlotObserver.SlotEvent.REMOVED_ITEM -> {
                if (slot.topItem.name == playerInv && slot.name == storeInv) {
                    tradeInVal -= slot.topItem.tradeInValue()
                    sellTotalLabel.setText("$sell : $tradeInVal $gp")
                    if (tradeInVal <= 0) {
                        sellButton.isDisabled = true
                        sellButton.touchable = Touchable.disabled
                    }
                }

                if (slot.topItem.name == storeInv && slot.name == playerInv) {
                    fullValue -= slot.topItem.itemValue
                    buyTotalLabel.setText("$buy : $fullValue $gp")
                    if (fullValue <= 0) {
                        buyButton.isDisabled = true
                        buyButton.touchable = Touchable.disabled
                    }
                }
            }
        }
    }

    fun loadStoreInventory(items: Array<InventoryItemLocation>) {
        InventoryUI.populateInventory(inventorySlotTable, items, dragAndDrop)
    }

    fun loadPlayerInventory(items: Array<InventoryItemLocation>) {
        InventoryUI.populateInventory(playerInventorySlotTable, items, dragAndDrop)
    }
}