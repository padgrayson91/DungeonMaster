package com.tendebit.dungeonmaster.charactercreation.model.requirement

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

abstract class BaseRequirement<ItemType>: Requirement<ItemType> {

	protected val internalStatus = PublishSubject.create<Requirement.Status>()

	override val statusChanges = internalStatus as Observable<Requirement.Status>
	override val item: ItemType? = null
	override var status = Requirement.Status.NOT_SET

}
