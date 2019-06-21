package com.tendebit.dungeonmaster.core.model

import kotlin.random.Random

class SingleDiceRoll(private val numSides: Int) : DiceRoll {

	override fun roll(): Int {
		return Random.nextInt(1, numSides + 1)
	}

}
