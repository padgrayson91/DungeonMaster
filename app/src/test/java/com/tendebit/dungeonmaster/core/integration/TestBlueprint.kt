package com.tendebit.dungeonmaster.core.integration

import com.tendebit.dungeonmaster.core.blueprint.Blueprint
import com.tendebit.dungeonmaster.core.blueprint.Delta
import com.tendebit.dungeonmaster.core.blueprint.examination.Examination
import com.tendebit.dungeonmaster.core.blueprint.examination.StaticExamination
import com.tendebit.dungeonmaster.core.blueprint.requirement.Requirement
import com.tendebit.dungeonmaster.testhelpers.SimpleExaminer
import com.tendebit.dungeonmaster.testhelpers.SimpleFulfillment
import io.reactivex.observers.TestObserver
import io.reactivex.subjects.PublishSubject
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.anyList
import org.mockito.Mockito.argThat
import org.mockito.Mockito.times
import java.lang.RuntimeException
import org.mockito.Mockito.`when` as whenever

@Suppress("UNCHECKED_CAST")
class TestBlueprint {

	@Test
	fun testMultipleExaminersCombineCorrectly() {
		val publishSubject = PublishSubject.create<Requirement.Status>()
		val req1 = Mockito.mock(Requirement::class.java) as Requirement<Any>
		whenever(req1.statusChanges).thenReturn(publishSubject)
		val req2 = Mockito.mock(Requirement::class.java) as Requirement<Any>
		whenever(req2.statusChanges).thenReturn(publishSubject)
		val req3 = Mockito.mock(Requirement::class.java) as Requirement<Any>
		whenever(req3.statusChanges).thenReturn(publishSubject)

		val ex1 = SimpleExaminer(req1)
		val ex2 = SimpleExaminer(req2)
		val ex3 = SimpleExaminer(req3)

		val testObserver = TestObserver<List<Delta<Requirement<*>>>>()
		val toTest = Blueprint(listOf(ex1, ex2, ex3), "")
		toTest.requirements.subscribe(testObserver)

		testObserver.assertValueCount(1)
		testObserver.assertValue { it.size == 3 }
	}

	@Test
	fun testUpdatingRequirementStatusCausesRequirementsToEmitAgain() {
		val ps1 = PublishSubject.create<Requirement.Status>()
		val ps2 = PublishSubject.create<Requirement.Status>()
		val ps3 = PublishSubject.create<Requirement.Status>()

		val req1 = Mockito.mock(Requirement::class.java) as Requirement<Any>
		whenever(req1.statusChanges).thenReturn(ps1)
		val req2 = Mockito.mock(Requirement::class.java) as Requirement<Any>
		whenever(req2.statusChanges).thenReturn(ps2)
		val req3 = Mockito.mock(Requirement::class.java) as Requirement<Any>
		whenever(req3.statusChanges).thenReturn(ps3)

		val ex1 = SimpleExaminer(req1)
		val ex2 = SimpleExaminer(req2)
		val ex3 = SimpleExaminer(req3)

		val testObserver = TestObserver<List<Delta<Requirement<*>>>>()
		val toTest = Blueprint(listOf(ex1, ex2, ex3), "")
		toTest.requirements.subscribe(testObserver)

		ps1.onNext(Requirement.Status.FULFILLED)
		testObserver.assertValueCount(2)
	}

	@Test
	fun testUpdatingLaterRequirementCausesEarlierRequirementToBeUnchanged() {
		val ps1 = PublishSubject.create<Requirement.Status>()
		val ps2 = PublishSubject.create<Requirement.Status>()
		val ps3 = PublishSubject.create<Requirement.Status>()

		val req1 = Mockito.mock(Requirement::class.java) as Requirement<Any>
		whenever(req1.statusChanges).thenReturn(ps1)
		val req1Alt = Mockito.mock(Requirement::class.java) as Requirement<Any>
		whenever(req1Alt.statusChanges).thenReturn(ps1)
		val req2 = Mockito.mock(Requirement::class.java) as Requirement<Any>
		whenever(req2.statusChanges).thenReturn(ps2)
		val req3 = Mockito.mock(Requirement::class.java) as Requirement<Any>
		whenever(req3.statusChanges).thenReturn(ps3)

		val ex1 = Mockito.mock(SimpleExaminer::class.java)
		whenever(ex1.requirement).thenReturn(req1)
		whenever(ex1.examineWithDelta(ArgumentMatchers.anyString(), argThat { it == it })).thenCallRealMethod()
		whenever(ex1.examine(ArgumentMatchers.anyString()))
				.thenReturn(StaticExamination(listOf(SimpleFulfillment(req1)), false))
				.thenReturn(StaticExamination(listOf(SimpleFulfillment(req1Alt)), false))
		val ex2 = SimpleExaminer(req2)
		val ex3 = SimpleExaminer(req3)

		val testObserver = TestObserver<List<Delta<Requirement<*>>>>()
		val toTest = Blueprint(listOf(ex1, ex2, ex3), "")
		toTest.requirements.subscribe(testObserver)

		ps2.onNext(Requirement.Status.FULFILLED)
		testObserver.assertValueCount(2)
		testObserver.assertValueAt(1) { it[0].type == Delta.Type.UNCHANGED && it[0].item == req1 }
	}

	@Test
	fun testHaltingExaminerRespected() {
		val ps1 = PublishSubject.create<Requirement.Status>()
		val ps2 = PublishSubject.create<Requirement.Status>()
		val ps3 = PublishSubject.create<Requirement.Status>()

		val req1 = Mockito.mock(Requirement::class.java) as Requirement<Any>
		whenever(req1.statusChanges).thenReturn(ps1)
		val req2 = Mockito.mock(Requirement::class.java) as Requirement<Any>
		whenever(req2.statusChanges).thenReturn(ps2)
		val req3 = Mockito.mock(Requirement::class.java) as Requirement<Any>
		whenever(req3.statusChanges).thenReturn(ps3)

		val ex1 = SimpleExaminer(req1)
		val ex2 = SimpleExaminer(req2, true)
		val ex3 = SimpleExaminer(req3)

		val testObserver = TestObserver<List<Delta<Requirement<*>>>>()
		val toTest = Blueprint(listOf(ex1, ex2, ex3), "")
		toTest.requirements.subscribe(testObserver)

		testObserver.assertValueCount(1)
		testObserver.assertValueAt(0) { it.size == 2 }
	}

	@Test
	fun testChainExaminationCompletes() {
		val ps1 = PublishSubject.create<Requirement.Status>()
		val ps2 = PublishSubject.create<Requirement.Status>()
		val ps3 = PublishSubject.create<Requirement.Status>()

		val req1 = Mockito.mock(Requirement::class.java) as Requirement<Any>
		whenever(req1.statusChanges).thenReturn(ps1)
		whenever(req1.item).thenReturn("Hello")
		val req2 = Mockito.mock(Requirement::class.java) as Requirement<Any>
		whenever(req2.statusChanges).thenReturn(ps2)
		whenever(req2.item).thenReturn("World")
		val req3 = Mockito.mock(Requirement::class.java) as Requirement<Any>
		whenever(req3.statusChanges).thenReturn(ps3)
		whenever(req3.item).thenReturn("!")

		val ex1 = Mockito.mock(SimpleExaminer::class.java)
		whenever(ex1.requirement).thenReturn(req1)
		whenever(ex1.examineWithDelta(ArgumentMatchers.anyList<String>(), argThat { it == it })).thenCallRealMethod()
		whenever(ex1.examine(ArgumentMatchers.anyList<String>()))
				.thenReturn(StaticExamination(listOf(SimpleFulfillment(req1)), true))
				.thenReturn(StaticExamination(listOf(SimpleFulfillment(req1)), false))
				.thenThrow(RuntimeException("Examiner 1 called too many times!"))
		val ex2 = Mockito.mock(SimpleExaminer::class.java)
		whenever(ex2.requirement).thenReturn(req2)
		whenever(ex2.examineWithDelta(ArgumentMatchers.anyList<String>(), argThat { it == it })).thenCallRealMethod()
		whenever(ex2.examine(ArgumentMatchers.anyList<String>()))
				.thenReturn(StaticExamination(listOf(SimpleFulfillment(req2)), true))
				.thenReturn(StaticExamination(listOf(SimpleFulfillment(req2)), false))
				.thenThrow(RuntimeException("Examiner 2 called too many times"))
		val ex3 = Mockito.mock(SimpleExaminer::class.java)
		whenever(ex3.requirement).thenReturn(req3)
		whenever(ex3.examineWithDelta(ArgumentMatchers.anyList<String>(), argThat { it == it })).thenCallRealMethod()
		whenever(ex3.examine(ArgumentMatchers.anyList<String>()))
				.thenReturn(StaticExamination(listOf(SimpleFulfillment(req3)), false))
				.thenReturn(StaticExamination(listOf(SimpleFulfillment(req3)), false))
				.thenThrow(RuntimeException("Examiner 3 called too many times!"))


		val testObserver = TestObserver<List<Delta<Requirement<*>>>>()
		val toTest = Blueprint(listOf(ex1, ex2, ex3), ArrayList<String>())
		toTest.requirements.subscribe(testObserver)

		ps1.onNext(Requirement.Status.FULFILLED)

		assert(toTest.state == listOf("Hello")) { "State was ${toTest.state}"}
		Mockito.verify(ex1, times(2)).examineWithDelta(ArgumentMatchers.anyList<String>(), ArgumentMatchers.any())
		Mockito.verify(ex2, times(1)).examineWithDelta(listOf("Hello"), null)
		Mockito.verify(ex3, times(0)).examineWithDelta(ArgumentMatchers.anyList<String>(), argThat { it == it })
		assert(testObserver.values().last().find { it.item == req2 } != null)
		assert(testObserver.values().last().size == 2) { "Had ${testObserver.values().last()}"}

		ps2.onNext(Requirement.Status.FULFILLED)

		assert(toTest.state == listOf("Hello", "World")) { "State was ${toTest.state}"}
		Mockito.verify(ex1, times(2)).examineWithDelta(ArgumentMatchers.anyList<String>(), ArgumentMatchers.any())
		Mockito.verify(ex2, times(2)).examineWithDelta(ArgumentMatchers.anyList<String>(), argThat { it == it })
		Mockito.verify(ex3, times(1)).examineWithDelta(listOf("Hello", "World"), null)
		assert(testObserver.values().last().find { it.item == req3 } != null)
		assert(testObserver.values().last().size == 3)

		ps3.onNext(Requirement.Status.FULFILLED)

		assert(toTest.state == listOf("Hello", "World", "!")) { "State was ${toTest.state}"}
	}

}