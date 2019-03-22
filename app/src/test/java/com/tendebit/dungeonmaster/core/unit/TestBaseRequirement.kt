package com.tendebit.dungeonmaster.core.unit

import com.tendebit.dungeonmaster.core.blueprint.requirement.BaseRequirement
import com.tendebit.dungeonmaster.core.blueprint.requirement.Requirement
import org.junit.Test

class TestBaseRequirement {

	class SubClassA(initialValue: String?): BaseRequirement<String>(initialValue) {
		override fun onUpdate(item: String?) {}

		override fun statusForItem(item: String?) = Requirement.Status.FULFILLED

		override fun onRevoke() {}
	}

	class SubClassB(initialValue: String?): BaseRequirement<String>(initialValue) {
		override fun onUpdate(item: String?) {}

		override fun statusForItem(item: String?) = Requirement.Status.FULFILLED

		override fun onRevoke() {}
	}

	@Test
	fun testSameSubclassAreEqualWithEqualValues() {
		assert(SubClassA("test") == SubClassA("test"))
	}

	@Test
	fun testDifferentSubclassesAreUnequalWithEqualValues() {
		assert(SubClassB("test") as Requirement<String> != SubClassA("test"))
	}

	@Test
	fun testSameSubclassHasSameHashcodeWithEqualValues() {
		assert(SubClassA("blahahha").hashCode() == SubClassA("blahahha").hashCode())
	}

	@Test
	fun testDifferentSubclassHasDifferentHashcodeWithEqualValues() {
		assert(SubClassA("blahahha").hashCode() != SubClassB("blahahha").hashCode())
	}

}