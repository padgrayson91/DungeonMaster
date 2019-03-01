package com.tendebit.dungeonmaster.core.blueprint.fulfillment

import com.tendebit.dungeonmaster.core.blueprint.requirement.Requirement

abstract class BaseFulfillment<T, StateType>(override val requirement: Requirement<T>): Fulfillment<T, StateType>