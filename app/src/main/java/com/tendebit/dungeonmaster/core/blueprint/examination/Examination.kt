package com.tendebit.dungeonmaster.core.blueprint.examination

import com.tendebit.dungeonmaster.core.blueprint.fulfillment.Fulfillment

class Examination<StateType>(val fulfillmentList: List<Fulfillment<*, StateType>>, val shouldHalt: Boolean): List<Fulfillment<*, StateType>> by fulfillmentList