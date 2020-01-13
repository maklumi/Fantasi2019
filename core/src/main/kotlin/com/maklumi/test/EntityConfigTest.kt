package com.maklumi.test

import com.badlogic.gdx.math.GridPoint2
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonWriter
import com.maklumi.*
import com.badlogic.gdx.utils.Array as gdxArray

object EntityConfigTest {

    @JvmStatic
    fun main(args: Array<String>) {
        val animConfig = AnimationConfig()
        animConfig.frameDuration = 0.5f
        animConfig.animationType = AnimationType.IMMOBILE
        animConfig.texturePaths.add("sprites/characters/Demon0.png")
        animConfig.texturePaths.add("sprites/characters/Demon0.png")
        animConfig.gridPoints.add(GridPoint2(1, 0), GridPoint2(1, 0))

        val config = EntityConfig().apply {
            entityID = "MONSTER001"
            state = Entity.State.IMMOBILE
            direction = Entity.Direction.DOWN
            animationConfig.addAll(animConfig)
            inventory = gdxArray()
            conversationConfigPath = ""
            questConfigPath = ""
            itemTypeID = InventoryItem.ItemTypeID.NOTHING
            currentQuestID = ""
        }

        val j = Json()
        j.setOutputType(JsonWriter.OutputType.javascript)
        print(j.prettyPrint(config))
    }
}