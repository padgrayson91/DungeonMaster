package com.tendebit.dungeonmaster.charactercreation.feature.examiner

import com.tendebit.dungeonmaster.charactercreation.feature.fulfillment.Fulfillment

class Examination<StateType>(val fulfillmentList: List<Fulfillment<*, StateType>>, val shouldHalt: Boolean): List<Fulfillment<*, StateType>> by fulfillmentList