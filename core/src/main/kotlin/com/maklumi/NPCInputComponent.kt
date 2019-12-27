package com.maklumi

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.math.MathUtils
import com.maklumi.Component.MESSAGE
import ktx.json.fromJson

class NPCInputComponent : InputComponent() {

    private var frameTime = 0.0f
    private var delayTime = 0.0f
    private var currentState = Entity.State.WALKING

    override fun update(entity: Entity, delta: Float) {
        frameTime += delta
        delayTime += delta % 10

        // if IMMOBILE, don't update anything
        if (currentState == Entity.State.IMMOBILE) {
            entity.sendMessage(MESSAGE.CURRENT_STATE, json.toJson(Entity.State.IMMOBILE))
            return
        }

        //Change direction after so many seconds
        if (frameTime > MathUtils.random(1f, 5f)) {
            currentState = Entity.State.nextRandom()
            currentDirection = Entity.Direction.nextRandom()
            frameTime = 0.0f
        }

        if (currentState == Entity.State.IDLE) {
            entity.sendMessage(MESSAGE.CURRENT_STATE, json.toJson(Entity.State.IDLE))
            return
        }

        when (currentDirection) {
            Entity.Direction.LEFT -> {
                entity.sendMessage(MESSAGE.CURRENT_STATE, json.toJson(Entity.State.WALKING))
                entity.sendMessage(MESSAGE.CURRENT_DIRECTION, json.toJson(Entity.Direction.LEFT))
            }
            Entity.Direction.RIGHT -> {
                entity.sendMessage(MESSAGE.CURRENT_STATE, json.toJson(Entity.State.WALKING))
                entity.sendMessage(MESSAGE.CURRENT_DIRECTION, json.toJson(Entity.Direction.RIGHT))
            }
            Entity.Direction.UP -> {
                entity.sendMessage(MESSAGE.CURRENT_STATE, json.toJson(Entity.State.WALKING))
                entity.sendMessage(MESSAGE.CURRENT_DIRECTION, json.toJson(Entity.Direction.UP))
            }
            Entity.Direction.DOWN -> {
                entity.sendMessage(MESSAGE.CURRENT_STATE, json.toJson(Entity.State.WALKING))
                entity.sendMessage(MESSAGE.CURRENT_DIRECTION, json.toJson(Entity.Direction.DOWN))
            }
        }
    }

    override fun receiveMessage(message: String) {
        val string = message.split(MESSAGE_TOKEN)

        if (string.size == 1) {
            val code = MESSAGE.valueOf(string.first())
            if (MESSAGE.COLLISION_WITH_MAP == code) currentDirection = Entity.Direction.nextRandom()
            if (MESSAGE.COLLISION_WITH_ENTITY == code) {
                if (delayTime > 0.5f) {
                    currentDirection = currentDirection.getOpposite()
                    delayTime = 0f
                }
            }
        }

        if (string.size == 2) {
            val code = MESSAGE.valueOf(string[0])
            val value = string[1]
            if (MESSAGE.INIT_STATE == code) currentState = json.fromJson(value)
            if (MESSAGE.INIT_DIRECTION == code) currentDirection = json.fromJson(value)
        }
    }

    override fun keyDown(keycode: Int): Boolean {
        if (keycode == Input.Keys.Q) {
            Gdx.app.exit()
        }
        return false
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

    override fun keyUp(keycode: Int): Boolean {
        return false
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        return false
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return false
    }
}