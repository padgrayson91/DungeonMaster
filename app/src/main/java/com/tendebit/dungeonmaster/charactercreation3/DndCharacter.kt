package com.tendebit.dungeonmaster.charactercreation3

import android.os.Parcel
import android.os.Parcelable
import com.tendebit.dungeonmaster.charactercreation3.proficiency.DndProficiencySelection

class DndCharacter() : Parcelable {

	var race: Any? = null // TODO
	var dndClass: Any? = null // TODO

	var proficiencies: DndProficiencySelection? = null


	constructor(parcel: Parcel): this() {
		proficiencies = parcel.readParcelable(DndProficiencySelection::class.java.classLoader)
	}

	override fun writeToParcel(dest: Parcel?, flags: Int) {
		dest?.let {
			it.writeParcelable(proficiencies, 0)
		}
	}

	override fun describeContents(): Int = 0

	companion object CREATOR : Parcelable.Creator<DndCharacter> {

		override fun createFromParcel(source: Parcel): DndCharacter {
			return DndCharacter(source)
		}

		override fun newArray(size: Int): Array<DndCharacter?> {
			return arrayOfNulls(size)
		}
	}

}
