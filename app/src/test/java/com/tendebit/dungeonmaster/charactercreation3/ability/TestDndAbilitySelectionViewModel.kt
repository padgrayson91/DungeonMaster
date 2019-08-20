package com.tendebit.dungeonmaster.charactercreation3.ability

import com.tendebit.dungeonmaster.charactercreation3.ability.viewmodel.DndAbilityDiceRollSelectionViewModel
import com.tendebit.dungeonmaster.charactercreation3.ability.viewmodel.DndAbilitySelectionViewModel
import com.tendebit.dungeonmaster.charactercreation3.abilitycore.AbilityProvider
import com.tendebit.dungeonmaster.charactercreation3.abilitycore.DndAbilitySelection
import com.tendebit.dungeonmaster.testhelpers.CharacterCreationRobots
import com.tendebit.dungeonmaster.testhelpers.TestConcurrency
import com.tendebit.dungeonmastercore.model.state.Completed
import com.tendebit.dungeonmastercore.model.state.ItemState
import com.tendebit.dungeonmastercore.model.state.Normal
import io.reactivex.observers.TestObserver
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when` as whenever

@ExperimentalCoroutinesApi
class TestDndAbilitySelectionViewModel {

	private val concurrency = TestConcurrency
	private lateinit var abilities: AbilityProvider
	private lateinit var internalStateChanges: Subject<ItemState<out DndAbilitySelection>>
	private lateinit var externalStateChanges: Subject<ItemState<out DndAbilitySelection>>
	private val defaultState = Normal(DndAbilitySelection(concurrency, CharacterCreationRobots.emptyAbilitySlotStateList))
	private var mostRecentState: ItemState<out DndAbilitySelection> = defaultState

	@Before
	fun setup() {
		abilities = Mockito.mock(AbilityProvider::class.java)
		internalStateChanges = PublishSubject.create()
		externalStateChanges = PublishSubject.create()
		internalStateChanges.mergeWith(externalStateChanges).subscribe { mostRecentState = it }
		whenever(abilities.externalStateChanges).thenReturn(externalStateChanges)
		whenever(abilities.internalStateChanges).thenReturn(internalStateChanges)
		whenever(abilities.state).thenReturn(mostRecentState)
	}

	@Test
	fun testInitialStateIsNotLoadingIfDefaultStateIsNormal() {
		val toTest = DndAbilitySelectionViewModel(abilities, concurrency)
		assert(!toTest.showLoading)
	}

	@Test
	fun testInitialStateIsNotCompletedIfDefaultStateIsNormal() {
		val toTest = DndAbilitySelectionViewModel(abilities, concurrency)
		assert(!toTest.isComplete)
	}

	@Test
	fun testInitialStateIsCompleteIfProviderStateIsComplete() {
		whenever(abilities.state).thenReturn(Completed(DndAbilitySelection(concurrency, CharacterCreationRobots.emptyAbilitySlotStateList)))
		val toTest = DndAbilitySelectionViewModel(abilities, concurrency)
		assert(toTest.isComplete)
	}

	@Test
	fun testExternalStateChangeUpdatesViewModel() {
		val toTest = DndAbilitySelectionViewModel(abilities, concurrency)
		val testObserver = TestObserver<DndAbilitySelectionViewModel>()
		toTest.changes.subscribe(testObserver)
		val previousCount = testObserver.valueCount()
		externalStateChanges.onNext(Completed(DndAbilitySelection(concurrency, CharacterCreationRobots.emptyAbilitySlotStateList)))
		assert(testObserver.valueCount() == previousCount + 1) { "Expected ${previousCount + 1} updated but had ${testObserver.valueCount()}"}
	}

	@Test
	fun testInternalStateChangeUpdatesViewModel() {
		val toTest = DndAbilitySelectionViewModel(abilities, concurrency)
		val testObserver = TestObserver<DndAbilitySelectionViewModel>()
		toTest.changes.subscribe(testObserver)
		val previousCount = testObserver.valueCount()
		internalStateChanges.onNext(Completed(defaultState.item))
		assert(testObserver.valueCount() == previousCount + 1) { "Expected ${previousCount + 1} updated but had ${testObserver.valueCount()}"}
	}

	@Test
	fun testPerformAutoRollDoesNotChangeOuterViewModel() {
		val toTest = DndAbilitySelectionViewModel(abilities, concurrency)
		val testObserver = TestObserver<DndAbilitySelectionViewModel>()
		toTest.changes.subscribe(testObserver)
		val previousCount = testObserver.valueCount()
		toTest.onClickRoll()
		assert(testObserver.valueCount() == previousCount) { "Expected $previousCount updated but had ${testObserver.valueCount()}"}
	}

	@Test
	fun testPerformAutoRollChangesRollViewModel() {
		val toTest = DndAbilitySelectionViewModel(abilities, concurrency)
		val testObserver = TestObserver<DndAbilityDiceRollSelectionViewModel>()
		toTest.rolls?.changes?.subscribe(testObserver)
		toTest.onClickRoll()
		assert(testObserver.valueCount() == 1)
	}

}
