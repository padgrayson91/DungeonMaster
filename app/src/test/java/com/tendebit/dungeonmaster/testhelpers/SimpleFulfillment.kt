package com.tendebit.dungeonmaster.testhelpers

import com.tendebit.dungeonmaster.core.blueprint.fulfillment.BaseFulfillment
import com.tendebit.dungeonmaster.core.blueprint.requirement.Requirement

@Suppress("UNCHECKED_CAST")
class SimpleFulfillment(requirement: Requirement<Any>) : BaseFulfillment<Any, Any>(requirement) {
	override fun applyToState(state: Any): Boolean {
		if (state is MutableList<*>) {
			requirement.item?.let {
				(state as MutableList<Any>).add(it)
			}
		}
		return true
	}
}