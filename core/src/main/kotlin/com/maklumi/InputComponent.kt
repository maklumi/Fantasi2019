package com.maklumi

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.math.Vector3
import com.maklumi.Component.MESSAGE

class InputComponent : InputProcessor, Component {
    enum class Keys { Left, Right, Up, Down, Quit }
    enum class Mouse { SELECT, DOACTION }

    private val lastMouseCoordinates = Vector3()

    private val mouseButtons = mutableMapOf(
            Mouse.SELECT to false,
            Mouse.DOACTION to false)

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

    fun update(entity: Entity) {

        // keyboard input
        when {
            keys[Keys.Left]!! -> {
                entity.sendMessage(MESSAGE.CURRENT_STATE, json.toJson(Entity.State.WALKING))
                entity.sendMessage(MESSAGE.CURRENT_DIRECTION, json.toJson(Entity.Direction.LEFT))
            }
            keys[Keys.Right]!! -> {
                entity.sendMessage(MESSAGE.CURRENT_STATE, json.toJson(Entity.State.WALKING))
                entity.sendMessage(MESSAGE.CURRENT_DIRECTION, json.toJson(Entity.Direction.RIGHT))
            }
            keys[Keys.Up]!! -> {
                entity.sendMessage(MESSAGE.CURRENT_STATE, json.toJson(Entity.State.WALKING))
                entity.sendMessage(MESSAGE.CURRENT_DIRECTION, json.toJson(Entity.Direction.UP))
            }
            keys[Keys.Down]!! -> {
                entity.sendMessage(MESSAGE.CURRENT_STATE, json.toJson(Entity.State.WALKING))
                entity.sendMessage(MESSAGE.CURRENT_DIRECTION, json.toJson(Entity.Direction.DOWN))
            }
            keys[Keys.Quit]!! -> {
                Gdx.app.exit()
            }
            else -> {
                entity.sendMessage(MESSAGE.CURRENT_STATE, json.toJson(Entity.State.IDLE))
            }
        }
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (button == Input.Buttons.LEFT || button == Input.Buttons.RIGHT)
            lastMouseCoordinates.set(screenX.toFloat(), screenY.toFloat(), 0f)

        if (button == Input.Buttons.LEFT) mouseButtons[Mouse.SELECT] = true

        if (button == Input.Buttons.RIGHT) mouseButtons[Mouse.DOACTION] = true

        return true
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (button == Input.Buttons.LEFT) mouseButtons[Mouse.SELECT] = false

        if (button == Input.Buttons.RIGHT) mouseButtons[Mouse.DOACTION] = false

        return true
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

    override fun receiveMessage(message: String) {
    }

}