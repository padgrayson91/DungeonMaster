package com.tendebit.dungeonmaster.core.blueprint.fulfillment

import com.tendebit.dungeonmaster.core.blueprint.requirement.Requirement

abstract class BaseFulfillment<T, StateType>(override val requirement: Requirement<T>): Fulfillment<T, StateType> {

	override fun equals(other: Any?): Boolean {
		return other is Fulfillment<*, *> && other.requirement == requirement
	}

	override fun hashCode(): Int {
		return requirement.hashCode()
	}

	override fun toString(): String {
		return "${javaClass.simpleName} for $requirement"
	}
}
