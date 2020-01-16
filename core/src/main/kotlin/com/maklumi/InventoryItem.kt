package com.maklumi

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.maklumi.InventoryItem.ItemUseType.*

class InventoryItem(
        textureRegion: TextureRegion? = null,
        var itemAttributes: Int = 0,
        var itemTypeID: ItemTypeID = ItemTypeID.NONE,
        var itemUseType: Int = 0,
        var itemUseTypeValue: Int = 0,
        var itemShortDescription: String = "",
        var itemValue: Int = 0
) : Image(textureRegion) {

    enum class ItemAttribute(private val value: Int) {
        CONSUMABLE(1),
        WEARABLE(2),
        STACKABLE(4);

        operator fun invoke(): Int = value
    }

    enum class ItemUseType(private val value: Int) {
        RESTORE_HEALTH(1), RESTORE_MP(2), DAMAGE(4),
        WEAPON_ONEHAND(8), WEAPON_TWOHAND(16), WAND_ONEHAND(32),
        WAND_TWOHAND(64), ARMOR_SHIELD(128), ARMOR_HELMET(256),
        ARMOR_CHEST(512), ARMOR_FEET(1024), QUEST_ITEM(2048);

        operator fun invoke(): Int = value
    }

    enum class ItemTypeID {
        ARMOR01, ARMOR02, ARMOR03, ARMOR04, ARMOR05,
        BOOTS01, BOOTS02, BOOTS03, BOOTS04, BOOTS05,
        HELMET01, HELMET02, HELMET03, HELMET04, HELMET05,
        SHIELD01, SHIELD02, SHIELD03, SHIELD04, SHIELD05,
        WANDS01, WANDS02, WANDS03, WANDS04, WANDS05,
        WEAPON01, WEAPON02, WEAPON03, WEAPON04, WEAPON05,
        POTIONS01, POTIONS02, POTIONS03,
        SCROLL01, SCROLL02, SCROLL03, NONE, HERB001,
        BABY001, HORNS001, FUR001,
    }

    fun isStackable(): Boolean {
        return itemAttributes.and(ItemAttribute.STACKABLE()) == ItemAttribute.STACKABLE()
    }

    fun isSameItemType(candidateInventoryItem: InventoryItem): Boolean {
        return itemTypeID == candidateInventoryItem.itemTypeID
    }

    fun tradeInValue(): Int {
        //For now, we will set the trade in value of items at about one third their original value
        return MathUtils.floor(itemValue * .33f) + 2
    }

    fun isInventoryItemOffensive(): Boolean {
        return itemUseType.and(WEAPON_ONEHAND()) == WEAPON_ONEHAND() ||
                itemUseType.and(WEAPON_TWOHAND()) == WEAPON_TWOHAND() ||
                itemUseType.and(WAND_ONEHAND()) == WAND_ONEHAND() ||
                itemUseType.and(WAND_TWOHAND()) == WAND_TWOHAND()

    }

    fun isInventoryItemDefensive(): Boolean {
        return itemUseType and ARMOR_CHEST() == ARMOR_CHEST() ||
                itemUseType and ARMOR_HELMET() == ARMOR_HELMET() ||
                itemUseType and ARMOR_FEET() == ARMOR_FEET() ||
                itemUseType and ARMOR_SHIELD() == ARMOR_SHIELD()
    }

}