package com.tendebit.dungeonmastercore.model.dice

class MultipleDiceRoll(private val rolls: List<DiceRoll>) : DiceRoll {

	override fun roll(): Int {
		return rolls.map { it.roll() }.sum()
	}

	fun rollChooseLowest(count: Int) : Int {
		if (count > rolls.size) throw IllegalArgumentException("Cannot chose $count dice, only ${rolls.size} were provided")
		return rolls.map { it.roll() }.sorted().subList(0, count).sum()
	}

	fun rollChooseHighest(count: Int) : Int {
		if (count > rolls.size) throw IllegalArgumentException("Cannot chose $count dice, only ${rolls.size} were provided")
		return rolls.map { it.roll() }.sortedDescending().subList(0, count).sum()
	}

}
