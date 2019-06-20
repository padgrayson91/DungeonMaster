package com.tendebit.dungeonmaster.charactercreation3.proficiency

import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tendebit.dungeonmaster.R
import com.tendebit.dungeonmaster.charactercreation3.ID_KEY
import com.tendebit.dungeonmaster.charactercreation3.proficiency.view.DndProficiencyGroupFragment
import com.tendebit.dungeonmaster.charactercreation3.proficiency.viewmodel.DndProficiencyViewModel
import com.tendebit.dungeonmaster.core.model.Normal
import com.tendebit.dungeonmaster.core.platform.ViewModelManager
import com.tendebit.dungeonmaster.core.platform.ViewModels
import com.tendebit.dungeonmaster.core.viewmodel3.MultiSelectViewModel
import com.tendebit.dungeonmaster.testhelpers.CharacterCreationViewRobots
import com.tendebit.dungeonmaster.testhelpers.TestConcurrencyUi
import io.reactivex.Observable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.`when` as whenever

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class TestProficiencyGroupFragment {

	private val concurrency = TestConcurrencyUi

	@Test
	fun testHeaderTextIsCorrectForSingleRemainingChoice() {
		val viewModel = Mockito.mock(MultiSelectViewModel::class.java)
		val children = listOf(
				DndProficiencyViewModel(Normal(CharacterCreationViewRobots.standardProficiencyList[0]), concurrency),
				DndProficiencyViewModel(Normal(CharacterCreationViewRobots.standardProficiencyList[1]), concurrency))
		whenever(viewModel.changes).thenReturn(Observable.empty())
		whenever(viewModel.children).thenReturn(children)
		whenever(viewModel.remainingChoices).thenReturn(1)
		val viewModelManager = Mockito.mock(ViewModelManager::class.java)
		whenever(viewModelManager.findViewModel<MultiSelectViewModel>(0)).thenReturn(viewModel)
		ViewModels.viewModelAccess = { viewModelManager }
		launchFragmentInContainer<DndProficiencyGroupFragment>(themeResId = R.style.AppTheme, fragmentArgs = Bundle().apply { putLong(ID_KEY, 0) })

		onView(withId(R.id.instructions)).check(matches(withText("Choose 1 Proficiency")))
	}

	@Test
	fun testHeaderTextIsCorrectForMultipleRemainingChoices() {
		val viewModel = Mockito.mock(MultiSelectViewModel::class.java)
		val children = listOf(
				DndProficiencyViewModel(Normal(CharacterCreationViewRobots.standardProficiencyList[0]), concurrency),
				DndProficiencyViewModel(Normal(CharacterCreationViewRobots.standardProficiencyList[1]), concurrency))
		whenever(viewModel.changes).thenReturn(Observable.empty())
		whenever(viewModel.children).thenReturn(children)
		whenever(viewModel.remainingChoices).thenReturn(2)
		val viewModelManager = Mockito.mock(ViewModelManager::class.java)
		whenever(viewModelManager.findViewModel<MultiSelectViewModel>(0)).thenReturn(viewModel)
		ViewModels.viewModelAccess = { viewModelManager }
		launchFragmentInContainer<DndProficiencyGroupFragment>(themeResId = R.style.AppTheme, fragmentArgs = Bundle().apply { putLong(ID_KEY, 0) })

		onView(withId(R.id.instructions)).check(matches(withText("Choose 2 Proficiencies")))
	}

	@Test
	fun testHeaderTextIsCorrectForNoRemainingChoices() {
		val viewModel = Mockito.mock(MultiSelectViewModel::class.java)
		val children = listOf(
				DndProficiencyViewModel(Normal(CharacterCreationViewRobots.standardProficiencyList[0]), concurrency),
				DndProficiencyViewModel(Normal(CharacterCreationViewRobots.standardProficiencyList[1]), concurrency))
		whenever(viewModel.changes).thenReturn(Observable.empty())
		whenever(viewModel.children).thenReturn(children)
		whenever(viewModel.remainingChoices).thenReturn(0)
		val viewModelManager = Mockito.mock(ViewModelManager::class.java)
		whenever(viewModelManager.findViewModel<MultiSelectViewModel>(0)).thenReturn(viewModel)
		ViewModels.viewModelAccess = { viewModelManager }
		launchFragmentInContainer<DndProficiencyGroupFragment>(themeResId = R.style.AppTheme, fragmentArgs = Bundle().apply { putLong(ID_KEY, 0) })

		onView(withId(R.id.instructions)).check(matches(withText("Selection Complete")))
	}

}
