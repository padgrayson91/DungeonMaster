package com.tendebit.dungeonmaster.charactercreation3.proficiency

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tendebit.dungeonmaster.R
import com.tendebit.dungeonmaster.charactercreation3.Normal
import com.tendebit.dungeonmaster.charactercreation3.proficiency.view.DndProficiencyGroupFragment
import com.tendebit.dungeonmaster.charactercreation3.proficiency.viewmodel.DndProficiencyViewModel
import com.tendebit.dungeonmaster.charactercreation3.proficiency.viewmodel.MultiSelectViewModel
import com.tendebit.dungeonmaster.testhelpers.CharacterCreationViewRobots
import io.reactivex.Observable
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.`when` as whenever

@RunWith(AndroidJUnit4::class)
class TestProficiencyGroupFragment {

	@Test
	fun testHeaderTextIsCorrectForSingleRemainingChoice() {
		val viewModel = Mockito.mock(MultiSelectViewModel::class.java)
		val children = listOf(
				DndProficiencyViewModel(Normal(CharacterCreationViewRobots.standardProficiencyList[0])),
				DndProficiencyViewModel(Normal(CharacterCreationViewRobots.standardProficiencyList[1])))
		whenever(viewModel.changes).thenReturn(Observable.empty())
		whenever(viewModel.children).thenReturn(children)
		whenever(viewModel.remainingChoices).thenReturn(1)
		val scenario = launchFragmentInContainer<DndProficiencyGroupFragment>(themeResId = R.style.AppTheme)
		scenario.onFragment { it.viewModel = viewModel }

		onView(withId(R.id.instructions)).check(matches(withText("Choose 1 Proficiency")))
	}

	@Test
	fun testHeaderTextIsCorrectForMultipleRemainingChoices() {
		val viewModel = Mockito.mock(MultiSelectViewModel::class.java)
		val children = listOf(
				DndProficiencyViewModel(Normal(CharacterCreationViewRobots.standardProficiencyList[0])),
				DndProficiencyViewModel(Normal(CharacterCreationViewRobots.standardProficiencyList[1])))
		whenever(viewModel.changes).thenReturn(Observable.empty())
		whenever(viewModel.children).thenReturn(children)
		whenever(viewModel.remainingChoices).thenReturn(2)
		val scenario = launchFragmentInContainer<DndProficiencyGroupFragment>(themeResId = R.style.AppTheme)
		scenario.onFragment { it.viewModel = viewModel }

		onView(withId(R.id.instructions)).check(matches(withText("Choose 2 Proficiencies")))
	}

	@Test
	fun testHeaderTextIsCorrectForNoRemainingChoices() {
		val viewModel = Mockito.mock(MultiSelectViewModel::class.java)
		val children = listOf(
				DndProficiencyViewModel(Normal(CharacterCreationViewRobots.standardProficiencyList[0])),
				DndProficiencyViewModel(Normal(CharacterCreationViewRobots.standardProficiencyList[1])))
		whenever(viewModel.changes).thenReturn(Observable.empty())
		whenever(viewModel.children).thenReturn(children)
		whenever(viewModel.remainingChoices).thenReturn(0)
		val scenario = launchFragmentInContainer<DndProficiencyGroupFragment>(themeResId = R.style.AppTheme)
		scenario.onFragment { it.viewModel = viewModel }

		onView(withId(R.id.instructions)).check(matches(withText("Selection Complete")))
	}

}
