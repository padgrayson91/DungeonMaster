package com.tendebit.dungeonmaster.charactercreation.feature.fulfillment

import com.tendebit.dungeonmaster.charactercreation.feature.requirement.Requirement

abstract class BaseFulfillment<T, StateType>(override val requirement: Requirement<T>): Fulfillment<T, StateType>