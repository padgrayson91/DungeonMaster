package com.tendebit.dungeonmaster

import com.tendebit.dungeonmaster.charactercreation.viewpager.BasePageCollection
import com.tendebit.dungeonmaster.charactercreation.viewpager.PageInsertion
import com.tendebit.dungeonmaster.charactercreation.viewpager.PageRemoval
import com.tendebit.dungeonmaster.charactercreation.viewpager.ViewModel
import io.reactivex.observers.TestObserver
import org.junit.Test
import org.mockito.Mockito

class TestBasePageCollection {

	@Test
	fun testInitialStateIsEmpty() {
		val toTest = BasePageCollection()
		assert(toTest.isEmpty())
	}

	@Test
	fun testInsertingPastEndEmitsPageInsertionAtProperIndex() {
		val toTest = BasePageCollection()
		val testObserver = TestObserver<PageInsertion>()

		toTest.pageAdditions.subscribe(testObserver)
		toTest.insertPage(Mockito.mock(ViewModel::class.java), 2)

		testObserver.assertValueCount(1)
		testObserver.assertValue { it.range.single() == 0 }
		assert(toTest.size == 1)
	}

	@Test
	fun testAddingAndRemovingPageEmitsPageInsertionAndRemoval() {
		val toTest = BasePageCollection()
		val testObserver = TestObserver<PageInsertion>()
		val testObserver2 = TestObserver<PageRemoval>()

		toTest.pageAdditions.subscribe(testObserver)
		toTest.pageRemovals.subscribe(testObserver2)
		toTest.insertPage(Mockito.mock(ViewModel::class.java), 0)
		testObserver.assertValueCount(1)
		assert(toTest.size == 1)
		toTest.removePages(0 until 1)
		testObserver.assertValue { it.range.single() == 0 }
		testObserver2.assertValueCount(1)
		testObserver.assertValue { it.range.single() == 0 }
		assert(toTest.isEmpty())
	}

	@Test
	fun testMultipleInsertionYieldsCorrectOrder() {

		val toTest = BasePageCollection()
		val testObserver = TestObserver<PageInsertion>()
		val testPages = listOf(
				Mockito.mock(ViewModel::class.java),
				Mockito.mock(ViewModel::class.java),
				Mockito.mock(ViewModel::class.java),
				Mockito.mock(ViewModel::class.java),
				Mockito.mock(ViewModel::class.java))

		toTest.pageAdditions.subscribe(testObserver)
		toTest.insertPage(testPages[4], 0)
		toTest.insertPage(testPages[3], 0)
		toTest.insertPage(testPages[2], 0)
		toTest.insertPage(testPages[1], 0)
		toTest.insertPage(testPages[0], 0)

		testObserver.assertValueCount(5)
		assert(toTest.size == 5)
		assert(toTest.pages == testPages)
	}

	@Test
	fun testMutlipleInsertionAndRemoval() {
		val toTest = BasePageCollection()
		val testObserver = TestObserver<PageInsertion>()
		val testObserver2 = TestObserver<PageRemoval>()
		val testPages = listOf(
				Mockito.mock(ViewModel::class.java),
				Mockito.mock(ViewModel::class.java),
				Mockito.mock(ViewModel::class.java),
				Mockito.mock(ViewModel::class.java),
				Mockito.mock(ViewModel::class.java))

		toTest.pageAdditions.subscribe(testObserver)
		toTest.pageRemovals.subscribe(testObserver2)
		toTest.insertPage(testPages[0], 0)
		toTest.insertPage(testPages[1], 0)
		toTest.removePages(0 until 1)
		toTest.insertPage(testPages[2], 1)
		toTest.removePages(0 until 2)
		toTest.insertPage(testPages[3], 0)
		toTest.insertPage(testPages[4], 1)

		testObserver.assertValueCount(5)
		testObserver2.assertValueCount(2)
		assert(toTest.pages.size == 2)

		assert(toTest.pages[0] == testPages[3])
		assert(toTest.pages[1] == testPages[4])
	}
}