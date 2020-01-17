package com.maklumi

import com.badlogic.gdx.Game
import com.badlogic.gdx.Screen
import com.badlogic.gdx.assets.AssetManager
import com.maklumi.screens.*


class Fantasi : Game() {

    enum class ScreenType {
        MainMenu, MainGame, LoadGame, NewGame, GameOver, WatchIntro
    }

    private lateinit var mainMenuScreen: MainMenuScreen
    private lateinit var loadGameScreen: LoadGameScreen
    private lateinit var mainGameScreen: MainGameScreen
    private lateinit var newGameScreen: NewGameScreen
    private lateinit var gameOverScreen: GameOverScreen
    private lateinit var cutSceneScreen: CutSceneScreen

    @Override
    override fun create() {
        Utility.assetManager = AssetManager()
        mainMenuScreen = MainMenuScreen(this)
        loadGameScreen = LoadGameScreen(this)
        mainGameScreen = MainGameScreen(this)
        newGameScreen = NewGameScreen(this)
        gameOverScreen = GameOverScreen(this)
        cutSceneScreen = CutSceneScreen()
        setScreen(cutSceneScreen)
    }

    override fun dispose() {
        super.dispose()
        Utility.assetManager.dispose()
        ScreenType.values().forEach { getScreenType(it).dispose() }
    }

    fun getScreenType(type: ScreenType): Screen {
        return when (type) {
            ScreenType.MainMenu -> mainMenuScreen
            ScreenType.MainGame -> mainGameScreen
            ScreenType.LoadGame -> loadGameScreen
            ScreenType.NewGame -> newGameScreen
            ScreenType.GameOver -> gameOverScreen
            ScreenType.WatchIntro -> cutSceneScreen
        }
    }

}