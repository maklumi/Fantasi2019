package com.maklumi.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.utils.Align.left

class StatusUI : Group() {

    private val textureAtlasPath = "skins/statusui.atlas"
    private val textureAtlas = TextureAtlas(textureAtlasPath)
    private val hudBackground = NinePatch(textureAtlas.findRegion("dialog"))
    private val hudBackgroundImage = Image(hudBackground)
    private val skin = Skin().also { it.load(Gdx.files.internal("skins/statusui.json")) }
    private val hpBar = Image(textureAtlas.findRegion("HP_Bar"))
    private val mpBar = Image(textureAtlas.findRegion("MP_Bar"))
    private val xpBar = Image(textureAtlas.findRegion("XP_Bar"))

    private var level = 1
    private var gold = 0
    private var hp = 50000
    private var mp = 50
    private var xp = 0

    init {
        val table = Table()

        // health row
        val bar = Image(textureAtlas.findRegion("Bar"))
        hpBar.setPosition(3f, 6f)
        val group = WidgetGroup()
        group.addActor(bar) // decor
        group.addActor(hpBar) // filler

        val cell = table.add(group)
        cell.size(bar.width, bar.height) // important to set size to widget group

        val hpLabel = Label(" hp: ", skin)
        table.add(hpLabel)
        val hp = Label("$hp", skin)
        table.add(hp)
        table.row()

        // magic row
        val bar2 = Image(textureAtlas.findRegion("Bar"))
        mpBar.setPosition(3f, 6f)
        val group2 = WidgetGroup()
        group2.addActor(bar2)
        group2.addActor(mpBar)

        val cell2 = table.add(group2)
        cell2.width(bar2.width)
        cell2.height(bar2.height)

        val mpLabel = Label(" mp: ", skin)
        table.add(mpLabel)
        val mp = Label("$mp", skin)
        table.add(mp).align(left)
        table.row()

        // experience row
        val bar3 = Image(textureAtlas.findRegion("Bar"))
        xpBar.setPosition(3f, 6f)
        val group3 = WidgetGroup()
        group3.addActor(bar3)
        group3.addActor(xpBar)

        val cell3 = table.add(group3)
        cell3.width(bar3.width)
        cell3.height(bar3.height)

        val xpLabel = Label(" xp: ", skin)
        table.add(xpLabel)
        val xp = Label("$xp", skin)
        table.add(xp).align(left)
        table.row()

        // level row
        val levelLabel = Label("level: ", skin)
        table.add(levelLabel).align(left)
        val levelVal = Label("$level", skin)
        table.add(levelVal).align(left)
        table.row()

        // gold row
        val goldLabel = Label("gold: ", skin)
        table.add(goldLabel).align(left)
        val goldVal = Label("$gold", skin)
        table.add(goldVal).align(left)
        table.pad(20f)

//        table.debug()
        hudBackgroundImage.setPosition(-table.prefWidth * 1.2f / 2, -table.prefHeight * 1.2f / 2)
        hudBackgroundImage.setSize(table.prefWidth * 1.2f, table.prefHeight * 1.2f)
        this.addActor(hudBackgroundImage)
        this.addActor(table)
    }

}