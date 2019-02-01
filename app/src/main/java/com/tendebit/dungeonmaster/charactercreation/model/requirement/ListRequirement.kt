package com.tendebit.dungeonmaster.charactercreation.model.requirement

abstract class ListRequirement<ItemType>(initialValue: List<ItemType>): BaseRequirement<List<ItemType>>() {

	final override val item: List<ItemType> = ArrayList(initialValue)
	private val internalItem = item as MutableList<ItemType>

	override fun update(item: List<ItemType>) {
		if (item.isEmpty()) {
			revoke()
			return
		}

		internalItem.clear()
		internalItem.addAll(item)
		status = Requirement.Status.FULFILLED
		internalStatus.onNext(status)
	}

	override fun revoke() {
		internalItem.clear()
		status = Requirement.Status.NOT_FULFILLED
		internalStatus.onNext(status)
	}

}
