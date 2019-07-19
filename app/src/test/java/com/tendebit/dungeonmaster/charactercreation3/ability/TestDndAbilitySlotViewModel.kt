package com.tendebit.dungeonmaster.charactercreation3.ability

import com.tendebit.dungeonmaster.charactercreation3.ability.viewmodel.DndAbilitySlotViewModel
import com.tendebit.dungeonmaster.charactercreation3.abilitycore.DndAbility
import com.tendebit.dungeonmaster.charactercreation3.abilitycore.DndAbilityBonus
import com.tendebit.dungeonmaster.charactercreation3.abilitycore.DndAbilitySlot
import com.tendebit.dungeonmaster.charactercreation3.abilitycore.DndAbilityType
import com.tendebit.dungeonmastercore.model.state.Completed
import com.tendebit.dungeonmastercore.model.state.Locked
import com.tendebit.dungeonmastercore.model.state.Removed
import io.reactivex.observers.TestObserver
import org.junit.Test

class TestDndAbilitySlotViewModel {

	@Test
	fun testTextIsCorrect() {
		val testBonus = DndAbilityBonus(DndAbilityType.DEX, 2)
		val testAbility = DndAbility(testBonus, 14)
		val testAbilitySlot = DndAbilitySlot(Locked(testAbility), testBonus)
		val toTest = DndAbilitySlotViewModel(Completed(testAbilitySlot))

		assert(toTest.abilityNameTextRes == DndAbilityType.DEX.nameResId)
		assert(toTest.modifierText == "+3")
		assert(toTest.rawScoreText == "14")
		assert(toTest.bonusText == "+2")
	}

	@Test
	fun testClickEmits() {
		val testBonus = DndAbilityBonus(DndAbilityType.DEX, 2)
		val testAbilitySlot = DndAbilitySlot(Removed, testBonus)
		val toTest = DndAbilitySlotViewModel(Completed(testAbilitySlot))

		val testObserver = TestObserver<Unit>()
		toTest.clicks.subscribe(testObserver)

		toTest.onClick()
		toTest.onClick()
		assert(testObserver.valueCount() == 2)
	}

}
