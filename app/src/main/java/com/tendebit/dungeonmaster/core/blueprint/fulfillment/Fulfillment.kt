package com.tendebit.dungeonmaster.core.blueprint.fulfillment

import com.tendebit.dungeonmaster.core.blueprint.requirement.Requirement

interface Fulfillment<T, StateType> {

	val requirement: Requirement<T>

	fun applyToState(state: StateType): Boolean

}
