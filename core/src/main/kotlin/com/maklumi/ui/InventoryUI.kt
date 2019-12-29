package com.maklumi.ui

import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.Window
import com.maklumi.InventoryItem
import com.maklumi.InventoryItem.ItemAttribute
import com.maklumi.InventoryItem.ItemType
import com.maklumi.ui.StatusUI.Companion.textureAtlas

class InventoryUI : Window("Inventory Window", StatusUI.skin, "solidbackground") {

    private val lengthSlotRow = 10
    private val dragAndDrop = MyDragAndDrop()
    private val itemsTextureAtlasPath = "skins/items.atlas"
    private val itemsTextureAtlas = TextureAtlas(itemsTextureAtlasPath)
    private val slotWidth = 52f
    private val slotHeight = 52f

    init {
        // bottom inventory
        val inventorySlotTable = Table()

        // create 50 slots in inventory table
        for (i in 1..50) {
            val inventorySlot = InventorySlot()
            dragAndDrop.addTarget(InventorySlotTarget(inventorySlot))

            if (i == 5 || i == 10 || i == 15 || i == 20) {
                val slotItem = InventoryItem(itemsTextureAtlas.findRegion("armor01"), ItemAttribute.WEARABLE(), "armor01", ItemType.ARMOR_CHEST())
                inventorySlot.add(slotItem)

                dragAndDrop.addSource(InventorySlotSource(inventorySlot))
            }

            if (i == 1 || i == 13 || i == 25 || i == 30) {
                val slotItem = InventoryItem(itemsTextureAtlas.findRegion("potions02"),
                        ItemAttribute.CONSUMABLE() or ItemAttribute.STACKABLE(), "potions02", ItemType.RESTORE_MP())
                inventorySlot.add(slotItem)

                dragAndDrop.addSource(InventorySlotSource(inventorySlot))
            }

            inventorySlotTable.add(inventorySlot).size(slotWidth, slotHeight)
            if (i % lengthSlotRow == 0) inventorySlotTable.row()
        }

        // player inventory
        val headSlot = InventorySlot(ItemType.ARMOR_HELMET(), Image(itemsTextureAtlas.findRegion("inv_helmet")))

        val armFilter = ItemType.WEAPON_ONEHAND() or
                ItemType.WEAPON_TWOHAND() or
                ItemType.ARMOR_SHIELD() or
                ItemType.WAND_ONEHAND() or
                ItemType.WAND_TWOHAND()

        val leftArmSlot = InventorySlot(armFilter, Image(itemsTextureAtlas.findRegion("inv_weapon")))

        val rightArmSlot = InventorySlot(armFilter, Image(itemsTextureAtlas.findRegion("inv_shield")))

        val chestSlot = InventorySlot(ItemType.ARMOR_CHEST(), Image(itemsTextureAtlas.findRegion("inv_chest")))

        val legsSlot = InventorySlot(ItemType.ARMOR_FEET(), Image(itemsTextureAtlas.findRegion("inv_boot")))

        dragAndDrop.addTarget(InventorySlotTarget(headSlot))
        dragAndDrop.addTarget(InventorySlotTarget(leftArmSlot))
        dragAndDrop.addTarget(InventorySlotTarget(chestSlot))
        dragAndDrop.addTarget(InventorySlotTarget(rightArmSlot))
        dragAndDrop.addTarget(InventorySlotTarget(legsSlot))

        val equipSlots = Table()
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

        val playerSlotTable = Table()
        playerSlotTable.background = Image(NinePatch(textureAtlas.createPatch("dialog"))).drawable
        playerSlotTable.add(equipSlots)
        add(playerSlotTable).padBottom(20f).row()
        add(inventorySlotTable).row()
        pack()
    }
}