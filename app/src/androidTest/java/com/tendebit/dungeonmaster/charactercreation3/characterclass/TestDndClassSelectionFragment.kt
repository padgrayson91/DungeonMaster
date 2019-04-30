package com.tendebit.dungeonmaster.charactercreation3.characterclass

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tendebit.dungeonmaster.R
import com.tendebit.dungeonmaster.charactercreation3.Normal
import com.tendebit.dungeonmaster.charactercreation3.characterclass.view.DndClassSelectionFragment
import com.tendebit.dungeonmaster.charactercreation3.characterclass.viewmodel.DndCharacterClassViewModel
import com.tendebit.dungeonmaster.charactercreation3.characterclass.viewmodel.SingleSelectViewModel
import com.tendebit.dungeonmaster.testhelpers.CharacterCreationViewRobots
import io.reactivex.Observable
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.`when` as whenever

@RunWith(AndroidJUnit4::class)
class TestDndClassSelectionFragment {

	@Test
	fun testNoCardsDisplayWithNoClasses() {
		val viewModel = Mockito.mock(SingleSelectViewModel::class.java)
		val children = emptyList<DndCharacterClassViewModel>()
		whenever(viewModel.changes).thenReturn(Observable.empty())
		whenever(viewModel.children).thenReturn(children)
		whenever(viewModel.itemCount).thenReturn(0)
		val scenario = launchFragmentInContainer<DndClassSelectionFragment>(themeResId = R.style.AppTheme)
		scenario.onFragment { it.viewModel = viewModel }

		Espresso.onView(withId(R.id.item_list)).check(ViewAssertions.matches(hasChildCount(0)))
	}

	@Test
	fun testTwoCardsDisplayWithTwoClasses() {
		val viewModel = Mockito.mock(SingleSelectViewModel::class.java)
		val classStates = CharacterCreationViewRobots.standardClassList.subList(0, 2).map { Normal(it) }
		val children = classStates.map { DndCharacterClassViewModel(it) }
		whenever(viewModel.changes).thenReturn(Observable.empty())
		whenever(viewModel.children).thenReturn(children)
		whenever(viewModel.itemCount).thenReturn(2)
		val scenario = launchFragmentInContainer<DndClassSelectionFragment>(themeResId = R.style.AppTheme)
		scenario.onFragment { it.viewModel = viewModel }

		Espresso.onView(withId(R.id.item_list)).check(ViewAssertions.matches(hasChildCount(2)))
	}

	@Test
	fun testCardTextDisplays() {
		val viewModel = Mockito.mock(SingleSelectViewModel::class.java)
		val classStates = CharacterCreationViewRobots.standardClassList.subList(0, 2).map { Normal(it) }
		val children = classStates.map { DndCharacterClassViewModel(it) }
		whenever(viewModel.changes).thenReturn(Observable.empty())
		whenever(viewModel.children).thenReturn(children)
		whenever(viewModel.itemCount).thenReturn(2)
		val scenario = launchFragmentInContainer<DndClassSelectionFragment>(themeResId = R.style.AppTheme)
		scenario.onFragment { it.viewModel = viewModel }

		Espresso.onView(withId(R.id.item_list)).check(ViewAssertions.matches(hasDescendant(withText(CharacterCreationViewRobots.standardClassList[0].name))))
	}

}