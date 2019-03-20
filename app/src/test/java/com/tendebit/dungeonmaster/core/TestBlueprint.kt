package com.tendebit.dungeonmaster.core

import com.tendebit.dungeonmaster.core.blueprint.Blueprint
import com.tendebit.dungeonmaster.core.blueprint.Delta
import com.tendebit.dungeonmaster.core.blueprint.examination.Examination
import com.tendebit.dungeonmaster.core.blueprint.examination.Examiner
import com.tendebit.dungeonmaster.core.blueprint.examination.StaticExamination
import com.tendebit.dungeonmaster.core.blueprint.requirement.Requirement
import com.tendebit.dungeonmaster.testhelpers.SimpleFulfillment
import io.reactivex.observers.TestObserver
import io.reactivex.subjects.PublishSubject
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when` as whenever

@Suppress("UNCHECKED_CAST")
class TestBlueprint {

	class SimpleExaminer(val requirement: Requirement<Any>): Examiner<String> {
		override fun examine(state: String): Examination<String> {
			return StaticExamination(listOf(SimpleFulfillment(requirement)), false)
		}
	}

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

}