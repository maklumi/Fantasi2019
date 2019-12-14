package com.maklumi

import com.badlogic.gdx.Screen

/** First screen of the application. Displayed after the application is created.  */
class FirstScreen : Screen {
    @Override
    override fun show() { // Prepare your screen here.
    }

    @Override
    override fun render(delta: Float) { // Draw your screen here. "delta" is the time since last render in seconds.
    }

    @Override
    override fun resize(width: Int, height: Int) { // Resize your screen here. The parameters represent the new window size.
    }

    @Override
    override fun pause() { // Invoked when your application is paused.
    }

    @Override
    override fun resume() { // Invoked when your application is resumed after pause.
    }

    @Override
    override fun hide() { // This method is called when another screen replaces this one.
    }

    @Override
    override fun dispose() { // Destroy screen's assets here.
    }
}