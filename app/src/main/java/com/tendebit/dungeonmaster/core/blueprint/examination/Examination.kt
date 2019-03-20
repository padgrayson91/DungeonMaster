package com.tendebit.dungeonmaster.core.blueprint.examination

import com.tendebit.dungeonmaster.core.blueprint.fulfillment.Fulfillment

interface Examination<StateType>: List<Fulfillment<*, StateType>> {
	val fulfillmentList: List<Fulfillment<*, StateType>>
	val shouldHalt: Boolean
}