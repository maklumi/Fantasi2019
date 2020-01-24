package com.maklumi.sfx

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.maklumi.Utility

class ClockActor : Label(" 00:00 PM ", Utility.STATUSUI_SKIN) {

    private var total = 0f
    private val rate = 60f

    override fun act(delta: Float) {
        total += delta * rate

        val minutes = MathUtils.floor(total / 60 % 60)
        var hours = MathUtils.floor(total / 3600 % 24)
        val afternoon = hours / 12 != 0
        hours %= 12
        if (hours == 0) hours = 12
        val string = String.format("%02d:%02d %s", hours, minutes, if (afternoon) "PM" else "AM")
        setText(string)

        super.act(delta)
    }
}