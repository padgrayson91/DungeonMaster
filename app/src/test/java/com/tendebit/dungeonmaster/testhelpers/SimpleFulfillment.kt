package com.tendebit.dungeonmaster.testhelpers

import com.tendebit.dungeonmaster.core.blueprint.fulfillment.BaseFulfillment
import com.tendebit.dungeonmaster.core.blueprint.requirement.Requirement

class SimpleFulfillment(requirement: Requirement<Any>) : BaseFulfillment<Any, Any>(requirement) {
	override fun applyToState(state: Any): Boolean {
		return true
	}
}