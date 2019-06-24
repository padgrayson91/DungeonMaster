package com.tendebit.dungeonmaster.charactercreation3.ability

import com.tendebit.dungeonmastercore.model.state.Locked
import com.tendebit.dungeonmastercore.model.state.Normal
import com.tendebit.dungeonmastercore.model.state.Selected
import org.junit.Test

class TestDndAbilityRollSelection {

	@Test
	fun testPerformRoll() {
		val toTest = DndAbilityRollSelection(6)
		toTest.autoRollAll()

		assert(toTest.options.all { it is Normal && it.item in 3..18 })
	}

	@Test
	fun testPerformSelection() {
		val toTest = DndAbilityRollSelection(6)
		toTest.autoRollAll()
		toTest.select(2)
		assert(toTest.options[2] is Selected)
	}

	@Test
	fun testItemIsLockedAfterAssignment() {
		val toTest = DndAbilityRollSelection(6)
		toTest.autoRollAll()
		toTest.select(2)
		assert(toTest.options[2] is Selected)
		toTest.onAssigned()
		assert(toTest.options[2] is Locked)
		assert(toTest.selectedItem == null)
	}

	@Test
	@Suppress("UNCHECKED_CAST")
	fun testItemIsNormalAfterRetraction() {
		val toTest = DndAbilityRollSelection(6)
		toTest.autoRollAll()
		toTest.select(2)
		assert(toTest.options[2] is Selected)
		toTest.onAssigned()
		assert(toTest.options[2] is Locked)
		assert(toTest.selectedItem == null)
		toTest.onRetracted(toTest.options[2] as Locked<Int>)
		assert(toTest.options[2] is Normal)
	}

	@Test
	fun testSelectAndAssignAll() {
		val toTest = DndAbilityRollSelection(6)
		toTest.autoRollAll()
		toTest.select(0)
		toTest.onAssigned()
		toTest.select(1)
		toTest.onAssigned()
		toTest.select(2)
		toTest.onAssigned()
		toTest.select(3)
		toTest.onAssigned()
		toTest.select(4)
		toTest.onAssigned()
		toTest.select(5)
		toTest.onAssigned()

		assert(toTest.options.all { it is Locked })
	}

}
