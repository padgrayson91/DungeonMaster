package com.tendebit.dungeonmaster.charactercreation.viewpager

import com.tendebit.dungeonmaster.core.blueprint.requirement.Requirement

interface ViewModelFactory {

	fun viewModelFor(requirement: Requirement<*>): ViewModel?

}
