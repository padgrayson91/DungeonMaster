package com.tendebit.dungeonmaster.charactercreation.feature.requirement

open class ListRequirement<ItemType>(initialValue: List<ItemType>): BaseRequirement<List<ItemType>>(initialValue) {

	final override val item = ArrayList<ItemType>(initialValue)

	final override fun onUpdate(item: List<ItemType>?) {
		this.item.clear()
		if (item.isNullOrEmpty()) {
			return
		}

		this.item.addAll(item)
	}

	override fun onRevoke() = this.item.clear()

	final override fun statusForItem(item: List<ItemType>?): Requirement.Status {
		return if (item.isNullOrEmpty()) { Requirement.Status.NOT_FULFILLED } else { Requirement.Status.FULFILLED }
	}

}
