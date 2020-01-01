package com.maklumi.ui

import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Cell
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.Window
import com.maklumi.InventoryItem.ItemTypeID
import com.maklumi.InventoryItem.ItemUseType
import com.maklumi.InventoryItemFactory
import com.maklumi.MapManager
import com.maklumi.profile.ProfileEvent
import com.maklumi.profile.ProfileManager
import com.maklumi.profile.ProfileObserver
import com.maklumi.ui.StatusUI.Companion.textureAtlas
import com.badlogic.gdx.utils.Array as gdxArray

class InventoryUI : Window("Inventory Window", StatusUI.skin, "solidbackground"),
        ProfileObserver {

    private val lengthSlotRow = 10
    private val dragAndDrop = MyDragAndDrop()

    companion object {
        private const val itemsTextureAtlasPath = "skins/items.atlas"
        val itemsTextureAtlas = TextureAtlas(itemsTextureAtlasPath)
    }

    private val inventorySlotTable = Table()
    private val equipSlots = Table()
    private val playerSlotTable = Table()
    val tooltip = InventorySlotTooltip()

    private val slotWidth = 52f
    private val slotHeight = 52f

    init {
        // create 50 slots in inventory table
        for (i in 1..50) {
            val inventorySlot = InventorySlot()
            inventorySlot.addListener(InventorySlotTooltipListener(tooltip))
            dragAndDrop.addTarget(InventorySlotTarget(inventorySlot))
            inventorySlotTable.add(inventorySlot).size(slotWidth, slotHeight)
            if (i % lengthSlotRow == 0) inventorySlotTable.row()
        }

        // player inventory
        val headSlot = InventorySlot(ItemUseType.ARMOR_HELMET(), Image(itemsTextureAtlas.findRegion("inv_helmet")))

        val armFilter = ItemUseType.WEAPON_ONEHAND() or
                ItemUseType.WEAPON_TWOHAND() or
                ItemUseType.ARMOR_SHIELD() or
                ItemUseType.WAND_ONEHAND() or
                ItemUseType.WAND_TWOHAND()

        val leftArmSlot = InventorySlot(armFilter, Image(itemsTextureAtlas.findRegion("inv_weapon")))

        val rightArmSlot = InventorySlot(armFilter, Image(itemsTextureAtlas.findRegion("inv_shield")))

        val chestSlot = InventorySlot(ItemUseType.ARMOR_CHEST(), Image(itemsTextureAtlas.findRegion("inv_chest")))

        val legsSlot = InventorySlot(ItemUseType.ARMOR_FEET(), Image(itemsTextureAtlas.findRegion("inv_boot")))

        headSlot.addListener(InventorySlotTooltipListener(tooltip))
        leftArmSlot.addListener(InventorySlotTooltipListener(tooltip))
        rightArmSlot.addListener(InventorySlotTooltipListener(tooltip))
        chestSlot.addListener(InventorySlotTooltipListener(tooltip))
        legsSlot.addListener(InventorySlotTooltipListener(tooltip))

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

        playerSlotTable.background = Image(NinePatch(textureAtlas.createPatch("dialog"))).drawable
        playerSlotTable.add(equipSlots)
        add(playerSlotTable).padBottom(20f).row()
        add(inventorySlotTable).row()
        pack()
    }
/*
    fun populateInventory(itemTypeIDs: gdxArray<ItemTypeID>) {
        val cells: gdxArray<Cell<Actor>> = inventorySlotTable.cells
        for ((i, itemTypeID) in itemTypeIDs.withIndex()) {
            val inventorySlot = cells[i].actor as InventorySlot
            val inventoryItem = InventoryItemFactory.getInventoryItem(itemTypeID)
            inventorySlot.add(inventoryItem)
            dragAndDrop.addSource(InventorySlotSource(inventorySlot))
        }
    }*/

    private fun populateInventory(targetTable: Table, inventoryItems: gdxArray<InventoryItemLocation>) {
        val cells: gdxArray<Cell<Actor>> = targetTable.cells
        for (i in 0 until inventoryItems.size) {
            val (locationIndex, itemType, numItems) = inventoryItems[i]
            val itemTypeId = ItemTypeID.valueOf(itemType)
            val inventorySlot = cells[locationIndex].actor as InventorySlot
            inventorySlot.clearAllInventoryItems()

            for (index in 0 until numItems) {
                inventorySlot.add(InventoryItemFactory.getInventoryItem(itemTypeId))
                dragAndDrop.addSource(InventorySlotSource(inventorySlot))
            }
        }
    }

    private fun getInventoryAt(targetTable: Table): gdxArray<InventoryItemLocation> {
        val cells: gdxArray<Cell<Actor>> = targetTable.cells
        val items = gdxArray<InventoryItemLocation>()
        for (index in 0 until cells.size) {
            if (cells[index].actor == null) continue
            val slot = cells[index].actor as InventorySlot
            val numItems = slot.numItems
            if (numItems > 0)
                items.add(InventoryItemLocation(index, slot.topItem.itemTypeID.toString(), numItems))
        }
        return items
    }

    /*
    fun testAllItemLoad() {
        val array = gdxArray<ItemTypeID>()
        for (itemTypeId in ItemTypeID.values()) {
            if (itemsTextureAtlas.findRegion("$itemTypeId") == null) {
                println("InventoryUI-103: Need texture for $itemTypeId")
                continue
            }
            array.add(itemTypeId)
        }
        populateInventory(array)
    }
    */

    override fun onNotify(event: ProfileEvent) {
        when (event) {
            ProfileEvent.PROFILE_LOADED -> {
                // inventory slot
                val inventory = ProfileManager.getProperty<gdxArray<InventoryItemLocation>>("playerInventory")
                if (inventory != null && inventory.size > 0) {
                    populateInventory(inventorySlotTable, inventory)
                } else {
                    //add default items if nothing is found
                    val items: gdxArray<ItemTypeID> = MapManager.player.entityConfig.inventory
                    println("InvUI ${items.size}")
                    val itemLocations = gdxArray<InventoryItemLocation>()
                    for (i in 0 until items.size) {
                        itemLocations.add(InventoryItemLocation(i, items.get(i).toString(), 1))
                    }
                    populateInventory(inventorySlotTable, itemLocations)
                }

                // equip slot
                val equipInventory = ProfileManager.getProperty<gdxArray<InventoryItemLocation>>("playerEquipInventory")
                if (equipInventory != null && equipInventory.size > 0) {
                    populateInventory(equipSlots, equipInventory)
                }
            }
            ProfileEvent.SAVING_PROFILE -> {
                ProfileManager.setProperty("playerInventory", getInventoryAt(inventorySlotTable))
                ProfileManager.setProperty("playerEquipInventory", getInventoryAt(equipSlots))
            }
        }
    }
}