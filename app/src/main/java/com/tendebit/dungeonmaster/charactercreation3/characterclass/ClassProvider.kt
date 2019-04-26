package com.tendebit.dungeonmaster.charactercreation3.characterclass

import com.tendebit.dungeonmaster.charactercreation3.ItemState
import io.reactivex.Observable

interface ClassProvider {

	val classOptions: Observable<ItemState<out DndCharacterClassSelection>>

	fun refreshState(): ItemState<out DndCharacterClassSelection>

}
