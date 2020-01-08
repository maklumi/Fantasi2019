package com.maklumi.ui

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Array
import com.maklumi.MapManager
import com.maklumi.Utility
import com.maklumi.json
import com.maklumi.ui.StoreInventoryObserver.StoreInventoryEvent.PLAYER_GP_TOTAL_UPDATED
import com.maklumi.ui.StoreInventoryObserver.StoreInventoryEvent.PLAYER_INVENTORY_UPDATED
import ktx.actors.onClick

class StoreInventoryUI : Window("Inventory Transaction", Utility.STATUSUI_SKIN, "solidbackground"),
        InventorySlotObserver,
        StoreInventorySubject {

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
    override val storeInventoryObservers = Array<StoreInventoryObserver>()
    var playerTotal = 0
        set(value) {
            field = value
            playerTotalGPLabel.setText("$playerTotalStr : $playerTotal $gp")
        }
    private val playerTotalStr = "Player Total"
    private val playerTotalGPLabel = Label("", Utility.STATUSUI_SKIN)

    init {
//        setFillParent(true)

        for (i in 1..30) {
            val slot = InventorySlot()
            slot.addListener(InventorySlotTooltipListener(inventorySlotTooltip))
            slot.inventorySlotObservers.add(this)
            slot.name = storeInv

            dragAndDrop.addTarget(InventorySlotTarget(slot))
            inventorySlotTable.name = storeInv
            inventorySlotTable.add(slot).size(52f, 52f)
            if (i % 10 == 0) inventorySlotTable.row()
        }
        enableButton(sellButton, false)
        enableButton(buyButton, false)
        buttonTable.defaults().expand().fill()
        buttonTable.add(sellButton).padLeft(10f).padRight(10f)
        buttonTable.add()
        buttonTable.add(buyButton).padLeft(10f).padRight(10f)

        sellTotalLabel.setAlignment(Align.center)
        buyTotalLabel.setAlignment(Align.center)
        totalLabels.defaults().expand().fill()
        totalLabels.add(sellTotalLabel).padLeft(40f).padRight(40f)
        totalLabels.add(buyTotalLabel).padLeft(40f).padRight(40f)

        for (i in 1..InventoryUI.numSlots) {
            val slot = InventorySlot()
            slot.addListener(InventorySlotTooltipListener(inventorySlotTooltip))
            slot.inventorySlotObservers.add(this)
            slot.name = playerInv

            dragAndDrop.addTarget(InventorySlotTarget(slot))
            playerInventorySlotTable.name = playerInv
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
        row()
        add(playerTotalGPLabel)
        pack()

        closeButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                this@StoreInventoryUI.isVisible = false
                MapManager.clearCurrentSelectedEntity()
            }
        })

        buyButton.onClick {
            if (fullValue !in 1..playerTotal) return@onClick
            playerTotal -= fullValue
            fullValue = 0
            buyTotalLabel.setText("$buy : $fullValue $gp")
            enableButton(buyButton, false)
            if (tradeInVal > 0) enableButton(sellButton, true) else enableButton(sellButton, false)
            notify(playerTotal.toString(), PLAYER_GP_TOTAL_UPDATED)

            // update the owner of the items
            InventoryUI.nameInventoryItemWith(playerInventorySlotTable, playerInv)
            savePlayerInventory()
        }

        sellButton.onClick {
            playerTotal += tradeInVal
            tradeInVal = 0
            sellTotalLabel.setText("$sell : $tradeInVal $gp")
            enableButton(sellButton, false)
            if (playerTotal >= fullValue) enableButton(buyButton, true) else enableButton(buyButton, false)
            notify(playerTotal.toString(), PLAYER_GP_TOTAL_UPDATED)

            // remove sold item
            inventorySlotTable.cells
                    .mapNotNull { it.actor as InventorySlot }
                    .filter { slot -> slot.hasItem() && slot.topItem.name == playerInv }
                    .forEach { slot -> slot.clearAllInventoryItems(false) }

            savePlayerInventory()
        }
    }

    override fun onNotify(slot: InventorySlot, event: InventorySlotObserver.SlotEvent) {
        when (event) {
            InventorySlotObserver.SlotEvent.ADDED_ITEM -> {
                if (slot.topItem.name == playerInv && slot.name == storeInv) {
                    tradeInVal += slot.topItem.tradeInValue()
                    sellTotalLabel.setText("$sell : $tradeInVal $gp")
                    if (tradeInVal > 0) {
                        enableButton(sellButton, true)
                    }
                }

                if (slot.topItem.name == storeInv && slot.name == playerInv) {
                    fullValue += slot.topItem.itemValue
                    buyTotalLabel.setText("$buy : $fullValue $gp")
                    if (fullValue > 0) {
                        enableButton(buyButton, true)
                    }
                }
            }

            InventorySlotObserver.SlotEvent.REMOVED_ITEM -> {
                if (slot.topItem.name == playerInv && slot.name == storeInv) {
                    tradeInVal -= slot.topItem.tradeInValue()
                    sellTotalLabel.setText("$sell : $tradeInVal $gp")
                    if (tradeInVal <= 0) {
                        enableButton(sellButton, false)
                    }
                }

                if (slot.topItem.name == storeInv && slot.name == playerInv) {
                    fullValue -= slot.topItem.itemValue
                    buyTotalLabel.setText("$buy : $fullValue $gp")
                    if (fullValue <= 0) {
                        enableButton(buyButton, false)
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

    private fun enableButton(button: Button, enable: Boolean) {
        if (enable) {
            button.touchable = Touchable.enabled
            button.isDisabled = false
        } else {
            button.touchable = Touchable.disabled
            button.isDisabled = true
        }
    }

    private fun savePlayerInventory() {
        // player items
        val inPlayerInventory = InventoryUI.getInventoryAt(playerInventorySlotTable, playerInv)
        val inStoreInventory = InventoryUI.getInventoryAt(playerInventorySlotTable, inventorySlotTable, playerInv)
        inPlayerInventory.addAll(inStoreInventory)
        notify(json.toJson(inPlayerInventory), PLAYER_INVENTORY_UPDATED)
    }

}