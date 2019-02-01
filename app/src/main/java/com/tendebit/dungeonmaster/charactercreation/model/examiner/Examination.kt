package com.tendebit.dungeonmaster.charactercreation.model.examiner

import com.tendebit.dungeonmaster.charactercreation.model.fulfillment.Fulfillment

class Examination<StateType>(val fulfillmentList: List<Fulfillment<*, StateType>>, val shouldHalt: Boolean): List<Fulfillment<*, StateType>> by fulfillmentList