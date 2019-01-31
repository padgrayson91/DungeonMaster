package com.tendebit.dungeonmaster.charactercreation.model.fulfillment

import com.tendebit.dungeonmaster.charactercreation.model.requirement.Requirement

abstract class BaseFulfillment<T, StateType>(override val requirement: Requirement<T>): Fulfillment<T, StateType>