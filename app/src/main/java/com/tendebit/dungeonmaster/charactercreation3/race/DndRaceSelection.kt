package com.tendebit.dungeonmaster.charactercreation3.race

import android.os.Parcel
import android.os.Parcelable
import com.tendebit.dungeonmaster.charactercreation3.ItemState
import com.tendebit.dungeonmaster.charactercreation3.ItemStateUtils
import com.tendebit.dungeonmaster.core.model.BaseSelection

class DndRaceSelection : BaseSelection<DndRace> {

	override val options: MutableList<ItemState<out DndRace>>

	constructor(forExistingState: List<ItemState<out DndRace>>) {
		options = ArrayList(forExistingState)
	}

	constructor(parcel: Parcel) {
		options = ArrayList(ItemStateUtils.readItemStateListFromParcel(parcel))
	}

	override fun writeToParcel(dest: Parcel?, flags: Int) {
		dest?.let {
			ItemStateUtils.writeItemStateListToParcel(options, it)
		}
	}

	override fun describeContents(): Int = 0

	companion object CREATOR : Parcelable.Creator<DndRaceSelection> {

		override fun createFromParcel(source: Parcel): DndRaceSelection {
			return DndRaceSelection(source)
		}

		override fun newArray(size: Int): Array<DndRaceSelection?> {
			return arrayOfNulls(size)
		}
	}

}
