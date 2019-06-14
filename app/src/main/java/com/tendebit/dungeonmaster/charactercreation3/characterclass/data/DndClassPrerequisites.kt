package com.tendebit.dungeonmaster.charactercreation3.characterclass.data

import com.tendebit.dungeonmaster.core.concurrency.Concurrency

interface DndClassPrerequisites {

	val concurrency: Concurrency
	val dataStore: DndCharacterClassDataStore

	class Impl(override val concurrency: Concurrency, override val dataStore: DndCharacterClassDataStore) : DndClassPrerequisites

}
