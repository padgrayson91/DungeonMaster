package com.tendebit.dungeonmaster.testhelpers

import com.tendebit.dungeonmaster.core.blueprint.fulfillment.BaseFulfillment
import com.tendebit.dungeonmaster.core.blueprint.requirement.Requirement

class SimpleFulfillment(requirement: Requirement<Any>) : BaseFulfillment<Any, String>(requirement) {
	override fun applyToState(state: String): Boolean {
		return true
	}
}