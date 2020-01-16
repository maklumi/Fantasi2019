package com.maklumi.ui

import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.maklumi.Entity
import com.maklumi.InventoryItem.ItemTypeID
import com.maklumi.InventoryItem.ItemUseType
import com.maklumi.InventoryItemFactory
import com.maklumi.MESSAGE_TOKEN
import com.maklumi.Utility.ITEMS_TEXTUREATLAS
import com.maklumi.Utility.STATUSUI_SKIN
import com.maklumi.Utility.STATUSUI_TEXTUREATLAS
import com.maklumi.ui.InventoryObserver.InventoryEvent
import com.maklumi.ui.InventoryObserver.InventoryEvent.UPDATED_AP
import com.maklumi.ui.InventoryObserver.InventoryEvent.UPDATED_DP
import com.maklumi.ui.InventorySlotObserver.SlotEvent.ADDED_ITEM
import com.maklumi.ui.InventorySlotObserver.SlotEvent.REMOVED_ITEM
import com.badlogic.gdx.utils.Array as gdxArray

class InventoryUI : Window("Inventory Window", STATUSUI_SKIN, "solidbackground"),
        InventorySlotObserver,
        InventorySubject {

    override val inventoryObservers = gdxArray<InventoryObserver>()

    private val lengthSlotRow = 10
    val dragAndDrop = MyDragAndDrop()
    val inventorySlotTable = Table()
    val equipSlots = Table()
    private val playerSlotTable = Table()
    val tooltip = InventorySlotTooltip()

    private val slotWidth = 52f
    private val slotHeight = 52f

    private var defVal = 0
        set(value) {
            field = value
            defValLabel.setText(field)
        }
    private val defValLabel = Label("", skin)
    private var atkVal = 0
        set(value) {
            field = value
            atkValLabel.setText(field)
        }
    private val atkValLabel = Label("$atkVal", skin)

    init {
        val defLabel = Label("Defense: ", skin)
        val atkLabel = Label("Attack : ", skin)
        val labelTable = Table()
        labelTable.add(defLabel).align(Align.left)
        labelTable.add(defValLabel).align(Align.center)
        labelTable.row()
        labelTable.row()
        labelTable.add(atkLabel).align(Align.left)
        labelTable.add(atkValLabel).align(Align.center)

        // create 50 slots in inventory table
        for (i in 1..numSlots) {
            val inventorySlot = InventorySlot()
            inventorySlot.addListener(InventorySlotTooltipListener(tooltip))
            dragAndDrop.addTarget(InventorySlotTarget(inventorySlot))
            inventorySlotTable.add(inventorySlot).size(slotWidth, slotHeight)
            inventorySlot.addListener(object : ClickListener() {
                override fun touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int) {
                    super.touchUp(event, x, y, pointer, button)
                    if (tapCount == 2) {
                        val slot = event.listenerActor as InventorySlot? ?: return
                        if (slot.hasItem()) {
                            val item = slot.topItem
                            if (item.isConsumable()) {
                                val itemInfo = item.itemUseType.toString() + MESSAGE_TOKEN + item.itemUseTypeValue
                                this@InventoryUI.notify(itemInfo, InventoryEvent.ITEM_CONSUMED)
                                slot.remove(item)
                            }
                        }
                    }
                }
            }
            )
            if (i % lengthSlotRow == 0) inventorySlotTable.row()
        }

        // player inventory
        val headSlot = InventorySlot(ItemUseType.ARMOR_HELMET(), Image(ITEMS_TEXTUREATLAS.findRegion("inv_helmet")))

        val armFilter = ItemUseType.WEAPON_ONEHAND() or
                ItemUseType.WEAPON_TWOHAND() or
                ItemUseType.ARMOR_SHIELD() or
                ItemUseType.WAND_ONEHAND() or
                ItemUseType.WAND_TWOHAND()

        val leftArmSlot = InventorySlot(armFilter, Image(ITEMS_TEXTUREATLAS.findRegion("inv_weapon")))

        val rightArmSlot = InventorySlot(armFilter, Image(ITEMS_TEXTUREATLAS.findRegion("inv_shield")))

        val chestSlot = InventorySlot(ItemUseType.ARMOR_CHEST(), Image(ITEMS_TEXTUREATLAS.findRegion("inv_chest")))

        val legsSlot = InventorySlot(ItemUseType.ARMOR_FEET(), Image(ITEMS_TEXTUREATLAS.findRegion("inv_boot")))

        headSlot.addListener(InventorySlotTooltipListener(tooltip))
        leftArmSlot.addListener(InventorySlotTooltipListener(tooltip))
        rightArmSlot.addListener(InventorySlotTooltipListener(tooltip))
        chestSlot.addListener(InventorySlotTooltipListener(tooltip))
        legsSlot.addListener(InventorySlotTooltipListener(tooltip))

        arrayOf(headSlot, leftArmSlot, rightArmSlot, chestSlot, legsSlot)
                .forEach { it.inventorySlotObservers.add(this) }

        dragAndDrop.addTarget(InventorySlotTarget(headSlot))
        dragAndDrop.addTarget(InventorySlotTarget(leftArmSlot))
        dragAndDrop.addTarget(InventorySlotTarget(chestSlot))
        dragAndDrop.addTarget(InventorySlotTarget(rightArmSlot))
        dragAndDrop.addTarget(InventorySlotTarget(legsSlot))


        equipSlots.defaults().space(10f)
        equipSlots.add()
        equipSlots.add(headSlot).size(slotWidth, slotHeight)
        equipSlots.row()

        equipSlots.add(leftArmSlot).size(slotWidth, slotHeight)
        equipSlots.add(chestSlot).size(slotWidth, slotHeight)
        equipSlots.add(rightArmSlot).size(slotWidth, slotHeight)
        equipSlots.row()

        equipSlots.add()
        equipSlots.right().add(legsSlot).size(slotWidth, slotHeight)

        playerSlotTable.background = Image(NinePatch(STATUSUI_TEXTUREATLAS.createPatch("dialog"))).drawable
        playerSlotTable.add(equipSlots)
        add(playerSlotTable).padBottom(20f)
        add(labelTable)
        row()
        add(inventorySlotTable).colspan(2)
        row()
        pack()
    }

    fun addEntityToInventory(entity: Entity, itemName: String) {
        val sourceCells = inventorySlotTable.cells
        val cell = sourceCells.first { it.actor != null && (it.actor as InventorySlot).numItems == 0 }
        val inventoryItem = InventoryItemFactory.getInventoryItem(ItemTypeID.valueOf(entity.entityConfig.entityID))
        inventoryItem.name = itemName
        val slot = cell.actor as InventorySlot
        slot.add(inventoryItem)
        dragAndDrop.addSource(InventorySlotSource(slot))
    }

    fun removeQuestItemFromInventory(questID: String) {
        val sourceCells = inventorySlotTable.cells
        for (index in 0 until sourceCells.size) {
            val inventorySlot = sourceCells.get(index).actor as InventorySlot? ?: continue
            val item = inventorySlot.topItem
            val inventoryItemName = item.name
            if (inventoryItemName != null && inventoryItemName == questID) {
                inventorySlot.clearAllInventoryItems(false)
            }
        }
    }

    fun doesInventoryHaveSpace(): Boolean {
        val sourceCells = inventorySlotTable.cells
        return sourceCells.any { it.actor != null && (it.actor as InventorySlot).numItems == 0 }
    }

    override fun onNotify(slot: InventorySlot, event: InventorySlotObserver.SlotEvent) {
        when (event) {
            ADDED_ITEM -> {
                val item = slot.topItem
                if (item.isInventoryItemOffensive()) {
                    atkVal += item.itemUseTypeValue
                    notify(atkVal.toString(), UPDATED_AP)
                }
                if (item.isInventoryItemDefensive()) {
                    defVal += item.itemUseTypeValue
                    notify(defVal.toString(), UPDATED_DP)
                }
            }
            REMOVED_ITEM -> {
                val item = slot.topItem
                if (item.isInventoryItemOffensive()) {
                    atkVal -= item.itemUseTypeValue
                    notify(atkVal.toString(), UPDATED_AP)
                }
                if (item.isInventoryItemDefensive()) {
                    defVal -= item.itemUseTypeValue
                    notify(defVal.toString(), UPDATED_DP)
                }
            }
        }
    }


    companion object {

        const val numSlots = 50
        const val PLAYER_INVENTORY = "Player_Inventory"
        const val STORE_INVENTORY = "Store_Inventory"

        fun populateInventory(targetTable: Table, inventoryItems: gdxArray<InventoryItemLocation>, dragAndDrop: MyDragAndDrop,
                              defaultName: String, disableNonDefaultItems: Boolean) {
            clearInventoryItemsAt(targetTable)
            val cells: gdxArray<Cell<Actor>> = targetTable.cells
            for (i in 0 until inventoryItems.size) {
                val (locationIndex, itemType, numItems, itemNameProperty) = inventoryItems[i]
                val itemTypeId = ItemTypeID.valueOf(itemType)
                val inventorySlot = cells[locationIndex].actor as InventorySlot
                inventorySlot.clearAllInventoryItems(true)

                for (index in 0 until numItems) {
                    val item = InventoryItemFactory.getInventoryItem(itemTypeId)
                    item.name = if (item.name.isNullOrEmpty()) defaultName else itemNameProperty
                    inventorySlot.add(item)
                    if (item.name.equals(defaultName, true))
                        dragAndDrop.addSource(InventorySlotSource(inventorySlot))
                    else if (!disableNonDefaultItems)
                        dragAndDrop.addSource(InventorySlotSource(inventorySlot))
                }
            }
        }

        fun getInventoryFiltered(targetTable: Table): gdxArray<InventoryItemLocation> {
            val cells: gdxArray<Cell<Actor>> = targetTable.cells
            val items = gdxArray<InventoryItemLocation>()
            for (index in 0 until cells.size) {
                if (cells[index].actor == null) continue
                val slot = cells[index].actor as InventorySlot
                val numItems = slot.numItems
                if (numItems > 0)
                    items.add(InventoryItemLocation(index, slot.topItem.itemTypeID.toString(), numItems,
                            slot.topItem.name))
            }
            return items
        }

        private fun clearInventoryItemsAt(table: Table) {
            table.cells.forEach {
                (it.actor as InventorySlot?)?.clearAllInventoryItems(false)
            }
        }

//        fun getInventoryAt(targetTable: Table, name: String): gdxArray<InventoryItemLocation> {
//            val array = gdxArray<InventoryItemLocation>()
//            for ((index, cell) in targetTable.cells.withIndex()) {
//                val slot = cell.actor as InventorySlot? ?: continue
//                val count = slot.getNumItems(name)
//                if (count > 0) array.add(InventoryItemLocation(index, slot.topItem.itemTypeID.toString(), count,
//                        slot.name))
//            }
//            return array
//        }

        fun getInventoryFiltered(playerTable: Table, storeTable: Table, filterOutName: String): gdxArray<InventoryItemLocation> {
            val targetArray = getInventoryFiltered(storeTable, filterOutName)
            val sourceCells = playerTable.cells
            var counter = 0
            for (item in targetArray) {
                for ((i, cell) in sourceCells.withIndex()) {
                    counter = i
                    val slot = cell.actor as InventorySlot? ?: continue
                    val numItems = slot.numItems
                    if (numItems == 0) {
                        item.locationIndex = i
                        counter++
                        break
                    }
                }
                // If we run out of room when buying items and still left items to be sold,
                // then we will stack remaining items in the last slot.
                if (counter == sourceCells.size) item.locationIndex = counter - 1
            }
            return targetArray
        }

        fun getInventoryFiltered(targetTable: Table, filterOutName: String): gdxArray<InventoryItemLocation> {
            val cells = targetTable.cells
            val items = gdxArray<InventoryItemLocation>()
            for (i in 0 until cells.size) {
                val inventorySlot = cells.get(i).actor as InventorySlot? ?: continue
                val numItems = inventorySlot.numItems
                if (numItems > 0) {
                    val topItemName = inventorySlot.topItem.name
                    if (topItemName.equals(filterOutName, ignoreCase = true)) continue
//                    println("[i] $i itemtype: " + inventorySlot.topItem.itemTypeID.toString() + " numItems " + numItems)
                    items.add(InventoryItemLocation(i, inventorySlot.topItem.itemTypeID.toString(), numItems,
                            inventorySlot.topItem.name))
                }
            }
            return items
        }

        fun nameInventoryItemWith(targetTable: Table, name: String) {
            targetTable.cells.forEach {
                (it.actor as InventorySlot?)?.nameAllInventoryItemsWith(name)
            }
        }

        fun removeInventoryItems(name: String, inventoryTable: Table) {
            inventoryTable.cells.forEach {
                val slot = it.actor as InventorySlot?
                slot?.removeAllInventoryItemsWith(name)
            }
        }
    }

}