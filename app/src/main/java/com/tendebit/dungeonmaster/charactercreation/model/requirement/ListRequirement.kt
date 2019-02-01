package com.tendebit.dungeonmaster.charactercreation.model.requirement

open class ListRequirement<ItemType>: BaseRequirement<List<ItemType>>() {

	final override val item = ArrayList<ItemType>()

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
