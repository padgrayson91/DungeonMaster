package com.tendebit.dungeonmaster.charactercreation2

import com.tendebit.dungeonmaster.charactercreation2.pager.Page
import com.tendebit.dungeonmaster.core.blueprint.requirement.Requirement

interface PageFactory {

	fun pageFor(requirement: Requirement<*>): Page?

}
