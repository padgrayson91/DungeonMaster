package com.tendebit.dungeonmaster.charactercreation.model.fulfillment

import com.tendebit.dungeonmaster.charactercreation.model.requirement.Requirement

interface Fulfillment<T, StateType> {

	val requirement: Requirement<T>

	fun applyToState(state: StateType): Boolean

}
