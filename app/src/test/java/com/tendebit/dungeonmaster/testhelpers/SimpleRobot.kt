package com.tendebit.dungeonmaster.testhelpers

/**
 * Variation of [ValueRobot] which returns the same value for any test level
 */
class SimpleRobot<ItemType>(val value: ItemType): ValueRobot<ItemType> {

	override fun getItem(forTestLevel: ValueRobot.TestingLevel): ItemType {
		return getItem()
	}

	fun getItem(): ItemType = value

}
