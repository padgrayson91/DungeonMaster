package com.tendebit.dungeonmaster.charactercreation.model.requirement

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

abstract class BaseRequirement<ItemType>: Requirement<ItemType> {

	private val internalStatus = PublishSubject.create<Requirement.Status>()
	override val item: ItemType? = null

	override val statusChanges = internalStatus as Observable<Requirement.Status>
	final override var status = Requirement.Status.NOT_SET
		private set

	final override fun <T : Requirement<ItemType>> initialize(item: ItemType?): T {
		this.status = statusForItem(item)
		onUpdate(item)
		@Suppress("UNCHECKED_CAST")
		return this as T
	}

	final override fun update(item: ItemType) {
		if (isItemValid(item)) {
			onUpdate(item)
			status = statusForItem(this.item)
			internalStatus.onNext(status)
		}
	}

	final override fun revoke() {
		onRevoke()
		status = Requirement.Status.NOT_FULFILLED
		internalStatus.onNext(status)
	}

	protected open fun isItemValid(item: ItemType): Boolean = true

	protected abstract fun onUpdate(item: ItemType?)

	protected abstract fun statusForItem(item: ItemType?): Requirement.Status

	protected abstract fun onRevoke()

}
