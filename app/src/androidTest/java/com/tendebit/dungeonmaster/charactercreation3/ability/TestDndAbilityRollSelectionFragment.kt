package com.tendebit.dungeonmaster.charactercreation3.ability

import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tendebit.dungeonmaster.R
import com.tendebit.dungeonmaster.charactercreation3.ID_KEY
import com.tendebit.dungeonmaster.charactercreation3.ability.view.DndAbilityDiceRollSelectionFragment
import com.tendebit.dungeonmaster.charactercreation3.ability.viewmodel.DndAbilityDiceRollViewModel
import com.tendebit.dungeonmastercore.model.state.Normal
import com.tendebit.dungeonmastercore.platform.ViewModelManager
import com.tendebit.dungeonmastercore.platform.ViewModels
import com.tendebit.dungeonmastercore.viewmodel3.SingleSelectViewModel
import io.reactivex.Observable
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito

@RunWith(AndroidJUnit4::class)
@Suppress("UNCHECKED_CAST")
class TestDndAbilityRollSelectionFragment {

	@Test
	fun testAtLeastThreeItemsAreVisible() {
		val viewModel = Mockito.mock(SingleSelectViewModel::class.java) as SingleSelectViewModel<Int>
		val rollStates = listOf(14, 8, 10, 12, 9, 5).map { Normal(it) }
		val children = rollStates.map { DndAbilityDiceRollViewModel(it) }
		Mockito.`when`(viewModel.changes).thenReturn(Observable.empty())
		Mockito.`when`(viewModel.children).thenReturn(children)
		Mockito.`when`(viewModel.itemCount).thenReturn(6)
		val viewModelManager = Mockito.mock(ViewModelManager::class.java)
		Mockito.`when`(viewModelManager.findViewModel<SingleSelectViewModel<Int>>(0)).thenReturn(viewModel)
		ViewModels.viewModelAccess = { viewModelManager }
		launchFragmentInContainer<DndAbilityDiceRollSelectionFragment>(themeResId = R.style.AppTheme, fragmentArgs = Bundle().apply { putLong(ID_KEY, 0) })

		Espresso.onView(ViewMatchers.withId(R.id.small_recycler)).check(ViewAssertions.matches(ViewMatchers.hasMinimumChildCount(3)))
	}

}
