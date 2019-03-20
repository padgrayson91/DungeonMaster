package com.tendebit.dungeonmaster.testhelpers

import com.tendebit.dungeonmaster.core.blueprint.examination.Examination
import com.tendebit.dungeonmaster.core.blueprint.examination.Examiner
import com.tendebit.dungeonmaster.core.blueprint.examination.StaticExamination
import com.tendebit.dungeonmaster.core.blueprint.requirement.Requirement

class SimpleExaminer(val requirement: Requirement<Any>): Examiner<String> {
	override fun examine(state: String): Examination<String> {
		return StaticExamination(listOf(SimpleFulfillment(requirement)), false)
	}
}