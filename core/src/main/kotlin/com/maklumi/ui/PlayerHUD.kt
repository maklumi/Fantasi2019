package com.maklumi.ui

import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Dialog
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.maklumi.*
import com.maklumi.battle.BattleObserver
import com.maklumi.dialog.ComponentObserver
import com.maklumi.dialog.ConversationGraph
import com.maklumi.dialog.ConversationGraphObserver
import com.maklumi.dialog.ConversationGraphObserver.ConversationCommandEvent
import com.maklumi.profile.ProfileEvent
import com.maklumi.profile.ProfileManager
import com.maklumi.profile.ProfileObserver
import com.maklumi.quest.QuestGraph
import com.maklumi.screens.MainGameScreen
import com.maklumi.screens.MainGameScreen.Companion.gameState
import com.maklumi.ui.StoreInventoryObserver.StoreInventoryEvent
import ktx.actors.onClick
import ktx.json.fromJson

class PlayerHUD(camera: Camera) : Screen,
        ComponentObserver,
        ConversationGraphObserver,
        ProfileObserver,
        StoreInventoryObserver,
        BattleObserver,
        StatusObserver {

    private val viewport = ScreenViewport(camera)
    val stage = Stage(viewport)
    private val statusUI = StatusUI()
    private val inventoryUI = InventoryUI()
    private val conversationUI = ConversationUI()
    private val storeInventoryUI = StoreInventoryUI()
    private val questUI = QuestUI()
    private val messageBoxUI = object : Dialog("Message", Utility.STATUSUI_SKIN, "solidbackground") {
        init {
            button("OK")
            text("Your inventory is full!")
            isVisible = false
            pack()
        }

        override fun result(obj: Any?) {
            cancel()
            isVisible = false
        }

    }
    private val battleUI = BattleUI()

    init {
        statusUI.setPosition(stage.width / 3, stage.height)
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

        questUI.isMovable = true
        questUI.isVisible = false
        questUI.setSize(stage.width, stage.height / 2f)
        questUI.setPosition(x, stage.height - statusUI.height - questUI.height)
        statusUI.questButton.onClick { questUI.isVisible = !questUI.isVisible }
        stage.addActor(questUI)
        messageBoxUI.setPosition(stage.width / 2f - messageBoxUI.width / 2f, stage.height / 2f - messageBoxUI.height / 2f)
        stage.addActor(messageBoxUI)
        battleUI.setFillParent(true)
        battleUI.isVisible = false
        battleUI.isMovable = false
        //removes all listeners including ones that handle focus
        battleUI.clearListeners()
        battleUI.battleState.battleObservers.add(this)
        inventoryUI.inventoryObservers.add(battleUI.battleState)
        stage.addActor(battleUI)
        statusUI.toFront()
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
        when (event) {
            ComponentObserver.ComponentEvent.LOAD_CONVERSATION -> {
                val config = json.fromJson<EntityConfig>(value)
                conversationUI.loadConversation(config)
                conversationUI.graph.conversationGraphObservers.add(this)
            }
            ComponentObserver.ComponentEvent.SHOW_CONVERSATION -> {
                val config = json.fromJson<EntityConfig>(value)
                if (config.entityID == conversationUI.currentEntityID) {
                    conversationUI.isVisible = true
                }
            }
            ComponentObserver.ComponentEvent.HIDE_CONVERSATION -> {
                val config = json.fromJson<EntityConfig>(value)
                if (config.entityID == conversationUI.currentEntityID) {
                    conversationUI.isVisible = false
                    conversationUI.listBox.clearItems() // make sure keyboard focus also lost
                }
            }
            ComponentObserver.ComponentEvent.QUEST_LOCATION_DISCOVERED -> {
                val string = value.split(MESSAGE_TOKEN)
                val questID = string[0]
                val questTaskID = string[1]

                questUI.questTaskComplete(questID, questTaskID)
                updateEntityObservers()
            }
            ComponentObserver.ComponentEvent.ENEMY_SPAWN_LOCATION_CHANGED -> {
                battleUI.battleState.currentZoneLevel = value.toInt()
                battleUI.battleZoneTriggered()
                battleUI.isVisible = true
            }
            ComponentObserver.ComponentEvent.PLAYER_HAS_MOVED -> {
                if (battleUI.isBattleReady()) {
                    gameState = MainGameScreen.GameState.SAVING
                    battleUI.toBack()
                    battleUI.isVisible = true
                }
            }
        }
    }

    override fun onNotify(graph: ConversationGraph, event: ConversationCommandEvent) {
        when (event) {
            ConversationCommandEvent.LOAD_STORE_INVENTORY -> {
                val table = InventoryUI.getInventoryFiltered(inventoryUI.inventorySlotTable)
                storeInventoryUI.loadPlayerInventory(table)

                val blackSmithOrMage = MapManager.currentSelectedEntity ?: return
                val itemLocations = Array<InventoryItemLocation>()
                val itemIDs = blackSmithOrMage.entityConfig.inventory
                for (i in 0 until itemIDs.size) {
                    itemLocations.add(InventoryItemLocation(i, itemIDs[i].toString(), 1, InventoryUI.PLAYER_INVENTORY))
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
            ConversationCommandEvent.ACCEPT_QUEST -> {
                val selectedEntity = MapManager.currentSelectedEntity ?: return
                val config = selectedEntity.entityConfig
                val path = config.questConfigPath
                val questGraph = questUI.loadQuest(path)
                if (questGraph != null) {
                    //Update conversation dialog
                    config.conversationConfigPath = QuestUI.RETURN_QUEST
                    config.currentQuestID = questGraph.questID
                    ProfileManager.properties.put(config.entityID, config)
                    updateEntityObservers()
                }

                conversationUI.isVisible = false
                MapManager.clearCurrentSelectedEntity()
            }
            ConversationCommandEvent.ADD_ENTITY_TO_INVENTORY -> {
                val selectedEntity = MapManager.currentSelectedEntity ?: return
                if (inventoryUI.doesInventoryHaveSpace()) {
                    inventoryUI.addEntityToInventory(selectedEntity, selectedEntity.entityConfig.currentQuestID)
                    MapManager.clearCurrentSelectedEntity()
                    MapManager.removeMapQuestEntity(selectedEntity)
                    selectedEntity.unregisterObservers()
                    conversationUI.isVisible = false
                    questUI.updateQuests()
                } else {
                    MapManager.clearCurrentSelectedEntity()
                    conversationUI.isVisible = false
                    messageBoxUI.isVisible = true
                }
            }
            ConversationCommandEvent.RETURN_QUEST -> {
                val selectedEntity = MapManager.currentSelectedEntity ?: return
                val config = selectedEntity.entityConfig
                val configReturnProperty = ProfileManager.getProperty<EntityConfig>(config.entityID) ?: return
                val questID = configReturnProperty.currentQuestID
                if (questUI.isQuestReadyForReturn(questID)) {
                    val quest = questUI.getQuestByID(questID)
                    statusUI.xp += quest!!.xpReward
                    statusUI.gold += quest.goldReward
                    inventoryUI.removeQuestItemFromInventory(questID)
                    configReturnProperty.conversationConfigPath = QuestUI.FINISHED_QUEST
                    ProfileManager.properties.put(configReturnProperty.entityID, configReturnProperty)
                }
                conversationUI.isVisible = false
                MapManager.clearCurrentSelectedEntity()
            }
        }
    }

    override fun onNotify(event: ProfileEvent) {
        when (event) {
            ProfileEvent.PROFILE_LOADED -> {
                // inventory slot
                val inventory = ProfileManager.getProperty<Array<InventoryItemLocation>>("playerInventory")
                if (inventory != null && inventory.size > 0) {
                    InventoryUI.populateInventory(inventoryUI.inventorySlotTable, inventory, inventoryUI.dragAndDrop, InventoryUI.PLAYER_INVENTORY, false)
                } else {
                    //add default items if nothing is found
                    val items: Array<InventoryItem.ItemTypeID> = MapManager.player.entityConfig.inventory
                    val itemLocations = Array<InventoryItemLocation>()
                    for (i in 0 until items.size) {
                        itemLocations.add(InventoryItemLocation(i, items.get(i).toString(), 1, InventoryUI.PLAYER_INVENTORY))
                    }
                    InventoryUI.populateInventory(inventoryUI.inventorySlotTable, itemLocations, inventoryUI.dragAndDrop, InventoryUI.PLAYER_INVENTORY, false)
                }

                // equip slot
                val equipInventory = ProfileManager.getProperty<Array<InventoryItemLocation>>("playerEquipInventory")
                if (equipInventory != null && equipInventory.size > 0) {
                    InventoryUI.populateInventory(inventoryUI.equipSlots, equipInventory, inventoryUI.dragAndDrop, InventoryUI.PLAYER_INVENTORY, false)
                }

                // check gold, if first time, give something
                val gold = ProfileManager.getProperty("currentPlayerGP") ?: 200
                statusUI.gold = gold

                statusUI.xpCurrentMax = ProfileManager.getProperty("currentPlayerXPMax") ?: 200
                statusUI.xp = ProfileManager.getProperty("currentPlayerXP") ?: 0
                statusUI.hpCurrentMax = ProfileManager.getProperty("currentPlayerHPMax") ?: 50
                statusUI.hp = ProfileManager.getProperty("currentPlayerHP") ?: 50
//                statusUI.hp = 50
                statusUI.mpCurrentMax = ProfileManager.getProperty("currentPlayerMPMax") ?: 50
                statusUI.mp = ProfileManager.getProperty("currentPlayerMP") ?: 50
                statusUI.level = ProfileManager.getProperty("currentPlayerLevel") ?: 1

                val quests = ProfileManager.getProperty<Array<QuestGraph>>("playerQuests")
                quests?.let { questUI.quests.addAll(quests) }
            }
            ProfileEvent.SAVING_PROFILE -> {
                ProfileManager.setProperty("playerInventory", InventoryUI.getInventoryFiltered(inventoryUI.inventorySlotTable))
                ProfileManager.setProperty("playerEquipInventory", InventoryUI.getInventoryFiltered(inventoryUI.equipSlots))
                ProfileManager.setProperty("currentPlayerGP", statusUI.gold)
                ProfileManager.setProperty("playerQuests", questUI.quests)
                ProfileManager.setProperty("currentPlayerXP", statusUI.xp)
                ProfileManager.setProperty("currentPlayerXPMax", statusUI.xpCurrentMax)
                ProfileManager.setProperty("currentPlayerHP", statusUI.hp)
                ProfileManager.setProperty("currentPlayerHPMax", statusUI.hpCurrentMax)
                ProfileManager.setProperty("currentPlayerMP", statusUI.mp)
                ProfileManager.setProperty("currentPlayerMPMax", statusUI.mpCurrentMax)
                ProfileManager.setProperty("currentPlayerLevel", statusUI.level)
            }
        }
    }

    override fun onNotify(value: String, event: StoreInventoryEvent) {
        when (event) {
            StoreInventoryEvent.PLAYER_GP_TOTAL_UPDATED -> {
                statusUI.gold = value.toInt()
            }
            StoreInventoryEvent.PLAYER_INVENTORY_UPDATED -> {
                val items = json.fromJson<Array<InventoryItemLocation>>(value)
                InventoryUI.populateInventory(inventoryUI.inventorySlotTable, items, inventoryUI.dragAndDrop, InventoryUI.PLAYER_INVENTORY, false)
            }
        }
    }

    override fun onNotify(entity: Entity, event: BattleObserver.BattleEvent) {
        when (event) {
            BattleObserver.BattleEvent.OPPONENT_ADDED -> {
            }
            BattleObserver.BattleEvent.OPPONENT_DEFEATED -> {
                val goldReward = entity.entityConfig.entityProperties[EntityProperties.ENTITY_GP_REWARD()].toInt()
                statusUI.gold += goldReward
                val xpReward = entity.entityConfig.entityProperties[EntityProperties.ENTITY_XP_REWARD()].toInt()
                statusUI.xp += xpReward
                battleUI.isVisible = false
                gameState = MainGameScreen.GameState.RUNNING
            }
            BattleObserver.BattleEvent.PLAYER_RUNNING -> {
                battleUI.isVisible = false
                gameState = MainGameScreen.GameState.RUNNING
            }
            BattleObserver.BattleEvent.PLAYER_HIT_DAMAGE -> {
                val hpVal = ProfileManager.getProperty("currentPlayerHP") ?: 100
                statusUI.hp = hpVal
                if (hpVal <= 0) {
                    battleUI.isVisible = false
                    gameState = MainGameScreen.GameState.GAME_OVER
                }
            }
            BattleObserver.BattleEvent.OPPONENT_HIT_DAMAGE -> {
            }
            BattleObserver.BattleEvent.OPPONENT_TURN_DONE -> {
            }
            BattleObserver.BattleEvent.PLAYER_TURN_START -> {
            }
            BattleObserver.BattleEvent.PLAYER_TURN_DONE -> {
            }
        }
    }

    override fun onNotify(value: Int, event: StatusObserver.StatusEvent) {
        when (event) {
            StatusObserver.StatusEvent.UPDATED_GP -> {
                storeInventoryUI.playerTotal = value
                ProfileManager.setProperty("currentPlayerGP", statusUI.gold)
            }
            StatusObserver.StatusEvent.UPDATED_LEVEL -> {
                ProfileManager.setProperty("currentPlayerLevel", statusUI.level)
            }
            StatusObserver.StatusEvent.UPDATED_HP -> {
                ProfileManager.setProperty("currentPlayerHP", statusUI.hp)
            }
            StatusObserver.StatusEvent.UPDATED_MP -> {
                ProfileManager.setProperty("currentPlayerMP", statusUI.mp)
            }
            StatusObserver.StatusEvent.UPDATED_XP -> {
                ProfileManager.setProperty("currentPlayerXP", statusUI.xp)
            }
        }
    }

    fun updateEntityObservers() {
        MapManager.unregisterCurrentMapEntityObservers()
        questUI.initQuests()
        MapManager.registerCurrentMapEntityObservers(this)
    }
}