package com.tendebit.dungeonmaster

import com.tendebit.dungeonmaster.charactercreation.model.DndCharacterBlueprint
import com.tendebit.dungeonmaster.charactercreation.model.requirement.DndClassOptionsRequirement
import com.tendebit.dungeonmaster.charactercreation.model.requirement.DndRaceOptionsRequirement
import com.tendebit.dungeonmaster.charactercreation.model.requirement.Requirement
import io.reactivex.observers.TestObserver
import org.junit.Test

class TestDndCharacterBlueprint {

	@Test
	fun testInitialRequirements() {
		val toTest = DndCharacterBlueprint()
		val testObserver = TestObserver<List<Requirement<*>>>()
		toTest.requirements.subscribe(testObserver)

		testObserver.assertValueCount(1) // should only emit once
		testObserver.assertValue {requirements ->
			requirements.size == 2 &&
			requirements.find { it is DndClassOptionsRequirement } != null &&
			requirements.find { it is DndRaceOptionsRequirement } != null
		}
	}

}
