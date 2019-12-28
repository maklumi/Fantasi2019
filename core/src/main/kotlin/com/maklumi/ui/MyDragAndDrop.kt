package com.maklumi.ui

import com.badlogic.gdx.Input.Buttons
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.utils.DragListener
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ObjectMap


class MyDragAndDrop {
    val tmpVector = Vector2()

    var dragSource: Source? = null
    var payload: Payload? = null
    var dragActor: Actor? = null
    var target: Target? = null
    var isValidTarget = false
    val targets: Array<Target?> = Array()
    private val sourceListeners: ObjectMap<Source?, DragListener?> = ObjectMap()
    private var tapSquareSize = 8f
    private var button = Buttons.LEFT
    var dragActorX = 0f
    var dragActorY = 0f
    var touchOffsetX = 0f
    var touchOffsetY = 0f
    var dragValidTime: Long = 0
    var dragTime = 250
    var activePointer = -1
    var cancelTouchFocus = true
    var keepWithinStage = true

    fun addSource(source: Source) {
        val listener = object : DragListener() {
            override fun dragStart(event: InputEvent, x: Float, y: Float, pointer: Int) {
                if (activePointer != -1) {
                    event.stop()
                    return
                }

                activePointer = pointer

                dragValidTime = System.currentTimeMillis() + dragTime
                dragSource = source
                payload = source.dragStart(event, touchDownX, touchDownY, pointer)
                event.stop()

                if (cancelTouchFocus && payload != null) {
                    val stage: Stage = source.actor.stage
                    stage.cancelTouchFocusExcept(this, source.actor)
                }
            }

            override fun drag(event: InputEvent, x: Float, y: Float, pointer: Int) {
                if (payload == null) return
                if (pointer != activePointer) return

                source.drag(event, x, y, pointer)

                val stage = event.stage

//                if (dragActor != null) {
//                    // causes NPE
////                    dragActor!!.remove(); // Remove so it cannot be hit (Touchable.disabled isn't enough).
//                    dragActor = null
//                }
                dragActor = null

                // Find target.
                var newTarget: Target? = null
                isValidTarget = false
                val stageX = event.stageX + touchOffsetX
                val stageY = event.stageY + touchOffsetY
                var hit = event.stage.hit(stageX, stageY, true) // Prefer touchable actors.
                if (hit == null) hit = event.stage.hit(stageX, stageY, false)
                if (hit != null) {
                    var i = 0
                    val n = targets.size
                    while (i < n) {
                        val target = targets[i]!!
                        if (!target.actor.isAscendantOf(hit)) {
                            i++
                            continue
                        }
                        newTarget = target
                        target.actor.stageToLocalCoordinates(tmpVector.set(stageX, stageY))
                        break
//                        i++
                    }
                }
                // If over a new target, notify the former target that it's being left behind.
                if (newTarget != target) {
                    target?.reset(source, payload)
                    target = newTarget
                }
                // Notify new target of drag.
                if (newTarget != null) isValidTarget = newTarget.drag(source, payload, tmpVector.x, tmpVector.y, pointer)

                // Add and position the drag actor.
                var actor: Actor? = null
                if (target != null) actor = if (isValidTarget) payload!!.validDragActor else payload!!.invalidDragActor
                if (actor == null) actor = payload!!.dragActor
                dragActor = actor
                if (actor == null) return
                stage.addActor(actor)
                var actorX = event.stageX - actor.width + dragActorX
                var actorY = event.stageY + dragActorY
                if (keepWithinStage) {
                    if (actorX < 0) actorX = 0f
                    if (actorY < 0) actorY = 0f
                    if (actorX + actor.width > stage.width) actorX = stage.width - actor.width
                    if (actorY + actor.height > stage.height) actorY = stage.height - actor.height
                }
                actor.setPosition(actorX, actorY)
            }

            override fun dragStop(event: InputEvent, x: Float, y: Float, pointer: Int) {
                if (pointer != activePointer) return
                activePointer = -1
                if (payload == null) return

                if (System.currentTimeMillis() < dragValidTime) isValidTarget = false
                dragActor?.remove()
                if (isValidTarget) {
                    val stageX = event.stageX + touchOffsetX
                    val stageY = event.stageY + touchOffsetY
                    target!!.actor.stageToLocalCoordinates(tmpVector.set(stageX, stageY))
                    target!!.drop(source, payload, tmpVector.x, tmpVector.y, pointer)
                }
                source.dragStop(event, x, y, pointer, payload, if (isValidTarget) target else null)
                target?.reset(source, payload)
                dragSource = null
                payload = null
                target = null
                isValidTarget = false
                dragActor = null
            }
        }
        listener.tapSquareSize = tapSquareSize
        listener.button = button
        source.actor.addCaptureListener(listener)
        sourceListeners.put(source, listener)
    }

//    fun removeSource(source: Source) {
//        val dragListener = sourceListeners.remove(source)!!
//        source.actor.removeCaptureListener(dragListener)
//    }

    fun addTarget(target: Target?) {
        targets.add(target)
    }

//    fun removeTarget(target: Target?) {
//        targets.removeValue(target, true)
//    }

    /** Removes all targets and sources.  */
//    fun clear() {
//        targets.clear()
//        sourceListeners.entries().forEach { entry ->
//            entry.key!!.actor.removeCaptureListener(entry.value)
//        }
//        sourceListeners.clear()
//    }

    /** Cancels the touch focus for everything except the specified source.  */
//    fun cancelTouchFocusExcept(except: Source) {
//        val listener = sourceListeners[except] ?: return
//        val stage = except.actor.stage
//        stage?.cancelTouchFocusExcept(listener, except.actor)
//    }

    /** Sets the distance a touch must travel before being considered a drag.  */
//    fun setTapSquareSize(halfTapSquareSize: Float) {
//        tapSquareSize = halfTapSquareSize
//    }

    /** Sets the button to listen for, all other buttons are ignored. Default is [Buttons.LEFT]. Use -1 for any button.  */
//    fun setButton(button: Int) {
//        this.button = button
//    }

//    fun setDragActorPosition(dragActorX: Float, dragActorY: Float) {
//        this.dragActorX = dragActorX
//        this.dragActorY = dragActorY
//    }

    /** Sets an offset in stage coordinates from the touch position which is used to determine the drop location. Default is
     * 0,0.  */
//    fun setTouchOffset(touchOffsetX: Float, touchOffsetY: Float) {
//        this.touchOffsetX = touchOffsetX
//        this.touchOffsetY = touchOffsetY
//    }

//    fun isDragging(): Boolean {
//        return payload != null
//    }

    /** Returns the current drag actor, or null.  */
//    fun getDragActor(): Actor? {
//        return dragActor
//    }

    /** Returns the current drag payload, or null.  */
//    fun getDragPayload(): Payload? {
//        return payload
//    }

    /** Returns the current drag source, or null.  */
//    fun getDragSource(): Source? {
//        return dragSource
//    }

    /** Time in milliseconds that a drag must take before a drop will be considered valid. This ignores an accidental drag and drop
     * that was meant to be a click. Default is 250.  */
//    fun setDragTime(dragMillis: Int) {
//        dragTime = dragMillis
//    }

//    fun getDragTime(): Int {
//        return dragTime
//    }

    /** Returns true if a drag is in progress and the [drag time][.setDragTime] has elapsed since the drag started.  */
//    fun isDragValid(): Boolean {
//        return payload != null && System.currentTimeMillis() >= dragValidTime
//    }

    /** When true (default), the [Stage.cancelTouchFocus] touch focus} is cancelled if
     * [dragStart][Source.dragStart] returns non-null. This ensures the DragAndDrop is the only
     * touch focus listener, eg when the source is inside a [Scroll Pane] with flick scroll enabled.  */
//    fun setCancelTouchFocus(cancelTouchFocus: Boolean) {
//        this.cancelTouchFocus = cancelTouchFocus
//    }

//    fun setKeepWithinStage(keepWithinStage: Boolean) {
//        this.keepWithinStage = keepWithinStage
//    }

    abstract class Source(val actor: Actor) {

        /** Called when a drag is started on the source. The coordinates are in the source's local coordinate system.
         * @return If null the drag will not affect any targets.
         */
        abstract fun dragStart(event: InputEvent?, x: Float, y: Float, pointer: Int): Payload?

        /** Called repeatedly during a drag which started on this source.  */
        abstract fun drag(event: InputEvent?, x: Float, y: Float, pointer: Int)

        /** Called when a drag for the source is stopped. The coordinates are in the source's local coordinate system.
         * @param payload null if dragStart returned null.
         * @param target null if not dropped on a valid target.
         */
        abstract fun dragStop(event: InputEvent?, x: Float, y: Float, pointer: Int, payload: Payload?, target: Target?)

    }

    /** A target where a payload can be dropped to. Actor cannot be null.
     * @author Nathan Sweet
     */
    abstract class Target(val actor: Actor) {

        /** Called when the payload is dragged over the target. The coordinates are in the target's local coordinate system.
         * @return true if this is a valid target for the payload.
         */
        abstract fun drag(source: Source?, payload: Payload?, x: Float, y: Float, pointer: Int): Boolean

        /** Called when the payload is no longer over the target, whether because the touch was moved or a drop occurred. This is
         * called even if [.drag] returned false.  */
        abstract fun reset(source: Source?, payload: Payload?)

        /** Called when the payload is dropped on the target. The coordinates are in the target's local coordinate system. This is
         * not called if [.drag] returned false.  */
        abstract fun drop(source: Source?, payload: Payload?, x: Float, y: Float, pointer: Int)

        init {
            val stage: Stage? = actor.stage
            require(!(stage != null && actor == stage.root)) { "The stage root cannot be a drag and drop target." }
        }
    }

    /** The payload of a drag and drop operation. Actors can be optionally provided to follow the cursor and change when over a
     * target. Such Actors will be added and removed from the stage automatically during the drag operation. Care should be taken
     * when using the source Actor as a payload drag actor.  */
    class Payload {
        var dragActor: Actor? = null
        var validDragActor: Actor? = null
        var invalidDragActor: Actor? = null
//        var `object`: Any? = null

    }

}

