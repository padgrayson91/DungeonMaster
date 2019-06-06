package com.tendebit.dungeonmaster.charactercreation3

import android.os.Parcel
import android.os.Parcelable
import com.tendebit.dungeonmaster.charactercreation3.characterclass.DndCharacterClassSelection
import com.tendebit.dungeonmaster.charactercreation3.proficiency.DndProficiencySelection
import com.tendebit.dungeonmaster.charactercreation3.race.DndRaceSelection

class DndCharacter() : Parcelable {

	var race: DndRaceSelection? = null
	var dndClass: DndCharacterClassSelection? = null

	var proficiencies: DndProficiencySelection? = null


	constructor(parcel: Parcel): this() {
		dndClass = parcel.readParcelable(DndCharacterClassSelection::class.java.classLoader)
		race = parcel.readParcelable(DndRaceSelection::class.java.classLoader)
		proficiencies = parcel.readParcelable(DndProficiencySelection::class.java.classLoader)
	}

	override fun writeToParcel(dest: Parcel?, flags: Int) {
		dest?.let {
			it.writeParcelable(dndClass, 0)
			it.writeParcelable(race, 0)
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
