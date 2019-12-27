package com.maklumi.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.utils.Align.left

class StatusUI : Window("Status", skin) {

    companion object {
        private const val textureAtlasPath = "skins/statusui.atlas"
        val textureAtlas = TextureAtlas(textureAtlasPath)
        val skin = Skin(Gdx.files.internal("skins/statusui.json"), textureAtlas)
    }

    private val hudBackground = NinePatch(textureAtlas.findRegion("dialog"))
    private val hudBackgroundImage = Image(hudBackground)
    private val hpBar = Image(textureAtlas.findRegion("HP_Bar"))
    private val mpBar = Image(textureAtlas.findRegion("MP_Bar"))
    private val xpBar = Image(textureAtlas.findRegion("XP_Bar"))

    private var level = 1
    private var gold = 0
    private var hp = 50000
    private var mp = 50
    private var xp = 0

    init {
        // health row
        val bar = Image(textureAtlas.findRegion("Bar"))
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
        val bar2 = Image(textureAtlas.findRegion("Bar"))
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
        val bar3 = Image(textureAtlas.findRegion("Bar"))
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
        val levelLabel = Label("level: ", skin)
        add(levelLabel).align(left)
        val levelVal = Label("$level", skin)
        add(levelVal).align(left)
        row()

        // gold row
        val goldLabel = Label("gold: ", skin)
        add(goldLabel).align(left)
        val goldVal = Label("$gold", skin)
        add(goldVal).align(left)
        //account for the title padding
        pad(padTop + 40f, 20f, 20f, 20f)

//        debug()
        background(hudBackgroundImage.drawable)
        titleLabel.color = Color.GOLD
        pack()
    }

}