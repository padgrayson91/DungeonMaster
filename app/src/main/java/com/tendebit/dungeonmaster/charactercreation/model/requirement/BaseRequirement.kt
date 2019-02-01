package com.tendebit.dungeonmaster.charactercreation.model.requirement

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

abstract class BaseRequirement<ItemType>(initialValue: ItemType?): Requirement<ItemType> {

	private val internalStatusChanges = PublishSubject.create<Requirement.Status>()
	private var internalStatus: Requirement.Status = Requirement.Status.NOT_SET
	override val item: ItemType? = initialValue

	final override val statusChanges = internalStatusChanges as Observable<Requirement.Status>
	final override var status: Requirement.Status
		private set(value) { internalStatus = value }
		get() { return if (internalStatus == Requirement.Status.NOT_SET) { statusForItem(item)} else { internalStatus } }

	final override fun update(item: ItemType) {
		if (isItemValid(item)) {
			onUpdate(item)
			status = statusForItem(this.item)
			internalStatusChanges.onNext(status)
		}
	}

	final override fun revoke() {
		onRevoke()
		status = Requirement.Status.NOT_FULFILLED
		internalStatusChanges.onNext(status)
	}

	protected open fun isItemValid(item: ItemType): Boolean = true

	protected abstract fun onUpdate(item: ItemType?)

	protected abstract fun statusForItem(item: ItemType?): Requirement.Status

	protected abstract fun onRevoke()

}
