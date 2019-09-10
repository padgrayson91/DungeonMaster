package com.tendebit.dungeonmastercore.model.state

/**
 * [Comparator] which compares the underlying item of two [ItemState] instances, ignoring
 * the state type
 */
class UnderlyingItemComparator<T>(private val comparator: Comparator<T>? = null) : Comparator<ItemState<out T>> {

	override fun compare(p0: ItemState<out T>?, p1: ItemState<out T>?): Int {
		val item1 = p0?.item
		val item2 = p1?.item

		if (item1 == null && item2 == null) {
			return 0
		}

		if (item1 == null) {
			return -1
		}

		if (comparator != null) {
			return comparator.compare(item1, item2)
		}

		if (item1 == item2) {
			return 0
		}

		return -1
	}

}
