package com.tendebit.dungeonmaster.charactercreation.viewpager

import com.tendebit.dungeonmaster.charactercreation.feature.requirement.Requirement

interface ViewModelFactory {

	fun viewModelFor(requirement: Requirement<*>): ViewModel?

}
