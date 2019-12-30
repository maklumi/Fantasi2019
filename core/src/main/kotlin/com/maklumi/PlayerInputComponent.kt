package com.maklumi

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.maklumi.screens.MainGameScreen.Companion.gameState

class PlayerInputComponent : InputComponent() {

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
        } else if (keycode == Input.Keys.P) {
            keys[Keys.Pause] = true
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
        } else if (keycode == Input.Keys.P) {
            keys[Keys.Pause] = false
        }
        return true
    }

    override fun update(entity: Entity, delta: Float) {

        // keyboard input
        when {
            keys[Keys.Left]!! -> {
                entity.sendMessage(Component.MESSAGE.CURRENT_STATE, json.toJson(Entity.State.WALKING))
                entity.sendMessage(Component.MESSAGE.CURRENT_DIRECTION, json.toJson(Entity.Direction.LEFT))
            }
            keys[Keys.Right]!! -> {
                entity.sendMessage(Component.MESSAGE.CURRENT_STATE, json.toJson(Entity.State.WALKING))
                entity.sendMessage(Component.MESSAGE.CURRENT_DIRECTION, json.toJson(Entity.Direction.RIGHT))
            }
            keys[Keys.Up]!! -> {
                entity.sendMessage(Component.MESSAGE.CURRENT_STATE, json.toJson(Entity.State.WALKING))
                entity.sendMessage(Component.MESSAGE.CURRENT_DIRECTION, json.toJson(Entity.Direction.UP))
            }
            keys[Keys.Down]!! -> {
                entity.sendMessage(Component.MESSAGE.CURRENT_STATE, json.toJson(Entity.State.WALKING))
                entity.sendMessage(Component.MESSAGE.CURRENT_DIRECTION, json.toJson(Entity.Direction.DOWN))
            }
            keys[Keys.Quit]!! -> {
                Gdx.app.exit()
            }
            keys[Keys.Pause]!! -> {
                gameState = gameState.toggle()
                println("PlayerInputComponent-69: $gameState")
                keys[Keys.Pause] = false
            }
            else -> {
                entity.sendMessage(Component.MESSAGE.CURRENT_STATE, json.toJson(Entity.State.IDLE))
            }
        }

        // Mouse input
        if (mouseButtons[Mouse.SELECT]!!) {
//            print("lastMouseCoordinates $lastMouseCoordinates")
            entity.sendMessage(Component.MESSAGE.INIT_SELECT_ENTITY, json.toJson(lastMouseCoordinates))
            mouseButtons[Mouse.SELECT] = false
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