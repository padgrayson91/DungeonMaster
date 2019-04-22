package com.tendebit.dungeonmaster.core.blueprint2

data class Delta<T>(val item: T, val change: Change) {

	enum class Change {
		UNCHANGED,
		UPDATED,
		DELETED,
		CREATED
	}

}
