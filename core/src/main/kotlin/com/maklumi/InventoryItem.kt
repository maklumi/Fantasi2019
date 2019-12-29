package com.maklumi

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.Image

class InventoryItem(
        textureRegion: TextureRegion? = null,
        private var itemAttributes: Int = 0,
        private var itemID: String = "",
        var itemUseType: Int = 0
) : Image(textureRegion) {

    enum class ItemAttribute(private val value: Int) {
        CONSUMABLE(1),
        WEARABLE(2),
        STACKABLE(4);

        operator fun invoke(): Int = value
    }

    enum class ItemType(private val value: Int) {
        RESTORE_HEALTH(1), RESTORE_MP(2), DAMAGE(4),
        WEAPON_ONEHAND(8), WEAPON_TWOHAND(16), WAND_ONEHAND(32),
        WAND_TWOHAND(64), ARMOR_SHIELD(128), ARMOR_HELMET(256),
        ARMOR_CHEST(512), ARMOR_FEET(1024);

        operator fun invoke(): Int = value
    }

    fun isStackable(): Boolean {
        return itemAttributes.and(ItemAttribute.STACKABLE()) == ItemAttribute.STACKABLE()
    }

    fun isSameItemType(candidateInventoryItem: InventoryItem): Boolean {
        return itemID.equals(candidateInventoryItem.itemID, true)
    }

}