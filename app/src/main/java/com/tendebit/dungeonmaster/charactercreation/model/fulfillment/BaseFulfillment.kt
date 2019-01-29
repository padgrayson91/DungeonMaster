package com.tendebit.dungeonmaster.charactercreation.model.fulfillment

import com.tendebit.dungeonmaster.charactercreation.model.requirement.Requirement

abstract class BaseFulfillment<RequirementType: Requirement<*>, StateType>(override val requirement: RequirementType): Fulfillment<RequirementType, StateType>