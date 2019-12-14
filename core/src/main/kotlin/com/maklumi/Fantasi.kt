package com.maklumi

import com.badlogic.gdx.Game
import com.badlogic.gdx.assets.AssetManager
import com.maklumi.screens.MainGameScreen


class Fantasi : Game() {

    @Override
    override fun create() {
        Utility.assetManager = AssetManager()

        setScreen(MainGameScreen())
    }

    override fun dispose() {
        super.dispose()
        Utility.assetManager.dispose()
    }
}