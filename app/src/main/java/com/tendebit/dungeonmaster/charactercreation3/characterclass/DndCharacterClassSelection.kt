package com.tendebit.dungeonmaster.charactercreation3.characterclass

import android.os.Parcel
import android.os.Parcelable
import com.tendebit.dungeonmaster.charactercreation3.ItemState
import com.tendebit.dungeonmaster.charactercreation3.ItemStateUtils
import com.tendebit.dungeonmaster.core.model.BaseSelection

class DndCharacterClassSelection : BaseSelection<DndCharacterClass> {

	override val options: MutableList<ItemState<out DndCharacterClass>>

	constructor(forExistingState: List<ItemState<out DndCharacterClass>>) {
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

	companion object CREATOR : Parcelable.Creator<DndCharacterClassSelection> {

		override fun createFromParcel(source: Parcel): DndCharacterClassSelection {
			return DndCharacterClassSelection(source)
		}

		override fun newArray(size: Int): Array<DndCharacterClassSelection?> {
			return arrayOfNulls(size)
		}
	}

}
