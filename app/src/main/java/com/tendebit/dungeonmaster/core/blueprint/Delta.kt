package com.tendebit.dungeonmaster.core.blueprint

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
}
