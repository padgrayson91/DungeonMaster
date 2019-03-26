package com.tendebit.dungeonmaster.core.unit

import com.tendebit.dungeonmaster.core.blueprint.Delta
import org.junit.Test
import java.math.BigInteger
import kotlin.math.max

class TestDelta {

	@Test
	fun testInsertThreeItems() {
		val oldList = emptyList<String>()
		val newList = listOf("Hello", "World", "")

		val toTest = Delta.from(oldList, newList)

		assert(toTest.size == max(oldList.size, newList.size)) { "Got $toTest" }
		assert(toTest.all { it.type == Delta.Type.INSERTION })
	}

	@Test
	fun testRemoveThreeItems() {
		val oldList = emptyList<String>()
		val newList = listOf("Hello", "World", "")

		val toTest = Delta.from(newList, oldList)

		assert(toTest.size == max(oldList.size, newList.size)) { "Got $toTest" }
		assert(toTest.all { it.type == Delta.Type.REMOVAL })
	}

	@Test
	fun removeFirstItem() {
		val oldList = listOf("Hello", "World", "")
		val newList = listOf("World", "")

		val toTest = Delta.from(oldList, newList)

		assert(toTest.size == max(oldList.size, newList.size)) { "Got $toTest" }
		assert(toTest[0].type == Delta.Type.REMOVAL)
		assert(toTest[1].type == Delta.Type.UNCHANGED)
		assert(toTest[2].type == Delta.Type.UNCHANGED)
	}

	@Test
	fun removeLastItem() {
		val oldList = listOf("Hello", "World", "")
		val newList = listOf("Hello", "World")

		val toTest = Delta.from(oldList, newList)

		assert(toTest.size == max(oldList.size, newList.size)) { "Got $toTest" }
		assert(toTest[0].type == Delta.Type.UNCHANGED)
		assert(toTest[1].type == Delta.Type.UNCHANGED)
		assert(toTest[2].type == Delta.Type.REMOVAL)
	}

	@Test
	fun testRemoveMiddleItem() {
		val oldList = listOf("Hello", "World", "")
		val newList = listOf("Hello", "")

		val toTest = Delta.from(oldList, newList)

		assert(toTest.size == max(oldList.size, newList.size)) { "Got $toTest" }
		assert(toTest[0].type == Delta.Type.UNCHANGED)
		assert(toTest[1].type == Delta.Type.REMOVAL)
		assert(toTest[2].type == Delta.Type.UNCHANGED)
	}

	@Test
	fun testRemoveMultipleItems() {
		val oldList = listOf("Hello", "Great", "Big", "Beautiful", "World", "!")
		val newList = listOf("Hello", "Big", "World")

		val toTest = Delta.from(oldList, newList)

		assert(toTest.size == max(oldList.size, newList.size)) { "Got $toTest" }
		assert(toTest[0].type == Delta.Type.UNCHANGED)
		assert(toTest[1].type == Delta.Type.REMOVAL)
		assert(toTest[2].type == Delta.Type.UNCHANGED)
		assert(toTest[3].type == Delta.Type.REMOVAL)
		assert(toTest[4].type == Delta.Type.UNCHANGED)
		assert(toTest[5].type == Delta.Type.REMOVAL)
	}

	@Test
	fun testInsertFirstItem() {
		val oldList = listOf("Hello", "World", "")
		val newList = listOf("Warm", "Hello", "World", "")

		val toTest = Delta.from(oldList, newList)

		assert(toTest.size == max(oldList.size, newList.size)) { "Got $toTest" }
		assert(toTest[0].type == Delta.Type.INSERTION)
		assert(toTest[1].type == Delta.Type.UNCHANGED)
		assert(toTest[2].type == Delta.Type.UNCHANGED)
		assert(toTest[3].type == Delta.Type.UNCHANGED)
	}

	@Test
	fun testInsertDuplicateItem() {
		val oldList = listOf("Hello", "World", "")
		val newList = listOf("Hello", "Hello", "World", "")

		val toTest = Delta.from(oldList, newList)

		assert(toTest.size == max(oldList.size, newList.size)) { "Got $toTest" }
		assert(toTest[0].type == Delta.Type.UNCHANGED)
		assert(toTest[1].type == Delta.Type.INSERTION)
		assert(toTest[2].type == Delta.Type.UNCHANGED)
		assert(toTest[3].type == Delta.Type.UNCHANGED)
	}

	@Test
	fun testInsertMultipleItem() {
		val oldList = listOf("Hello", "World", "")
		val newList = listOf("Hello", "Hello", "Warm", "New", "World", "")

		val toTest = Delta.from(oldList, newList)

		assert(toTest.size ==  max(oldList.size, newList.size)) { "Got $toTest" }
		assert(toTest[0].type == Delta.Type.UNCHANGED)
		assert(toTest[1].type == Delta.Type.INSERTION)
		assert(toTest[2].type == Delta.Type.INSERTION)
		assert(toTest[3].type == Delta.Type.INSERTION)
		assert(toTest[4].type == Delta.Type.UNCHANGED)
		assert(toTest[5].type == Delta.Type.UNCHANGED)
	}

	@Test
	fun testInsertAndRemove() {
		val oldList = listOf("Hello", "World", "")
		val newList = listOf("Hello", "Hello", "Warm", "New", "World")

		val toTest = Delta.from(oldList, newList)

		assert(toTest.size == 6) { "Got $toTest" }
		assert(toTest[0].type == Delta.Type.UNCHANGED)
		assert(toTest[1].type == Delta.Type.INSERTION)
		assert(toTest[2].type == Delta.Type.INSERTION)
		assert(toTest[3].type == Delta.Type.INSERTION)
		assert(toTest[4].type == Delta.Type.UNCHANGED)
		assert(toTest[5].type == Delta.Type.REMOVAL)
	}

	@Test
	fun testUnchangedIsSameObject() {
		val oldList = listOf(BigInteger("10"), BigInteger("20"))
		val newList = listOf(BigInteger("10"), BigInteger("20"))

		val toTest = Delta.from(oldList, newList)

		assert(toTest[0].type == Delta.Type.UNCHANGED)
		assert(toTest[1].type == Delta.Type.UNCHANGED)
		assert(oldList[0] !== newList[0])
		assert(toTest[0].item === oldList[0])
	}

}