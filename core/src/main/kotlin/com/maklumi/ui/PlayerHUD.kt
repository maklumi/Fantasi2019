package com.maklumi.ui

import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.maklumi.EntityConfig
import com.maklumi.InventoryItem
import com.maklumi.MapManager
import com.maklumi.dialog.ComponentObserver
import com.maklumi.dialog.ConversationGraph
import com.maklumi.dialog.ConversationGraphObserver
import com.maklumi.dialog.ConversationGraphObserver.ConversationCommandEvent
import com.maklumi.json
import com.maklumi.profile.ProfileEvent
import com.maklumi.profile.ProfileManager
import com.maklumi.profile.ProfileObserver
import com.maklumi.ui.StoreInventoryObserver.StoreInventoryEvent
import ktx.actors.onClick
import ktx.json.fromJson

class PlayerHUD(camera: Camera) : Screen,
        ComponentObserver,
        ConversationGraphObserver,
        ProfileObserver,
        StoreInventoryObserver,
        StatusObserver {

    private val viewport = ScreenViewport(camera)
    val stage = Stage(viewport)
    private val statusUI = StatusUI()
    private val inventoryUI = InventoryUI()
    private val conversationUI = ConversationUI()
    private val storeInventoryUI = StoreInventoryUI()

    init {
        statusUI.setPosition(0f, stage.height)
        stage.addActor(statusUI)

        val x = statusUI.width
        val y = statusUI.height
        inventoryUI.setPosition(x, y)
        inventoryUI.isVisible = false
        statusUI.inventoryButton.onClick { inventoryUI.isVisible = !inventoryUI.isVisible }
        stage.addActor(inventoryUI)

        //add tooltips to the stage
        stage.addActor(inventoryUI.tooltip)

        conversationUI.isMovable = true
        conversationUI.isVisible = false
        conversationUI.setPosition(stage.width / 2f, 0f)
        conversationUI.setSize(stage.width / 2f, stage.height / 2f)
        stage.addActor(conversationUI)

        storeInventoryUI.isMovable = false
        storeInventoryUI.isVisible = false
        stage.addActor(storeInventoryUI)
        storeInventoryUI.inventoryActors.forEach { stage.addActor(it) }

        statusUI.statusObservers.add(this)
        storeInventoryUI.storeInventoryObservers.add(this)
    }

    override fun show() {}

    override fun render(delta: Float) {
        stage.act(delta)
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }

    override fun pause() {

    }

    override fun resume() {

    }

    override fun hide() {

    }

    override fun dispose() {
        stage.dispose()
    }

    override fun onNotify(value: String, event: ComponentObserver.ComponentEvent) {
        val config = json.fromJson<EntityConfig>(value)
        when (event) {
            ComponentObserver.ComponentEvent.LOAD_CONVERSATION -> {
                conversationUI.loadConversation(config)
                conversationUI.graph.conversationGraphObservers.add(this)
            }
            ComponentObserver.ComponentEvent.SHOW_CONVERSATION -> {
                if (config.entityID == conversationUI.currentEntityID) {
                    conversationUI.isVisible = true
                }
            }
            ComponentObserver.ComponentEvent.HIDE_CONVERSATION -> {
                if (config.entityID == conversationUI.currentEntityID) {
                    conversationUI.isVisible = false
                    conversationUI.listBox.clearItems() // make sure keyboard focus also lost
                }
            }
        }
    }

    override fun onNotify(graph: ConversationGraph, event: ConversationCommandEvent) {
        when (event) {
            ConversationCommandEvent.LOAD_STORE_INVENTORY -> {
                val table = InventoryUI.getInventoryAt(inventoryUI.inventorySlotTable)
                storeInventoryUI.loadPlayerInventory(table)

                val blackSmithOrMage = MapManager.currentSelectedEntity ?: return
                val itemLocations = Array<InventoryItemLocation>()
                val itemIDs = blackSmithOrMage.entityConfig.inventory
                for (i in 0 until itemIDs.size) {
                    itemLocations.add(InventoryItemLocation(i, itemIDs[i].toString(), 1))
                }
                storeInventoryUI.loadStoreInventory(itemLocations)

                conversationUI.isVisible = false
                storeInventoryUI.isVisible = true
                storeInventoryUI.toFront()
            }
            ConversationCommandEvent.EXIT_CONVERSATION -> {
                conversationUI.isVisible = false
                MapManager.clearCurrentSelectedEntity()
            }
            ConversationCommandEvent.NONE -> {
            }
        }
    }

    override fun onNotify(event: ProfileEvent) {
        when (event) {
            ProfileEvent.PROFILE_LOADED -> {
                // inventory slot
                val inventory = ProfileManager.getProperty<Array<InventoryItemLocation>>("playerInventory")
                if (inventory != null && inventory.size > 0) {
                    InventoryUI.populateInventory(inventoryUI.inventorySlotTable, inventory, inventoryUI.dragAndDrop)
                } else {
                    //add default items if nothing is found
                    val items: Array<InventoryItem.ItemTypeID> = MapManager.player.entityConfig.inventory
                    val itemLocations = Array<InventoryItemLocation>()
                    for (i in 0 until items.size) {
                        itemLocations.add(InventoryItemLocation(i, items.get(i).toString(), 1))
                    }
                    InventoryUI.populateInventory(inventoryUI.inventorySlotTable, itemLocations, inventoryUI.dragAndDrop)
                }

                // equip slot
                val equipInventory = ProfileManager.getProperty<Array<InventoryItemLocation>>("playerEquipInventory")
                if (equipInventory != null && equipInventory.size > 0) {
                    InventoryUI.populateInventory(inventoryUI.equipSlots, equipInventory, inventoryUI.dragAndDrop)
                }

                // check gold, if first time, give something
                val value = ProfileManager.getProperty("currentPlayerGP") ?: 200
                statusUI.gold = value
            }
            ProfileEvent.SAVING_PROFILE -> {
                ProfileManager.setProperty("playerInventory", InventoryUI.getInventoryAt(inventoryUI.inventorySlotTable))
                ProfileManager.setProperty("playerEquipInventory", InventoryUI.getInventoryAt(inventoryUI.equipSlots))
                ProfileManager.setProperty("currentPlayerGP", statusUI.gold)
            }
        }
    }

    override fun onNotify(value: Int, event: StoreInventoryEvent) {
        when (event) {
            StoreInventoryEvent.PLAYER_GP_TOTAL_UPDATED -> {
                statusUI.gold = value
            }
            StoreInventoryEvent.PLAYER_INVENTORY_UPDATED -> {
            }
        }
    }

    override fun onNotify(value: Int, event: StatusObserver.StatusEvent) {
        when (event) {
            StatusObserver.StatusEvent.UPDATED_GP -> {
                storeInventoryUI.playerTotal = value
            }
            else -> {
            }
        }
    }
}