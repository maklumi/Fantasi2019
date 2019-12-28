package com.maklumi.ui

import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.Window
import com.maklumi.ui.StatusUI.Companion.textureAtlas

class InventoryUI : Window("Inventory Window", StatusUI.skin, "solidbackground") {

    private val lengthSlotRow = 10
    private val dragAndDrop = MyDragAndDrop()
    private val itemsTextureAtlasPath = "skins/items.atlas"
    private val itemsTextureAtlas = TextureAtlas(itemsTextureAtlasPath)

    init {
        setFillParent(true)

        // bottom inventory
        val inventorySlotTable = Table()

        // create 50 slots in inventory table
        for (i in 1..50) {
            val inventorySlot = InventorySlot()
            dragAndDrop.addTarget(InventorySlotTarget(inventorySlot))

            if (i == 5 || i == 10 || i == 15 || i == 20) {
                val image = Image(itemsTextureAtlas.findRegion("armor01"))
                inventorySlot.add(image)

                dragAndDrop.addSource(InventorySlotSource(inventorySlot))
            }

            inventorySlotTable.add(inventorySlot).size(52f, 52f)
            if (i % lengthSlotRow == 0) inventorySlotTable.row()
        }

        // player inventory
        val playerSlotTable = Table()
        playerSlotTable.add(Image(NinePatch(textureAtlas.createPatch("dialog")))).size(200f, 250f)

        add(playerSlotTable).padBottom(20f).row()
        add(inventorySlotTable).row()
        pack()
    }
}