package com.maklumi

import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor

class PlayerController(private val player: Player) : InputProcessor {

    sealed class Keys {
        object Left : Keys() {
            override fun onPressed(player: Player, delta: Float) {
                player.calculateNextPosition(Player.Direction.LEFT, delta)
            }
        }

        object Right : Keys() {
            override fun onPressed(player: Player, delta: Float) {
                player.calculateNextPosition(Player.Direction.RIGHT, delta)
            }
        }

        object Up : Keys() {
            override fun onPressed(player: Player, delta: Float) {
                player.calculateNextPosition(Player.Direction.UP, delta)
            }
        }

        object Down : Keys() {
            override fun onPressed(player: Player, delta: Float) {
                player.calculateNextPosition(Player.Direction.DOWN, delta)
            }
        }

        object NoPress : Keys() {
            override fun onPressed(player: Player, delta: Float) {
            }
        }

        abstract fun onPressed(player: Player, delta: Float)
    }

    private var keyPress: Keys = Keys.NoPress

    override fun keyDown(keycode: Int): Boolean {
        keyPress = if (keycode == Input.Keys.LEFT || keycode == Input.Keys.A) {
            Keys.Left
        } else if (keycode == Input.Keys.RIGHT || keycode == Input.Keys.D) {
            Keys.Right
        } else if (keycode == Input.Keys.UP || keycode == Input.Keys.W) {
            Keys.Up
        } else if (keycode == Input.Keys.DOWN || keycode == Input.Keys.S) {
            Keys.Down
        } else {
            Keys.NoPress
        }
        return true
    }

    override fun keyUp(keycode: Int): Boolean {
        keyPress = Keys.NoPress
        return true
    }

    fun processInput(delta: Float) {
        if (delta < 0.005) return
        keyPress.onPressed(player, delta)
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return false
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        return false
    }

    override fun keyTyped(character: Char): Boolean {
        return false
    }

    override fun scrolled(amount: Int): Boolean {
        return false
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        return false
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return false
    }


}