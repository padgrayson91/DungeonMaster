package com.tendebit.dungeonmaster.charactercreation.model.requirement

abstract class SimpleRequirement<ItemType>(initialValue: ItemType?): BaseRequirement<ItemType>() {

	override var item = initialValue

	override fun update(item: ItemType) {
		this.item = item
		status = Requirement.Status.FULFILLED
		internalStatus.onNext(status)
	}

	override fun revoke() {
		this.item = null
		status = Requirement.Status.NOT_FULFILLED
		internalStatus.onNext(status)
	}

}
