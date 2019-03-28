package com.tendebit.dungeonmaster.core

data class Id(private val idString: CharSequence): CharSequence by idString {

	operator fun plus(otherCharSequence: CharSequence): Id {
		return Id("$idString$otherCharSequence")
	}

	operator fun plus(otherId: Id): Id {
		return Id("$idString${otherId.idString}")
	}

	operator fun plus(someValue: Any?): Id {
		return if (someValue == null) this else {
			this + someValue.toString()
		}
	}

	override fun equals(other: Any?): Boolean {
		return if (other is Id) idString == other.idString else idString == other
	}

	override fun hashCode(): Int {
		return idString.hashCode()
	}

}
