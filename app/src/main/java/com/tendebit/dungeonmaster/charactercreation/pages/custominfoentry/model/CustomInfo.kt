package com.tendebit.dungeonmaster.charactercreation.pages.custominfoentry.model

class CustomInfo {
    companion object {
        val MIN_HEIGHT_FEET = 1
        val MAX_HEIGHT_FEET = 9
        val MIN_HEIGHT_INCHES = 0
        val MAX_HEIGHT_INCHES = 11
    }

    var name: CharSequence? = null
    var heightFeet = (MAX_HEIGHT_FEET - MIN_HEIGHT_FEET) / 2
    var heightInches = 0
    var weight: Int? = null

    override fun equals(other: Any?): Boolean {
        return other is CustomInfo && other.name == name && other.heightFeet == heightFeet
                && other.heightInches == heightInches && other.weight == weight
    }

    override fun hashCode(): Int {
        var result = 17
        result = 31 * result + if (name != null) name!!.hashCode() else 0
        result = 31 * result + heightFeet
        result = 31 * result + heightInches
        result = 31 * result + if (weight != null) weight!! else 0
        return result
    }

    fun isComplete() : Boolean {
        return !name.isNullOrEmpty() && weight != null
    }
}