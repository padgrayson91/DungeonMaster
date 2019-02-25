package com.tendebit.dungeonmaster.charactercreation.viewpager

import com.tendebit.dungeonmaster.charactercreation.feature.requirement.Requirement

interface PageFactory<T: ViewModel> {

	fun pageFor(requirement: Requirement<*>): Page?

	fun applyData(viewModel: T)

}