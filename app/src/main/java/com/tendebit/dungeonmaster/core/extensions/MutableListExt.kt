package com.tendebit.dungeonmaster.core.extensions

fun <T> MutableList<T>.addOrInsert(index: Int, item: T) {
	if (index >= size) {
		add(item)
	} else {
		add(index, item)
	}
}