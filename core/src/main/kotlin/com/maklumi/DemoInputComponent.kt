package com.maklumi

import com.maklumi.Component.MESSAGE

class DemoInputComponent : InputComponent() {

    override fun update(entity: Entity) {
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

        if (string.size == 1) { // demo entity only detects collision and moves away
            if (MESSAGE.COLLISION_WITH_MAP == MESSAGE.valueOf(string.first())) {
                currentDirection = Entity.Direction.nextRandom()
            }
        }
    }

}