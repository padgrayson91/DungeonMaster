package com.tendebit.dungeonmaster.testhelpers

interface ValueRobot<ItemType> {

	enum class TestingLevel {
		SIMPLE,
		STANDARD,
		STANDARD_EDGE,
		EXTREME_EDGE
	}

	fun getItem(forTestLevel: TestingLevel): ItemType

}
