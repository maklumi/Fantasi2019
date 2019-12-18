package com.maklumi

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor

class PlayerController(private val player: Player) : InputProcessor {
    enum class Keys { Left, Right, Up, Down, Quit }

    private val keys = mutableMapOf(
            Keys.Left to false,
            Keys.Right to false,
            Keys.Up to false,
            Keys.Down to false,
            Keys.Quit to false)

    override fun keyDown(keycode: Int): Boolean {
        if (keycode == Input.Keys.LEFT || keycode == Input.Keys.A) {
            keys[Keys.Left] = true
        } else if (keycode == Input.Keys.RIGHT || keycode == Input.Keys.D) {
            keys[Keys.Right] = true
        } else if (keycode == Input.Keys.UP || keycode == Input.Keys.W) {
            keys[Keys.Up] = true
        } else if (keycode == Input.Keys.DOWN || keycode == Input.Keys.S) {
            keys[Keys.Down] = true
        } else if (keycode == Input.Keys.Q) {
            keys[Keys.Quit] = true
        }
        return true
    }

    override fun keyUp(keycode: Int): Boolean {
        if (keycode == Input.Keys.LEFT || keycode == Input.Keys.A) {
            keys[Keys.Left] = false
        } else if (keycode == Input.Keys.RIGHT || keycode == Input.Keys.D) {
            keys[Keys.Right] = false
        } else if (keycode == Input.Keys.UP || keycode == Input.Keys.W) {
            keys[Keys.Up] = false
        } else if (keycode == Input.Keys.DOWN || keycode == Input.Keys.S) {
            keys[Keys.Down] = false
        } else if (keycode == Input.Keys.Q) {
            keys[Keys.Quit] = false
        }
        return true
    }

    fun processInput(delta: Float) {
//        if (delta < 0.005) return

        when {
            keys[Keys.Left]!! -> {
                player.calculateNextPosition(Player.Direction.LEFT, delta)
            }
            keys[Keys.Right]!! -> {
                player.calculateNextPosition(Player.Direction.RIGHT, delta)
            }
            keys[Keys.Up]!! -> {
                player.calculateNextPosition(Player.Direction.UP, delta)
            }
            keys[Keys.Down]!! -> {
                player.calculateNextPosition(Player.Direction.DOWN, delta)
            }
            keys[Keys.Quit]!! -> {
                Gdx.app.exit()
            }
        }
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