package com.tendebit.dungeonmaster.charactercreation.model.requirement

open class SimpleRequirement<ItemType>: BaseRequirement<ItemType>() {

	final override var item: ItemType? = null

	override fun onUpdate(item: ItemType?) {
		this.item = item
	}

	override fun onRevoke() {
		this.item = null
	}

	override fun statusForItem(item: ItemType?): Requirement.Status {
		return if (item == null) { Requirement.Status.NOT_FULFILLED } else { Requirement.Status.FULFILLED }
	}

}
