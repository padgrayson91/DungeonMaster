package com.tendebit.dungeonmaster.core.model

import kotlin.random.Random

class DiceRoll(private val numSides: Int) {

	fun roll(): Int {
		return Random.nextInt(1, numSides + 1)
	}

}
