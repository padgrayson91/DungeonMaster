package com.tendebit.dungeonmaster.charactercreation3

import android.os.Parcel
import android.os.Parcelable
import com.tendebit.dungeonmaster.charactercreation3.characterclass.DndClasses
import com.tendebit.dungeonmaster.charactercreation3.proficiency.DndProficiencies
import com.tendebit.dungeonmaster.charactercreation3.race.DndRaces

class CharacterCreation : Parcelable {

	val character: DndCharacter
	val races: DndRaces
	val classes: DndClasses
	val proficiencies: DndProficiencies

	constructor() {
		character = DndCharacter()
		races = DndRaces()
		classes = DndClasses()
		proficiencies = DndProficiencies()
	}

	constructor(parcel: Parcel) {
		character = parcel.readParcelable(DndCharacter::class.java.classLoader)!!
		races = parcel.readParcelable(DndRaces::class.java.classLoader)!!
		classes = parcel.readParcelable(DndClasses::class.java.classLoader)!!
		proficiencies = parcel.readParcelable(DndProficiencies::class.java.classLoader)!!
	}

	override fun writeToParcel(dest: Parcel?, flags: Int) {
		dest?.let {
			it.writeParcelable(character, 0)
			it.writeParcelable(races, 0)
			it.writeParcelable(classes, 0)
			it.writeParcelable(proficiencies, 0)
		}
	}

	override fun describeContents(): Int = 0

	companion object CREATOR : Parcelable.Creator<CharacterCreation> {

		override fun createFromParcel(source: Parcel): CharacterCreation {
			return CharacterCreation(source)
		}

		override fun newArray(size: Int): Array<CharacterCreation?> {
			return arrayOfNulls(size)
		}
	}

}
