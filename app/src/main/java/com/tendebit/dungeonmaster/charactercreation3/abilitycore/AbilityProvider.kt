package com.tendebit.dungeonmaster.charactercreation3.abilitycore

import com.tendebit.dungeonmastercore.model.state.ItemState
import io.reactivex.Observable

interface AbilityProvider {

	val state: ItemState<out DndAbilitySelection>
	val internalStateChanges: Observable<ItemState<out DndAbilitySelection>>
	val externalStateChanges: Observable<ItemState<out DndAbilitySelection>>

}