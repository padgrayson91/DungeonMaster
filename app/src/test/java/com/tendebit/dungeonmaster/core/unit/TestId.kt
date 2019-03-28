package com.tendebit.dungeonmaster.core.unit

import com.tendebit.dungeonmaster.core.Id
import org.junit.Test

class TestId {

	@Test
	fun testIdEqualsOther() {
		assert(Id("someString") == Id("someString"))
	}

	@Test
	fun testIdNotEqualsOther() {
		assert(Id("someString") != Id("someOtherString"))
	}

	@Test
	fun testIdEqualToCharSequence() {
		assert(Id("blah") == ("blah" as CharSequence))
	}

	@Test
	fun testIdNotEqualToCharSequence() {
		assert(Id("blah") != ("blahblah" as CharSequence))
	}

	@Test
	fun testIdPlusCharSequenceYieldsNewId() {
		val startId = Id("blah")
		val output = startId + "blah"
		assert(output == Id("blahblah"))
	}

	@Test
	fun testIdPlusIdYieldsNewId() {
		val startId = Id("blah")
		val output = startId + Id("blah")
		assert(output == Id("blahblah")) { "Had $output"}
	}

	@Test
	fun testContainsCharSequence() {
		assert(Id("Hello World").contains("Hello"))
	}

	@Test
	fun testCharSequenceContains() {
		assert("Hello World".contains(Id("Hello")))
	}

	@Test
	fun testContainsId() {
		assert(Id("Hello World").contains(Id("Hello")))
	}

	@Test
	fun testNotContainsCharSequence() {
		assert(!Id("HelloWorld").contains("Hello "))
	}

}
