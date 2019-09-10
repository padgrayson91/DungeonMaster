package com.tendebit.dungeonmaster.charactercreation3.ability

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tendebit.dungeonmaster.R
import com.tendebit.dungeonmaster.charactercreation3.ability.view.DndAbilityDiceRollSelectionFragment
import com.tendebit.dungeonmaster.charactercreation3.ability.viewmodel.DndAbilityDiceRollViewModel
import com.tendebit.dungeonmastercore.model.state.Normal
import com.tendebit.dungeonmastercore.viewmodel3.SingleSelectViewModel
import io.reactivex.Observable
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.`when` as whenever

@RunWith(AndroidJUnit4::class)
@Suppress("UNCHECKED_CAST")
class TestDndAbilityRollSelectionFragment {

	@Test
	fun testAtLeastThreeItemsAreVisible() {
		val viewModel = Mockito.mock(SingleSelectViewModel::class.java) as SingleSelectViewModel<Int>
		val rollStates = listOf(14, 8, 10, 12, 9, 5).map { Normal(it) }
		val children = rollStates.map { DndAbilityDiceRollViewModel(it) }
		whenever(viewModel.changes).thenReturn(Observable.empty())
		whenever(viewModel.children).thenReturn(children)
		whenever(viewModel.itemCount).thenReturn(6)
		whenever(viewModel.itemChanges).thenReturn(Observable.empty())

		val scenario = launchFragmentInContainer<DndAbilityDiceRollSelectionFragment>(themeResId = R.style.AppTheme)
		scenario.onFragment { it.viewModel = viewModel }

		Espresso.onView(ViewMatchers.withId(R.id.small_recycler)).check(ViewAssertions.matches(ViewMatchers.hasMinimumChildCount(3)))
	}

}
