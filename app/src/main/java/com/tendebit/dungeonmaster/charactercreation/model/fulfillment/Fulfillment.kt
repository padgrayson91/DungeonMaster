package com.tendebit.dungeonmaster.charactercreation.model.fulfillment

import com.tendebit.dungeonmaster.charactercreation.model.requirement.Requirement

interface Fulfillment<RequirementType: Requirement<*>, StateType> {
	val requirement: RequirementType

	fun applyToState(state: StateType): Boolean

}
