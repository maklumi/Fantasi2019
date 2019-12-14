package com.maklumi

import com.badlogic.gdx.Game

/** [com.badlogic.gdx.ApplicationListener] implementation shared by all platforms.  */
class Fantasi : Game() {
    @Override
    override fun create() {
        setScreen(FirstScreen())
    }
}