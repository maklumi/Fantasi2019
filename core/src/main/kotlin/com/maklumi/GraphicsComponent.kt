package com.maklumi

import com.badlogic.gdx.graphics.g2d.Batch

abstract class GraphicsComponent : Component {

    abstract fun update(batch: Batch, delta: Float)

}