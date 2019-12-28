package com.maklumi

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.Image

class InventoryItem(
        textureRegion: TextureRegion? = null,
        private var itemAttributes: Int = 0,
        private var itemID: String = ""
) : Image(textureRegion) {

    companion object {
        const val CONSUMABLE = 1
        const val WEARABLE = 2
        const val STACKABLE = 0b100 // 4
    }

    fun isStackable(): Boolean {
        return itemAttributes.and(STACKABLE) == STACKABLE
    }

    fun isSameItemType(candidateInventoryItem: InventoryItem): Boolean {
        return itemID.equals(candidateInventoryItem.itemID, true)
    }

}