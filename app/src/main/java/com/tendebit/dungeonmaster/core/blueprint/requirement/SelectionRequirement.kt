package com.tendebit.dungeonmaster.core.blueprint.requirement

open class SelectionRequirement<ItemType>(val choices: List<ItemType>, initialValue: ItemType?): SimpleRequirement<ItemType>(initialValue) {

	override fun isItemValid(item: ItemType): Boolean {
		return super.isItemValid(item) && choices.contains(item)
	}

}
