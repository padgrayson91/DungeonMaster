package com.tendebit.dungeonmaster.core

import com.tendebit.dungeonmaster.core.blueprint.Delta
import com.tendebit.dungeonmaster.core.blueprint.examination.Examination
import com.tendebit.dungeonmaster.core.blueprint.examination.Examiner
import com.tendebit.dungeonmaster.core.blueprint.examination.StaticExamination
import com.tendebit.dungeonmaster.core.blueprint.requirement.Requirement
import com.tendebit.dungeonmaster.testhelpers.SimpleFulfillment
import org.junit.Test
import org.mockito.Mockito

@Suppress("UNCHECKED_CAST")
class TestExaminer {

	@Test
	fun testInitialExaminationContainsAllInsertions() {
		val req1 = Mockito.mock(Requirement::class.java) as Requirement<Any>
		val req2 = Mockito.mock(Requirement::class.java) as Requirement<Any>
		val req3 = Mockito.mock(Requirement::class.java) as Requirement<Any>
		val toTest = object : Examiner<Any> {

			override fun examine(state: Any): Examination<Any> {
				return StaticExamination(listOf(
						SimpleFulfillment(req1),
						SimpleFulfillment(req2),
						SimpleFulfillment(req3)), false)
			}
		}

		val examination = toTest.examineWithDelta("", null)
		assert(examination.changes.size == 3)
		examination.changes.forEach {
			assert(it.type == Delta.Type.INSERTION)
		}
	}

	@Test
	fun testSecondExaminationIsAllUnchanged() {
		val req1 = Mockito.mock(Requirement::class.java) as Requirement<Any>
		val req2 = Mockito.mock(Requirement::class.java) as Requirement<Any>
		val req3 = Mockito.mock(Requirement::class.java) as Requirement<Any>
		val toTest = object : Examiner<Any> {

			override fun examine(state: Any): Examination<Any> {
				return StaticExamination(listOf(
						SimpleFulfillment(req1),
						SimpleFulfillment(req2),
						SimpleFulfillment(req3)), false)
			}
		}

		val examination = toTest.examine("")
		val examination2 = toTest.examineWithDelta("", examination)
		assert(examination2.changes.size == 3)
		examination2.changes.forEach {
			assert(it.type == Delta.Type.UNCHANGED)
		}
	}

	@Test
	fun testRemovalYieldsDeletionInExamination() {
		val req1 = Mockito.mock(Requirement::class.java) as Requirement<Any>
		val req2 = Mockito.mock(Requirement::class.java) as Requirement<Any>
		val req3 = Mockito.mock(Requirement::class.java) as Requirement<Any>
		val toTest = object : Examiner<Any> {

			override fun examine(state: Any): Examination<Any> {
				return if (state is String && state.isEmpty()) {
					StaticExamination(listOf(
							SimpleFulfillment(req1),
							SimpleFulfillment(req2),
							SimpleFulfillment(req3)), false)
				} else {
					StaticExamination(listOf(), false)
				}
			}
		}

		val examination = toTest.examine("")
		val examination2 = toTest.examineWithDelta("a", examination)
		assert( examination2.changes.size == 3)
		examination2.changes.forEach {
			assert(it.type == Delta.Type.REMOVAL)
		}
	}

	@Test
	fun testSecondExaminationAfterRemovalYieldsEmpty() {
		val req1 = Mockito.mock(Requirement::class.java) as Requirement<Any>
		val req2 = Mockito.mock(Requirement::class.java) as Requirement<Any>
		val req3 = Mockito.mock(Requirement::class.java) as Requirement<Any>
		val toTest = object : Examiner<Any> {

			override fun examine(state: Any): Examination<Any> {
				return if (state is String && state.isEmpty()) {
					StaticExamination(listOf(
							SimpleFulfillment(req1),
							SimpleFulfillment(req2),
							SimpleFulfillment(req3)), false)
				} else {
					StaticExamination(listOf(), false)
				}
			}
		}

		val examination = toTest.examine("")
		val examination2 = toTest.examineWithDelta("a", examination)
		val examination3 = toTest.examineWithDelta("a", examination2)

		assert(examination3.changes.isEmpty()) { "Had ${examination3.changes}"}
	}

}