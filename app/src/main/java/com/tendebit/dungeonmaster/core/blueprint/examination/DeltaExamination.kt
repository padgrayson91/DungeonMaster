package com.tendebit.dungeonmaster.core.blueprint.examination

import com.tendebit.dungeonmaster.core.blueprint.Delta
import com.tendebit.dungeonmaster.core.blueprint.fulfillment.Fulfillment

interface DeltaExamination<StateType> : Examination<StateType> {

	val changes: List<Delta<Fulfillment<*, StateType>>>

	class Impl<StateType> private constructor(override val changes: List<Delta<Fulfillment<*, StateType>>>,
											  override val shouldHalt: Boolean, override val fulfillmentList: List<Fulfillment<*, StateType>>):
			DeltaExamination<StateType>, List<Fulfillment<*, StateType>> by fulfillmentList {

		constructor(changes: List<Delta<Fulfillment<*, StateType>>>,
					shouldHalt: Boolean): this(changes, shouldHalt, changes.filter { it.type != Delta.Type.REMOVAL }.map { it.item!! })

		constructor(examination: Examination<StateType>): this(examination.map { Delta(Delta.Type.INSERTION, it) }, examination.shouldHalt)

	}

}
