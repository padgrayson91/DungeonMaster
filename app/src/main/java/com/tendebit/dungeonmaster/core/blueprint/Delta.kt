package com.tendebit.dungeonmaster.core.blueprint

import java.util.LinkedList

class Delta<T>(val type: Type, val item: T?) {

	enum class Type {
		INSERTION,
		REMOVAL,
		UPDATE,
		UNCHANGED
	}

	override fun toString(): String {
		return "$item was ${
			when(type) {
				Type.INSERTION -> "Inserted"
				Type.REMOVAL -> "Removed"
				Type.UPDATE -> "Updated"
				Type.UNCHANGED -> "Not Changed"
			}
		}"
	}

	companion object {

		fun <T> from(previous: List<T>, updated: List<T>): List<Delta<T>> {
			val deltas = LinkedList<Delta<T>>()
			var oldIndex = 0
			for (item in updated.withIndex()) {
				val updatedListElement = item.value
				val oldListElement = if (oldIndex >= previous.size) null else previous[oldIndex]
				when {
					oldIndex >= previous.size -> deltas.add(Delta(Delta.Type.INSERTION, updatedListElement))
					updatedListElement == oldListElement -> { deltas.add(Delta(Delta.Type.UNCHANGED, oldListElement)); oldIndex++ }
					else -> {
						// Look ahead in the old list to see if this item is there
						val offsetInOld = previous.subList(oldIndex, previous.size).indexOf(item.value)
						if (offsetInOld >= 0) {
							// This item was present at a later index, so intervening items must have been removed
							val start = oldIndex
							val end = oldIndex + offsetInOld
							for (i in start until end) {
								deltas.add(Delta(Delta.Type.REMOVAL, null))
								oldIndex++
							}
							deltas.add(Delta(Delta.Type.UNCHANGED, oldListElement))
							oldIndex++
						} else {
							// New item was not present in old list, but check if old item is present in new list later (meaning this item was inserted)
							val offsetInNew = updated.subList(item.index, updated.size).indexOf(oldListElement)
							if (offsetInNew >= 0) {
								// Old item is later in the new list
								deltas.add(Delta(Delta.Type.INSERTION, updatedListElement))
							} else {
								// Old item is not in the new list and new item is not in old list, item was replaced
								deltas.add(Delta(Delta.Type.UPDATE, updatedListElement))
								oldIndex++
							}

						}
					}
				}
			}

			if (oldIndex < previous.size) {
				val removals = Array(previous.size - oldIndex) { Delta(Delta.Type.REMOVAL, previous[oldIndex + it]) }
				deltas.addAll(removals)
			}

			return deltas
		}

	}

}
