package com.tendebit.dungeonmaster.charactercreation2.unit

import com.tendebit.dungeonmaster.charactercreation.feature.DndProficiency
import org.junit.Test

class TestDndProficiency {

	@Test
	fun testEquals() {
		val item1 = DndProficiency("Athletics", "example.com")
		val item2 = DndProficiency("Athletics", "example.com")

		assert(item1 == item2)
	}

	@Test
	fun testNotEquals() {
		val item1 = DndProficiency("Athletics", "example.com")
		val item2 = DndProficiency("Athletics", "another.com")

		assert(item1 != item2)
	}

	@Test
	fun testContains() {
		val item1 = DndProficiency("Athletics", "example.com")
		val item2 = DndProficiency("Athletics", "example.com")
		val testList = listOf(item1)

		assert(item2 in testList)
	}

}