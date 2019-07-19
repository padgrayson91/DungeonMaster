package com.tendebit.dungeonmaster.charactercreation3.ability.viewmodel

import com.tendebit.dungeonmaster.charactercreation3.abilitycore.DndAbilitySlot
import com.tendebit.dungeonmastercore.model.state.ItemState
import com.tendebit.dungeonmastercore.viewmodel3.ViewModel
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class DndAbilitySlotViewModel(initialState: ItemState<out DndAbilitySlot>) : ViewModel {

	var state: ItemState<out DndAbilitySlot> = initialState
	override val changes = PublishSubject.create<DndAbilitySlotViewModel>()
	private val internalClicks = PublishSubject.create<Unit>()
	val clicks = internalClicks as Observable<Unit>

	val modifierText: CharSequence
			get() = getModifierText(state.item?.modifier)
	val rawScoreText: CharSequence
			get() = state.item?.rawScore?.toString() ?: ""
	val bonusText: CharSequence
			get() = getModifierText(state.item?.bonus?.value)
	val abilityNameTextRes: Int?
			get() = state.item?.type?.nameResId


	fun onClick() {
		internalClicks.onNext(Unit)
	}

	private fun getModifierText(modifier: Int?): CharSequence {
		return when {
			modifier == null -> ""
			modifier >= 0 -> "+$modifier"
			else -> "-$modifier"
		}
	}

}
