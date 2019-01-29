package com.tendebit.dungeonmaster.charactercreation.model.requirement

import io.reactivex.Observable

interface Requirement<ItemType> {

	enum class Status {
		FULFILLED,
		NOT_FULFILLED,
		INVALID
	}

	val statusChanges: Observable<Status>
	var item: ItemType?
	var status: Status

	fun update(item: ItemType)

	fun revoke()

}
