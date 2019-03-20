package com.tendebit.dungeonmaster.core.blueprint.examination

import com.tendebit.dungeonmaster.core.blueprint.fulfillment.Fulfillment

class StaticExamination<StateType>(override val fulfillmentList: List<Fulfillment<*, StateType>>, override val shouldHalt: Boolean): List<Fulfillment<*, StateType>> by fulfillmentList, Examination<StateType>