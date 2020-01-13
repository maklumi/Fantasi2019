package com.maklumi.battle

import com.maklumi.Entity
import com.badlogic.gdx.utils.Array as gdxArray

open class BattleSubject {

    val battleObservers = gdxArray<BattleObserver>()

    protected fun notify(entity: Entity, event: BattleObserver.BattleEvent) {
        battleObservers.forEach { it.onNotify(entity, event) }
    }

}