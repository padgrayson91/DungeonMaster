package com.tendebit.dungeonmaster.charactercreation3.ability

import com.tendebit.dungeonmaster.charactercreation3.abilitycore.DndAbilityBonus
import com.tendebit.dungeonmaster.charactercreation3.abilitycore.DndAbilityPrerequisites
import com.tendebit.dungeonmaster.charactercreation3.abilitycore.DndAbilitySelection
import com.tendebit.dungeonmaster.charactercreation3.abilitycore.DndAbilitySource
import com.tendebit.dungeonmaster.testhelpers.CharacterCreationRobots
import com.tendebit.dungeonmaster.testhelpers.TestConcurrency
import com.tendebit.dungeonmastercore.concurrency.Concurrency
import com.tendebit.dungeonmastercore.model.state.Completed
import com.tendebit.dungeonmastercore.model.state.ItemState
import com.tendebit.dungeonmastercore.model.state.Loading
import com.tendebit.dungeonmastercore.model.state.Normal
import com.tendebit.dungeonmastercore.model.state.Waiting
import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when` as whenever

@ExperimentalCoroutinesApi
class TestDndAbilities {

	@Test
	fun testInitialStateIsLoading() {
		val toTest = DndAbilities()
		assert(toTest.state is Loading)
	}

	@Test
	fun testStateIsNormalAfterPrerequisitesAreProvided() {
		val prereq = TestPrerequisites()
		val toTest = DndAbilities()
		toTest.start(prereq)

		assert(toTest.state is Normal)
	}

	@Test
	fun testStateIsWaitingIfOneObservableYieldsLoading() {
		val prereq = TestPrerequisites()
		val toTest = DndAbilities()
		toTest.start(prereq)

		prereq.emitLoading(0)
		prereq.emitNormal(1)

		assert(toTest.state is Waiting)
	}

	@Test
	fun testStateIsNormalIfBothStatesAreNormal() {
		val prereq = TestPrerequisites()
		val toTest = DndAbilities()
		toTest.start(prereq)

		prereq.emitNormal(0)
		prereq.emitNormal(1)

		assert(toTest.state is Normal)
	}

	@Test
	fun testAbilityBonusesAreMerged() {
		val bonusList1 = CharacterCreationRobots.arbitraryBonusList
		val bonusList2 = CharacterCreationRobots.arbitraryBonusList2
		val prereq = TestPrerequisites()
		val toTest = DndAbilities()
		toTest.start(prereq)

		prereq.emitCompleted(0, bonusList1)
		prereq.emitCompleted(1, bonusList2)

		assert(toTest.state is Normal)
		val selection = toTest.state.item?.options
		assert(selection != null)
		selection?.forEachIndexed { index, itemState ->
			val bonus = itemState.item?.bonus
			assert(bonus?.type == bonusList1[index].type)
			assert(bonus?.value == bonusList1[index].value + bonusList2[index].value) { "Expected ${bonusList1[index].value + bonusList2[index].value} but had ${bonus?.value}"}
		}
	}

	@Test
	fun testStateChangesInternallyOnStart() {
		val prereq = TestPrerequisites()
		val toTest = DndAbilities()
		val testObserver = TestObserver<ItemState<out DndAbilitySelection>>()
		toTest.internalStateChanges.subscribe(testObserver)
		toTest.start(prereq)

		assert(testObserver.valueCount() == 1)
	}

	@Test
	fun testStateChangesExternallyOnceForFirstSourceEmission() {
		val bonusList1 = CharacterCreationRobots.arbitraryBonusList
		val bonusList2 = CharacterCreationRobots.arbitraryBonusList2
		val prereq = TestPrerequisites()
		val toTest = DndAbilities()
		val testObserver = TestObserver<ItemState<out DndAbilitySelection>>()
		toTest.externalStateChanges.subscribe(testObserver)
		toTest.start(prereq)

		prereq.emitCompleted(0, bonusList1)
		prereq.emitCompleted(1, bonusList2)

		assert(testObserver.valueCount() == 1)
	}

	@Test
	fun testStateChangesExternallyForIndividualSourceEmissionAfterFirst() {
		val bonusList1 = CharacterCreationRobots.arbitraryBonusList
		val bonusList2 = CharacterCreationRobots.arbitraryBonusList2
		val prereq = TestPrerequisites()
		val toTest = DndAbilities()
		val testObserver = TestObserver<ItemState<out DndAbilitySelection>>()
		toTest.externalStateChanges.subscribe(testObserver)
		toTest.start(prereq)

		prereq.emitLoading(0)
		prereq.emitLoading(1)
		prereq.emitCompleted(0, bonusList1)
		prereq.emitNormal(1)

		assert(testObserver.valueCount() == 3)
	}

	class TestPrerequisites : DndAbilityPrerequisites {

		override val sources: List<Observable<ItemState<out DndAbilitySource>>>
		override val concurrency: Concurrency = TestConcurrency
		private val observable1: PublishSubject<ItemState<out DndAbilitySource>> = PublishSubject.create()
		private val observable2: PublishSubject<ItemState<out DndAbilitySource>> = PublishSubject.create()
		private val observables = listOf(observable1, observable2)

		init {
			sources = listOf(observable1, observable2)
		}

		fun emitNormal(index: Int) {
			val source = Mockito.mock(DndAbilitySource::class.java)
			whenever(source.dndAbilityBonuses).thenReturn(CharacterCreationRobots.defaultBonusList)
			observables[index].onNext(Normal(source))
		}

		fun emitCompleted(index: Int, list: Array<DndAbilityBonus>) {
			val source = Mockito.mock(DndAbilitySource::class.java)
			whenever(source.dndAbilityBonuses).thenReturn(list)
			observables[index].onNext(Completed(source))
		}

		fun emitLoading(index: Int) {
			observables[index].onNext(Loading)
		}

	}

}
