package com.maklumi

import com.badlogic.gdx.Game
import com.badlogic.gdx.Screen
import com.badlogic.gdx.assets.AssetManager
import com.maklumi.screens.LoadGameScreen
import com.maklumi.screens.MainGameScreen
import com.maklumi.screens.MainMenuScreen
import com.maklumi.screens.NewGameScreen


class Fantasi : Game() {

    enum class ScreenType { MainMenu, MainGame, LoadGame, NewGame }

    private lateinit var mainMenuScreen: MainMenuScreen
    private lateinit var loadGameScreen: LoadGameScreen
    private lateinit var mainGameScreen: MainGameScreen
    private lateinit var newGameScreen: NewGameScreen

    @Override
    override fun create() {
        Utility.assetManager = AssetManager()
        mainMenuScreen = MainMenuScreen(this)
        loadGameScreen = LoadGameScreen(this)
        mainGameScreen = MainGameScreen()
        newGameScreen = NewGameScreen(this)
        setScreen(mainGameScreen)
    }

    override fun dispose() {
        super.dispose()
        Utility.assetManager.dispose()
    }

    fun getScreenType(type: ScreenType): Screen {
        return when (type) {
            ScreenType.MainMenu -> mainMenuScreen
            ScreenType.MainGame -> mainGameScreen
            ScreenType.LoadGame -> loadGameScreen
            ScreenType.NewGame -> newGameScreen
        }
    }

}