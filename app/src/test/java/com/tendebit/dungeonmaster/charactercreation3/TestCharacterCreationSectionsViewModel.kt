package com.tendebit.dungeonmaster.charactercreation3

import com.tendebit.dungeonmaster.charactercreation3.viewmodel.CharacterCreationSectionsViewModel
import com.tendebit.dungeonmaster.charactercreation3.viewmodel.Page
import com.tendebit.dungeonmaster.charactercreation3.viewmodel.PageSection
import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import io.reactivex.subjects.PublishSubject
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when` as whenever


class TestCharacterCreationSectionsViewModel {

	@Test
	fun testHasZeroPagesWithNoSections() {
		val testProviders = emptyList<PageSection>()
		val toTest = CharacterCreationSectionsViewModel(testProviders)

		assert(toTest.pageCount == 0)
	}

	@Test
	fun testHasTwoPagesFromOneSection() {
		val mockPagesOne = listOf<Page>(
				Mockito.mock(Page::class.java),
				Mockito.mock(Page::class.java))
		val mockPagesTwo = emptyList<Page>()

		val mockProviderOne = Mockito.mock(PageSection::class.java)
		whenever(mockProviderOne.pages).thenReturn(mockPagesOne)
		whenever(mockProviderOne.changes).thenReturn(Observable.empty())
		whenever(mockProviderOne.pageCount).thenReturn(2)
		whenever(mockProviderOne.showLoading).thenReturn(false)
		whenever(mockProviderOne.pageAdditions).thenReturn(Observable.empty())
		whenever(mockProviderOne.pageRemovals).thenReturn(Observable.empty())

		val mockProviderTwo = Mockito.mock(PageSection::class.java)
		whenever(mockProviderTwo.pages).thenReturn(mockPagesTwo)
		whenever(mockProviderTwo.changes).thenReturn(Observable.empty())
		whenever(mockProviderTwo.pageCount).thenReturn(0)
		whenever(mockProviderTwo.showLoading).thenReturn(false)
		whenever(mockProviderTwo.pageAdditions).thenReturn(Observable.empty())
		whenever(mockProviderTwo.pageRemovals).thenReturn(Observable.empty())

		val toTest = CharacterCreationSectionsViewModel(listOf(mockProviderOne, mockProviderTwo))

		assert(toTest.pageCount == 2)
	}

	@Test
	fun testHasThreePagesFromTwoSections() {
		val mockPagesOne = listOf<Page>(
				Mockito.mock(Page::class.java),
				Mockito.mock(Page::class.java))
		val mockPagesTwo = listOf<Page>(
				Mockito.mock(Page::class.java))

		val mockProviderOne = Mockito.mock(PageSection::class.java)
		whenever(mockProviderOne.pages).thenReturn(mockPagesOne)
		whenever(mockProviderOne.changes).thenReturn(Observable.empty())
		whenever(mockProviderOne.pageCount).thenReturn(2)
		whenever(mockProviderOne.showLoading).thenReturn(false)
		whenever(mockProviderOne.pageAdditions).thenReturn(Observable.empty())
		whenever(mockProviderOne.pageRemovals).thenReturn(Observable.empty())

		val mockProviderTwo = Mockito.mock(PageSection::class.java)
		whenever(mockProviderTwo.pages).thenReturn(mockPagesTwo)
		whenever(mockProviderTwo.changes).thenReturn(Observable.empty())
		whenever(mockProviderTwo.pageCount).thenReturn(1)
		whenever(mockProviderTwo.showLoading).thenReturn(false)
		whenever(mockProviderTwo.pageAdditions).thenReturn(Observable.empty())
		whenever(mockProviderTwo.pageRemovals).thenReturn(Observable.empty())

		val toTest = CharacterCreationSectionsViewModel(listOf(mockProviderOne, mockProviderTwo))

		assert(toTest.pageCount == 3)
	}

	@Test
	fun testShowsLoadingWhenSectionWithZeroPagesShowsLoading() {
		val mockPagesOne = listOf<Page>(
				Mockito.mock(Page::class.java),
				Mockito.mock(Page::class.java))
		val mockPagesTwo = emptyList<Page>()

		val mockProviderOne = Mockito.mock(PageSection::class.java)
		whenever(mockProviderOne.pages).thenReturn(mockPagesOne)
		whenever(mockProviderOne.changes).thenReturn(Observable.empty())
		whenever(mockProviderOne.pageCount).thenReturn(2)
		whenever(mockProviderOne.showLoading).thenReturn(false)
		whenever(mockProviderOne.pageAdditions).thenReturn(Observable.empty())
		whenever(mockProviderOne.pageRemovals).thenReturn(Observable.empty())

		val mockProviderTwo = Mockito.mock(PageSection::class.java)
		whenever(mockProviderTwo.pages).thenReturn(mockPagesTwo)
		whenever(mockProviderTwo.changes).thenReturn(Observable.empty())
		whenever(mockProviderTwo.pageCount).thenReturn(0)
		whenever(mockProviderTwo.showLoading).thenReturn(true)
		whenever(mockProviderTwo.pageAdditions).thenReturn(Observable.empty())
		whenever(mockProviderTwo.pageRemovals).thenReturn(Observable.empty())

		val toTest = CharacterCreationSectionsViewModel(listOf(mockProviderOne, mockProviderTwo))

		assert(toTest.showLoading)
	}

	@Test
	fun testDoesNotShowLoadingWhenSectionWithOneOrMorePagesShowsLoading() {
		val mockPagesOne = listOf<Page>(
				Mockito.mock(Page::class.java),
				Mockito.mock(Page::class.java))
		val mockPagesTwo = emptyList<Page>()

		val mockProviderOne = Mockito.mock(PageSection::class.java)
		whenever(mockProviderOne.pages).thenReturn(mockPagesOne)
		whenever(mockProviderOne.changes).thenReturn(Observable.empty())
		whenever(mockProviderOne.pageCount).thenReturn(2)
		whenever(mockProviderOne.showLoading).thenReturn(true)
		whenever(mockProviderOne.pageAdditions).thenReturn(Observable.empty())
		whenever(mockProviderOne.pageRemovals).thenReturn(Observable.empty())

		val mockProviderTwo = Mockito.mock(PageSection::class.java)
		whenever(mockProviderTwo.pages).thenReturn(mockPagesTwo)
		whenever(mockProviderTwo.changes).thenReturn(Observable.empty())
		whenever(mockProviderTwo.pageCount).thenReturn(0)
		whenever(mockProviderTwo.showLoading).thenReturn(false)
		whenever(mockProviderTwo.pageAdditions).thenReturn(Observable.empty())
		whenever(mockProviderTwo.pageRemovals).thenReturn(Observable.empty())


		val toTest = CharacterCreationSectionsViewModel(listOf(mockProviderOne, mockProviderTwo))

		assert(!toTest.showLoading)
	}

	@Test
	fun testAfterSectionAddsPageAdditionEmits() {
		val mockPagesOne = listOf<Page>(
				Mockito.mock(Page::class.java),
				Mockito.mock(Page::class.java))
		val mockPagesTwo = listOf<Page>(
				Mockito.mock(Page::class.java),
				Mockito.mock(Page::class.java),
				Mockito.mock(Page::class.java))

		val changeSubject = PublishSubject.create<PageSection>()
		val addSubject = PublishSubject.create<Int>()

		val mockSection = Mockito.mock(PageSection::class.java)
		whenever(mockSection.pages).thenReturn(mockPagesOne).thenReturn(mockPagesTwo)
		whenever(mockSection.changes).thenReturn(changeSubject)
		whenever(mockSection.pageCount).thenReturn(2)
		whenever(mockSection.showLoading).thenReturn(false)
		whenever(mockSection.pageAdditions).thenReturn(addSubject)
		whenever(mockSection.pageRemovals).thenReturn(Observable.empty())

		val toTest = CharacterCreationSectionsViewModel(listOf(mockSection))
		val testObserverAdditions = TestObserver<Int>()
		val testObserverRemovals = TestObserver<Int>()

		toTest.pageAdditions.subscribe(testObserverAdditions)
		toTest.pageRemovals.subscribe(testObserverRemovals)
		addSubject.onNext(2)

		testObserverAdditions.assertValueCount(1)
		testObserverRemovals.assertValueCount(0)

		testObserverAdditions.assertValue(2)
	}

	@Test
	fun testAfterSectionRemovesPageRemovalEmits() {
		val mockPagesTwo = listOf<Page>(
				Mockito.mock(Page::class.java),
				Mockito.mock(Page::class.java),
				Mockito.mock(Page::class.java))

		val changeSubject = PublishSubject.create<PageSection>()
		val removalSubject = PublishSubject.create<Int>()

		val mockSection = Mockito.mock(PageSection::class.java)
		whenever(mockSection.pages).thenReturn(mockPagesTwo)
		whenever(mockSection.changes).thenReturn(changeSubject)
		whenever(mockSection.pageCount).thenReturn(2)
		whenever(mockSection.showLoading).thenReturn(false)
		whenever(mockSection.pageAdditions).thenReturn(Observable.empty())
		whenever(mockSection.pageRemovals).thenReturn(removalSubject)

		val toTest = CharacterCreationSectionsViewModel(listOf(mockSection))
		val testObserverAdditions = TestObserver<Int>()
		val testObserverRemovals = TestObserver<Int>()

		assert(toTest.pageCount == 3)

		toTest.pageAdditions.subscribe(testObserverAdditions)
		toTest.pageRemovals.subscribe(testObserverRemovals)
		removalSubject.onNext(2)

		testObserverAdditions.assertValueCount(0)
		testObserverRemovals.assertValueCount(1)

		testObserverRemovals.assertValue(2)
	}

	@Test
	fun testPageChangesHaveCorrectOffsetWithMultipleSections() {
		val mockStaticPages = listOf<Page>(
				Mockito.mock(Page::class.java),
				Mockito.mock(Page::class.java))

		val mockPagesOne = listOf<Page>(
				Mockito.mock(Page::class.java),
				Mockito.mock(Page::class.java))
		val mockPagesTwo = listOf<Page>(
				Mockito.mock(Page::class.java),
				Mockito.mock(Page::class.java),
				Mockito.mock(Page::class.java))


		val mockSection1 = Mockito.mock(PageSection::class.java)
		whenever(mockSection1.pages).thenReturn(mockStaticPages)
		whenever(mockSection1.changes).thenReturn(Observable.empty())
		whenever(mockSection1.pageCount).thenReturn(2)
		whenever(mockSection1.showLoading).thenReturn(false)
		whenever(mockSection1.pageAdditions).thenReturn(Observable.empty())
		whenever(mockSection1.pageRemovals).thenReturn(Observable.empty())

		val changeSubject = PublishSubject.create<PageSection>()
		val addSubject = PublishSubject.create<Int>()
		val mockSection2 = Mockito.mock(PageSection::class.java)
		whenever(mockSection2.pages).thenReturn(mockPagesOne).thenReturn(mockPagesTwo)
		whenever(mockSection2.changes).thenReturn(changeSubject)
		whenever(mockSection2.pageCount).thenReturn(2).thenReturn(3)
		whenever(mockSection2.showLoading).thenReturn(false)
		whenever(mockSection2.pageAdditions).thenReturn(addSubject)
		whenever(mockSection2.pageRemovals).thenReturn(Observable.empty())

		val toTest = CharacterCreationSectionsViewModel(listOf(mockSection1, mockSection2))
		val testObserverAdditions = TestObserver<Int>()
		val testObserverRemovals = TestObserver<Int>()
		toTest.pageAdditions.subscribe(testObserverAdditions)
		toTest.pageRemovals.subscribe(testObserverRemovals)
		addSubject.onNext(2)

		testObserverAdditions.assertValueCount(1)
		testObserverAdditions.assertValue(4)
	}

	@Test
	fun testAllowsNextActionForNonFinalPageInSection() {
		val mockPagesOne = listOf<Page>(
				Mockito.mock(Page::class.java),
				Mockito.mock(Page::class.java))
		val mockPagesTwo = listOf<Page>(
				Mockito.mock(Page::class.java))

		val mockProviderOne = Mockito.mock(PageSection::class.java)
		whenever(mockProviderOne.pages).thenReturn(mockPagesOne)
		whenever(mockProviderOne.changes).thenReturn(Observable.empty())
		whenever(mockProviderOne.pageCount).thenReturn(2)
		whenever(mockProviderOne.showLoading).thenReturn(false)
		whenever(mockProviderOne.isComplete).thenReturn(false)
		whenever(mockProviderOne.pageAdditions).thenReturn(Observable.empty())
		whenever(mockProviderOne.pageRemovals).thenReturn(Observable.empty())

		val mockProviderTwo = Mockito.mock(PageSection::class.java)
		whenever(mockProviderTwo.pages).thenReturn(mockPagesTwo)
		whenever(mockProviderTwo.changes).thenReturn(Observable.empty())
		whenever(mockProviderTwo.pageCount).thenReturn(1)
		whenever(mockProviderTwo.showLoading).thenReturn(false)
		whenever(mockProviderTwo.isComplete).thenReturn(false)
		whenever(mockProviderTwo.pageAdditions).thenReturn(Observable.empty())
		whenever(mockProviderTwo.pageRemovals).thenReturn(Observable.empty())

		val toTest = CharacterCreationSectionsViewModel(listOf(mockProviderOne, mockProviderTwo))

		val actions = toTest.getPageActions(0)
		assert(actions.size == 2)
		assert(actions.contains(PageAction.NAVIGATE_BACK) && actions.contains(PageAction.NAVIGATE_NEXT))
	}

	@Test
	fun testDoesNotAllowNextActionForFinalPageInSection() {
		val mockPagesOne = listOf<Page>(
				Mockito.mock(Page::class.java),
				Mockito.mock(Page::class.java))
		val mockPagesTwo = listOf<Page>(
				Mockito.mock(Page::class.java))

		val mockProviderOne = Mockito.mock(PageSection::class.java)
		whenever(mockProviderOne.pages).thenReturn(mockPagesOne)
		whenever(mockProviderOne.changes).thenReturn(Observable.empty())
		whenever(mockProviderOne.pageCount).thenReturn(2)
		whenever(mockProviderOne.showLoading).thenReturn(false)
		whenever(mockProviderOne.isComplete).thenReturn(false)
		whenever(mockProviderOne.pageAdditions).thenReturn(Observable.empty())
		whenever(mockProviderOne.pageRemovals).thenReturn(Observable.empty())


		val mockProviderTwo = Mockito.mock(PageSection::class.java)
		whenever(mockProviderTwo.pages).thenReturn(mockPagesTwo)
		whenever(mockProviderTwo.changes).thenReturn(Observable.empty())
		whenever(mockProviderTwo.pageCount).thenReturn(1)
		whenever(mockProviderTwo.showLoading).thenReturn(false)
		whenever(mockProviderTwo.isComplete).thenReturn(false)
		whenever(mockProviderTwo.pageAdditions).thenReturn(Observable.empty())
		whenever(mockProviderTwo.pageRemovals).thenReturn(Observable.empty())

		val toTest = CharacterCreationSectionsViewModel(listOf(mockProviderOne, mockProviderTwo))

		val actions = toTest.getPageActions(1)
		assert(actions.size == 1)
		assert(actions.contains(PageAction.NAVIGATE_BACK))
	}

}