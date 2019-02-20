package com.tendebit.dungeonmaster.charactercreation.viewpager

import com.tendebit.dungeonmaster.charactercreation.feature.requirement.Requirement

interface PageFactory {

	fun pageFor(requirement: Requirement<*>): Page?

	fun applyData(pageid: String, viewModel: Any)

}