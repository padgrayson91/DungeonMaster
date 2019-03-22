package com.tendebit.dungeonmaster.testhelpers

/**
 * Variation of [ValueRobot] which returns the same value for any test level
 */
class SimpleRobot<ItemType>(private val value: ItemType): ValueRobot<ItemType> {

	override fun getItem(forTestLevel: ValueRobot.TestingLevel): ItemType {
		return getItem()
	}

	private fun getItem(): ItemType = value

}
