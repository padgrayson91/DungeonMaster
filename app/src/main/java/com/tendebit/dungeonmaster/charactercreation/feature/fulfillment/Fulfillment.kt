package com.tendebit.dungeonmaster.charactercreation.feature.fulfillment

import com.tendebit.dungeonmaster.charactercreation.feature.requirement.Requirement

interface Fulfillment<T, StateType> {

	val requirement: Requirement<T>

	fun applyToState(state: StateType): Boolean

}
