package com.maklumi

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonValue
import com.badlogic.gdx.utils.JsonWriter
import com.maklumi.InventoryItem.ItemTypeID
import com.maklumi.Utility.ITEMS_TEXTUREATLAS
import ktx.json.fromJson
import ktx.json.readValue

object InventoryItemFactory {

    private val json1 = Json()
    private const val INVENTORY_ITEM = "scripts/inventory_items.json"
    private val inventoryItemList = mutableMapOf<ItemTypeID, InventoryItem>()
    private val jsonValues = json1.fromJson<ArrayList<JsonValue>>(Gdx.files.internal(INVENTORY_ITEM))

    init {
        jsonValues.map { json1.readValue<InventoryItem>(it) }
                .forEach { inventoryItemList[it.itemTypeID] = it }
//       json1.setOutputType(JsonWriter.OutputType.javascript)
//        println(json1.prettyPrint(inventoryItemList.values))
    }

    fun getInventoryItem(itemTypeID: ItemTypeID): InventoryItem {
        val region = ITEMS_TEXTUREATLAS.findRegion("$itemTypeID")
        val props = inventoryItemList[itemTypeID] as InventoryItem
        return InventoryItem(region,
                props.itemAttributes,
                props.itemTypeID,
                props.itemUseType,
                props.itemShortDescription,
                props.itemValue)
    }

}