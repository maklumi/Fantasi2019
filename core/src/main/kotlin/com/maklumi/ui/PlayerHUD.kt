package com.maklumi.ui

import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Dialog
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.maklumi.*
import com.maklumi.audio.AudioManager
import com.maklumi.audio.AudioObserver
import com.maklumi.audio.AudioObserver.AudioCommand.*
import com.maklumi.audio.AudioObserver.AudioTypeEvent.*
import com.maklumi.audio.AudioSubject
import com.maklumi.battle.BattleObserver
import com.maklumi.battle.BattleObserver.BattleEvent.*
import com.maklumi.dialog.ComponentObserver
import com.maklumi.dialog.ComponentObserver.ComponentEvent.*
import com.maklumi.dialog.ConversationGraph
import com.maklumi.dialog.ConversationGraphObserver
import com.maklumi.dialog.ConversationGraphObserver.ConversationCommandEvent
import com.maklumi.profile.ProfileEvent
import com.maklumi.profile.ProfileManager
import com.maklumi.profile.ProfileObserver
import com.maklumi.quest.QuestGraph
import com.maklumi.screens.MainGameScreen
import com.maklumi.screens.MainGameScreen.Companion.gameState
import com.maklumi.sfx.ClockActor
import com.maklumi.sfx.ScreenTransitionAction
import com.maklumi.sfx.ScreenTransitionActor
import com.maklumi.sfx.ShakeCamera
import com.maklumi.ui.InventoryObserver.InventoryEvent.*
import com.maklumi.ui.StoreInventoryObserver.StoreInventoryEvent
import ktx.actors.onClick
import ktx.json.fromJson

class PlayerHUD(private val camera: Camera) : Screen,
        ComponentObserver,
        ConversationGraphObserver,
        ProfileObserver,
        StoreInventoryObserver,
        InventoryObserver,
        BattleObserver,
        AudioSubject,
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
    override val audioObservers = Array<AudioObserver>()
    private val transitionActor = ScreenTransitionActor(Color.BLACK)
    private val shakeCam = ShakeCamera(camera.position.x, camera.position.y, 30f)
    private val clock = ClockActor()

    init {
        audioObservers.add(AudioManager)
        statusUI.setPosition(0f, stage.height - statusUI.height - 40f)
        statusUI.setKeepWithinStage(false)
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
        inventoryUI.inventoryObservers.add(this)
        stage.addActor(battleUI)
        statusUI.toFront()
        transitionActor.isVisible = false
        stage.addActor(transitionActor)

        stage.addActor(clock)
        battleUI.validate()
        questUI.validate()
        storeInventoryUI.validate()
        conversationUI.validate()
        messageBoxUI.validate()
        statusUI.validate()
        inventoryUI.validate()
        clock.validate()
        clock.isVisible = true
        clock.setPosition(stage.width - clock.width, 10f)

        //Music/Sound loading
        notify(MUSIC_LOAD, MUSIC_BATTLE)
        notify(MUSIC_LOAD, MUSIC_LEVEL_UP_FANFARE)
        notify(SOUND_LOAD, SOUND_COIN_RUSTLE)
        notify(SOUND_LOAD, SOUND_CREATURE_PAIN)
        notify(SOUND_LOAD, SOUND_PLAYER_PAIN)
        notify(SOUND_LOAD, SOUND_PLAYER_WAND_ATTACK)
        notify(SOUND_LOAD, SOUND_EATING)
        notify(SOUND_LOAD, SOUND_DRINKING)
    }

    fun addTransitionToStage() {
        transitionActor.isVisible = true
        stage.addAction(Actions.addAction(ScreenTransitionAction.transition(ScreenTransitionAction.ScreenTransitionType.FADE_IN, 1f), transitionActor))
    }

    override fun show() {}

    override fun render(delta: Float) {
        if (shakeCam.shouldShake) {
            camera.position.set(shakeCam.position.x, shakeCam.position.y, 0f)
        }
        stage.act(delta)
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }

    override fun pause() {
        battleUI.resetDefaults()
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
            LOAD_CONVERSATION -> {
                var config = json.fromJson<EntityConfig>(value)
                //Check to see if there is a version loading into properties
                if (config.itemTypeID == InventoryItem.ItemTypeID.NONE) {
                    val configReturnProperty = ProfileManager.getProperty<EntityConfig>(config.entityID)
                    if (configReturnProperty != null) {
                        config = configReturnProperty
                    }
                }
                conversationUI.loadConversation(config)
                conversationUI.graph.conversationGraphObservers.add(this)
            }
            SHOW_CONVERSATION -> {
                val config = json.fromJson<EntityConfig>(value)
                if (config.entityID == conversationUI.currentEntityID) {
                    conversationUI.isVisible = true
                }
            }
            HIDE_CONVERSATION -> {
                val config = json.fromJson<EntityConfig>(value)
                if (config.entityID == conversationUI.currentEntityID) {
                    conversationUI.isVisible = false
                    conversationUI.listBox.clearItems() // make sure keyboard focus also lost
                }
            }
            QUEST_LOCATION_DISCOVERED -> {
                val string = value.split(MESSAGE_TOKEN)
                val questID = string[0]
                val questTaskID = string[1]

                questUI.questTaskComplete(questID, questTaskID)
                updateEntityObservers()
            }
            ENEMY_SPAWN_LOCATION_CHANGED -> {
                battleUI.battleState.currentZoneLevel = value.toInt()
                battleUI.battleZoneTriggered()
            }
            PLAYER_HAS_MOVED -> {
                if (MapManager.currentMapType == MapFactory.MapType.TOWN) return
                if (battleUI.isBattleReady()) {
                    addTransitionToStage()
                    gameState = MainGameScreen.GameState.SAVING
                    battleUI.toBack()
                    battleUI.isVisible = true
                    MapManager.disableCurrentmapMusic()
                    notify(MUSIC_PLAY_LOOP, MUSIC_BATTLE)
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
                    notify(MUSIC_PLAY_ONCE, MUSIC_LEVEL_UP_FANFARE)

                    val quest = questUI.getQuestByID(questID)
                    statusUI.xp += quest!!.xpReward
                    statusUI.gold += quest.goldReward
                    notify(SOUND_PLAY_ONCE, SOUND_COIN_RUSTLE)

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
                if (ProfileManager.isNewProfile) {
                    InventoryUI.clearInventoryItemsAt(inventoryUI.inventorySlotTable)
                    InventoryUI.clearInventoryItemsAt(inventoryUI.equipSlots)
                    inventoryUI.resetEquipSlots()

                    //add default items if first time
                    val items: Array<InventoryItem.ItemTypeID> = MapManager.player.entityConfig.inventory
                    val itemLocations = Array<InventoryItemLocation>()
                    for (i in 0 until items.size) {
                        itemLocations.add(InventoryItemLocation(i, items.get(i).toString(), 1, InventoryUI.PLAYER_INVENTORY))
                    }
                    InventoryUI.populateInventory(inventoryUI.inventorySlotTable, itemLocations, inventoryUI.dragAndDrop, InventoryUI.PLAYER_INVENTORY, false)
                    ProfileManager.properties.put("playerInventory", InventoryUI.getInventoryAt(inventoryUI.inventorySlotTable))

                    questUI.quests.clear()
                    statusUI.hp = 140
                    statusUI.mp = 150
                    statusUI.xp = 160
                    statusUI.level = 1
                    statusUI.gold = 1250
                    clock.total = 60 * 60 * 12f // start at noon
                    ProfileManager.setProperty("currentTime", clock.total)
                } else {
                    // inventory slot
                    val inventory = ProfileManager.getProperty<Array<InventoryItemLocation>>("playerInventory")
                    if (inventory != null && inventory.size > 0) {
                        InventoryUI.populateInventory(inventoryUI.inventorySlotTable, inventory, inventoryUI.dragAndDrop, InventoryUI.PLAYER_INVENTORY, false)
                    }
                    // equip slot
                    val equipInventory = ProfileManager.getProperty<Array<InventoryItemLocation>>("playerEquipInventory")
                    if (equipInventory != null && equipInventory.size > 0) {
                        inventoryUI.resetEquipSlots()
                        InventoryUI.populateInventory(inventoryUI.equipSlots, equipInventory, inventoryUI.dragAndDrop, InventoryUI.PLAYER_INVENTORY, false)
                    }

                    // check gold, if first time, give something
                    val gold = ProfileManager.getProperty("currentPlayerGP") ?: 200
                    statusUI.gold = gold

                    statusUI.xpCurrentMax = ProfileManager.getProperty("currentPlayerXPMax") ?: 200
                    statusUI.xp = ProfileManager.getProperty("currentPlayerXP") ?: 0
                    statusUI.hpCurrentMax = ProfileManager.getProperty("currentPlayerHPMax") ?: 50
                    statusUI.hp = ProfileManager.getProperty("currentPlayerHP") ?: 50
//                    statusUI.hp = 50
                    statusUI.mpCurrentMax = ProfileManager.getProperty("currentPlayerMPMax") ?: 50
                    statusUI.mp = ProfileManager.getProperty("currentPlayerMP") ?: 50
                    statusUI.level = ProfileManager.getProperty("currentPlayerLevel") ?: 1

                    val quests = ProfileManager.getProperty<Array<QuestGraph>>("playerQuests")
                    quests?.let { questUI.quests.addAll(quests) }

                    val totalTime = ProfileManager.getProperty("currentTime") ?: 0f
                    clock.total = totalTime
                }
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
                ProfileManager.setProperty("currentTime", clock.total)
            }
            ProfileEvent.CLEAR_CURRENT_PROFILE -> {
                ProfileManager.setProperty("playerQuests", Array<QuestGraph>())
                ProfileManager.setProperty("playerInventory", Array<InventoryItemLocation>())
                ProfileManager.setProperty("playerEquipInventory", Array<InventoryItemLocation>())
                ProfileManager.setProperty("currentPlayerGP", 0)
                ProfileManager.setProperty("currentPlayerLevel", 0)
                ProfileManager.setProperty("currentPlayerXP", 0)
                ProfileManager.setProperty("currentPlayerXPMax", 0)
                ProfileManager.setProperty("currentPlayerHP", 0)
                ProfileManager.setProperty("currentPlayerHPMax", 0)
                ProfileManager.setProperty("currentPlayerMP", 0)
                ProfileManager.setProperty("currentPlayerMPMax", 0)
                ProfileManager.setProperty("currentTime", 0f)
            }
        }
    }

    override fun onNotify(value: String, event: StoreInventoryEvent) {
        when (event) {
            StoreInventoryEvent.PLAYER_GP_TOTAL_UPDATED -> {
                statusUI.gold = value.toInt()
                notify(SOUND_PLAY_ONCE, SOUND_COIN_RUSTLE)
            }
            StoreInventoryEvent.PLAYER_INVENTORY_UPDATED -> {
                val items = json.fromJson<Array<InventoryItemLocation>>(value)
                InventoryUI.populateInventory(inventoryUI.inventorySlotTable, items, inventoryUI.dragAndDrop, InventoryUI.PLAYER_INVENTORY, false)
            }
        }
    }

    override fun onNotify(value: String, event: InventoryObserver.InventoryEvent) {
        when (event) {
            ITEM_CONSUMED -> {
                val strings = value.split(MESSAGE_TOKEN)
                if (strings.size != 2) return

                val type = strings[0].toInt()
                val typeValue = strings[1].toInt()

                if (InventoryItem.doesRestoreHP(type)) {
                    notify(SOUND_PLAY_ONCE, SOUND_EATING)
                    statusUI.hp += typeValue
                } else if (InventoryItem.doesRestoreMP(type)) {
                    notify(SOUND_PLAY_ONCE, SOUND_DRINKING)
                    statusUI.mp += typeValue
                }
            }
            UPDATED_AP -> {
            }
            UPDATED_DP -> {
            }
            ADD_WAND_AP -> {
                statusUI.mp -= value.toInt()
            }
            REMOVE_WAND_AP -> {
                statusUI.mp += value.toInt()
            }
        }
    }


    override fun onNotify(entity: Entity, event: BattleObserver.BattleEvent) {
        when (event) {
            OPPONENT_ADDED -> {
            }
            OPPONENT_DEFEATED -> {
                val goldReward = entity.entityConfig.entityProperties[EntityProperties.ENTITY_GP_REWARD()].toInt()
                statusUI.gold += goldReward
                val xpReward = entity.entityConfig.entityProperties[EntityProperties.ENTITY_XP_REWARD()].toInt()
                statusUI.xp += xpReward
                battleUI.isVisible = false
                gameState = MainGameScreen.GameState.RUNNING
                notify(MUSIC_STOP, MUSIC_BATTLE)
                MapManager.enableCurrentmapMusic()
                addTransitionToStage()
            }
            PLAYER_RUNNING -> {
                battleUI.isVisible = false
                gameState = MainGameScreen.GameState.RUNNING
                notify(MUSIC_STOP, MUSIC_BATTLE)
                MapManager.enableCurrentmapMusic()
                addTransitionToStage()
            }
            PLAYER_HIT_DAMAGE -> {
                notify(SOUND_PLAY_ONCE, SOUND_PLAYER_PAIN)
                val hpVal = ProfileManager.getProperty("currentPlayerHP") ?: 100
                statusUI.hp = hpVal
                shakeCam.shouldShake = true
                if (hpVal <= 0) {
                    addTransitionToStage()
                    notify(MUSIC_STOP, MUSIC_BATTLE)
                    battleUI.isVisible = false
                    gameState = MainGameScreen.GameState.GAME_OVER
                }
            }
            OPPONENT_HIT_DAMAGE -> {
                notify(SOUND_PLAY_ONCE, SOUND_CREATURE_PAIN)
            }
            OPPONENT_TURN_DONE -> {
            }
            PLAYER_TURN_START -> {
            }
            PLAYER_TURN_DONE -> {
            }
            PLAYER_USED_MAGIC -> {
                notify(SOUND_PLAY_ONCE, SOUND_PLAYER_WAND_ATTACK)
                statusUI.mp = ProfileManager.getProperty("currentPlayerMP") ?: 0
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
            StatusObserver.StatusEvent.LEVELED_UP -> {
                notify(MUSIC_PLAY_ONCE, MUSIC_LEVEL_UP_FANFARE)
            }
        }
    }

    fun updateEntityObservers() {
        MapManager.unregisterCurrentMapEntityObservers()
        questUI.initQuests()
        MapManager.registerCurrentMapEntityObservers(this)
    }

    fun timeOfDay(): ClockActor.TimeOfDay = clock.timeOfDay()
}