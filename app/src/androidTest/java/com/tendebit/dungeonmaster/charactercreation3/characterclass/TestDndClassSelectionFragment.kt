package com.tendebit.dungeonmaster.charactercreation3.characterclass

import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers.hasChildCount
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tendebit.dungeonmaster.R
import com.tendebit.dungeonmaster.charactercreation3.ID_KEY
import com.tendebit.dungeonmaster.charactercreation3.characterclass.view.DndClassSelectionFragment
import com.tendebit.dungeonmaster.charactercreation3.characterclass.viewmodel.DndCharacterClassViewModel
import com.tendebit.dungeonmaster.core.model.state.Normal
import com.tendebit.dungeonmaster.core.platform.ViewModelManager
import com.tendebit.dungeonmaster.core.platform.ViewModels
import com.tendebit.dungeonmaster.core.viewmodel3.SingleSelectViewModel
import com.tendebit.dungeonmaster.testhelpers.CharacterCreationViewRobots
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import org.hamcrest.CoreMatchers.not
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.`when` as whenever

@RunWith(AndroidJUnit4::class)
@Suppress("UNCHECKED_CAST")
class TestDndClassSelectionFragment {

	@Test
	fun testNoCardsDisplayWithNoClasses() {
		val viewModel = Mockito.mock(SingleSelectViewModel::class.java) as SingleSelectViewModel<DndCharacterClass>
		val children = emptyList<DndCharacterClassViewModel>()
		whenever(viewModel.changes).thenReturn(Observable.empty())
		whenever(viewModel.children).thenReturn(children)
		whenever(viewModel.itemCount).thenReturn(0)
		val viewModelManager = Mockito.mock(ViewModelManager::class.java)
		whenever(viewModelManager.findViewModel<SingleSelectViewModel<DndCharacterClass>>(0)).thenReturn(viewModel)
		ViewModels.viewModelAccess = { viewModelManager }
		launchFragmentInContainer<DndClassSelectionFragment>(themeResId = R.style.AppTheme, fragmentArgs = Bundle().apply { putLong(ID_KEY, 0) })

		Espresso.onView(withId(R.id.item_list)).check(ViewAssertions.matches(hasChildCount(0)))
	}

	@Test
	fun testTwoCardsDisplayWithTwoClasses() {
		val viewModel = Mockito.mock(SingleSelectViewModel::class.java) as SingleSelectViewModel<DndCharacterClass>
		val classStates = CharacterCreationViewRobots.standardClassList.subList(0, 2).map { Normal(it) }
		val children = classStates.map { DndCharacterClassViewModel(it) }
		whenever(viewModel.changes).thenReturn(Observable.empty())
		whenever(viewModel.children).thenReturn(children)
		whenever(viewModel.itemCount).thenReturn(2)
		val viewModelManager = Mockito.mock(ViewModelManager::class.java)
		whenever(viewModelManager.findViewModel<SingleSelectViewModel<DndCharacterClass>>(0)).thenReturn(viewModel)
		ViewModels.viewModelAccess = { viewModelManager }
		launchFragmentInContainer<DndClassSelectionFragment>(themeResId = R.style.AppTheme, fragmentArgs = Bundle().apply { putLong(ID_KEY, 0) })

		Espresso.onView(withId(R.id.item_list)).check(ViewAssertions.matches(hasChildCount(2)))
	}

	@Test
	fun testCardTextDisplays() {
		val viewModel = Mockito.mock(SingleSelectViewModel::class.java) as SingleSelectViewModel<DndCharacterClass>
		val classStates = CharacterCreationViewRobots.standardClassList.subList(0, 2).map { Normal(it) }
		val children = classStates.map { DndCharacterClassViewModel(it) }
		whenever(viewModel.changes).thenReturn(Observable.empty())
		whenever(viewModel.children).thenReturn(children)
		whenever(viewModel.itemCount).thenReturn(2)
		val viewModelManager = Mockito.mock(ViewModelManager::class.java)
		whenever(viewModelManager.findViewModel<SingleSelectViewModel<DndCharacterClass>>(0)).thenReturn(viewModel)
		ViewModels.viewModelAccess = { viewModelManager }
		launchFragmentInContainer<DndClassSelectionFragment>(themeResId = R.style.AppTheme, fragmentArgs = Bundle().apply { putLong(ID_KEY, 0) })

		Espresso.onView(withId(R.id.item_list)).check(ViewAssertions.matches(hasDescendant(withText(CharacterCreationViewRobots.standardClassList[0].name))))
	}

	@Test
	fun testShowsLoadingWithNullViewModel() {
		launchFragmentInContainer<DndClassSelectionFragment>(themeResId = R.style.AppTheme)
		Espresso.onView(withId(R.id.loading_dialog)).check(ViewAssertions.matches(isDisplayed()))
	}

	@Test
	fun testShowsLoadingWhenViewModelIsLoading() {
		val viewModel = Mockito.mock(SingleSelectViewModel::class.java) as SingleSelectViewModel<DndCharacterClass>
		val classStates = CharacterCreationViewRobots.standardClassList.subList(0, 2).map { Normal(it) }
		val children = classStates.map { DndCharacterClassViewModel(it) }
		whenever(viewModel.changes).thenReturn(Observable.empty())
		whenever(viewModel.children).thenReturn(children)
		whenever(viewModel.itemCount).thenReturn(2)
		whenever(viewModel.showLoading).thenReturn(true)
		val viewModelManager = Mockito.mock(ViewModelManager::class.java)
		whenever(viewModelManager.findViewModel<SingleSelectViewModel<DndCharacterClass>>(0)).thenReturn(viewModel)
		ViewModels.viewModelAccess = { viewModelManager }
		launchFragmentInContainer<DndClassSelectionFragment>(themeResId = R.style.AppTheme, fragmentArgs = Bundle().apply { putLong(ID_KEY, 0) })
		Espresso.onView(withId(R.id.loading_dialog)).check(ViewAssertions.matches(isDisplayed()))
	}

	@Test
	fun testHidesLoadingWhenViewModelStopsLoading() {
		val viewModel = Mockito.mock(SingleSelectViewModel::class.java) as SingleSelectViewModel<DndCharacterClass>
		val classStates = CharacterCreationViewRobots.standardClassList.subList(0, 2).map { Normal(it) }
		val children = classStates.map { DndCharacterClassViewModel(it) }
		val changes = PublishSubject.create<SingleSelectViewModel<DndCharacterClass>>()
		var loading = true
		whenever(viewModel.changes).thenReturn(changes)
		whenever(viewModel.children).thenReturn(children)
		whenever(viewModel.itemCount).thenReturn(2)
		whenever(viewModel.itemChanges).thenReturn(Observable.empty())
		whenever(viewModel.showLoading).thenAnswer { loading }
		val viewModelManager = Mockito.mock(ViewModelManager::class.java)
		whenever(viewModelManager.findViewModel<SingleSelectViewModel<DndCharacterClass>>(0)).thenReturn(viewModel)
		ViewModels.viewModelAccess = { viewModelManager }
		launchFragmentInContainer<DndClassSelectionFragment>(themeResId = R.style.AppTheme, fragmentArgs = Bundle().apply { putLong(ID_KEY, 0) })
		loading = false
		changes.onNext(viewModel)
		Espresso.onView(withId(R.id.loading_dialog)).check(ViewAssertions.matches(not(isDisplayed())))
	}

}