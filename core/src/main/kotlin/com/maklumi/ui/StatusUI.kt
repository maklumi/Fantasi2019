package com.maklumi.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Align.left
import com.badlogic.gdx.utils.Array
import com.maklumi.Utility.STATUSUI_SKIN
import com.maklumi.Utility.STATUSUI_TEXTUREATLAS

class StatusUI : Window("Status", STATUSUI_SKIN), StatusSubject {

    private val hudBackground = NinePatch(STATUSUI_TEXTUREATLAS.findRegion("dialog"))
    private val hudBackgroundImage = Image(hudBackground)
    private val hpBar = Image(STATUSUI_TEXTUREATLAS.findRegion("HP_Bar"))
    private val mpBar = Image(STATUSUI_TEXTUREATLAS.findRegion("MP_Bar"))
    private val xpBar = Image(STATUSUI_TEXTUREATLAS.findRegion("XP_Bar"))
    val inventoryButton = ImageButton(skin, "inventory-button")
    val questButton = ImageButton(skin, "quest-button")
    private val goldVal = Label("", skin)

    private var level = 1
    var gold = 0
        set(value) {
            field = value
            goldVal.setText("$value")
            notify(value, StatusObserver.StatusEvent.UPDATED_GP)
        }
    private var hp = 50000
    private var mp = 50
    private var xp = 0

    init {
        add()
        add()
        questButton.imageCell.size(64f)
        add(questButton).align(Align.right)
        inventoryButton.imageCell.size(64f)
        add(inventoryButton).align(Align.right)
        row()

        // health row
        val bar = Image(STATUSUI_TEXTUREATLAS.findRegion("Bar"))
        hpBar.setPosition(3f, 6f)
        val group = WidgetGroup()
        group.addActor(bar) // decor
        group.addActor(hpBar) // filler
        add(group).size(bar.width, bar.height)

        val hpLabel = Label(" hp: ", skin)
        add(hpLabel)
        val hp = Label("$hp", skin)
        add(hp)
        row()

        // magic row
        val bar2 = Image(STATUSUI_TEXTUREATLAS.findRegion("Bar"))
        mpBar.setPosition(3f, 6f)
        val group2 = WidgetGroup()
        group2.addActor(bar2)
        group2.addActor(mpBar)
        add(group2).size(bar2.width, bar2.height)

        val mpLabel = Label(" mp: ", skin)
        add(mpLabel)
        val mp = Label("$mp", skin)
        add(mp).align(left)
        row()

        // experience row
        val bar3 = Image(STATUSUI_TEXTUREATLAS.findRegion("Bar"))
        xpBar.setPosition(3f, 6f)
        val group3 = WidgetGroup()
        group3.addActor(bar3)
        group3.addActor(xpBar)
        add(group3).size(bar3.width, bar3.height)

        val xpLabel = Label(" xp: ", skin)
        add(xpLabel)
        val xp = Label("$xp", skin)
        add(xp).align(left)
        row()

        // level row
        val levelLabel = Label("level ", skin)
        add(levelLabel).align(left)
        val levelVal = Label("$level", skin)
        add(levelVal).align(left)
        row()

        // gold row
        val goldLabel = Label("gold ", skin)
        add(goldLabel).align(left)
        add(goldVal).align(left)
        //account for the title padding
        pad(padTop + 40f, 40f, 40f, 40f)

//        debug()
        background(hudBackgroundImage.drawable)
        titleLabel.color = Color.GOLD
        pack()
    }

    override val statusObservers = Array<StatusObserver>()
}