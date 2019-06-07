package com.tendebit.dungeonmaster.charactercreation3.characterclass

import android.os.Parcel
import android.os.Parcelable
import com.tendebit.dungeonmaster.charactercreation3.ItemState
import com.tendebit.dungeonmaster.charactercreation3.ItemStateUtils
import com.tendebit.dungeonmaster.charactercreation3.ListItemState
import com.tendebit.dungeonmaster.charactercreation3.Normal
import com.tendebit.dungeonmaster.charactercreation3.Selected
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class DndCharacterClassSelection : Parcelable {

	val options: MutableList<ItemState<out DndCharacterClass>>
	val selectedItem: Selected<out DndCharacterClass>?
		get() = options.find { it is Selected } as? Selected<out DndCharacterClass>
	private val selectedIndex: Int
		get() = options.indexOfFirst { it is Selected }

	private val indirectSelectionChanges = PublishSubject.create<ListItemState<DndCharacterClass>>()
	private val directSelectionChanges = PublishSubject.create<ListItemState<DndCharacterClass>>()

	/**
	 * An [Observable] which will emit whenever the [ItemState] for any item in this group changes
	 */
	val selectionChanges : Observable<ListItemState<DndCharacterClass>> = directSelectionChanges.mergeWith(indirectSelectionChanges)

	/**
	 * An [Observable] which will emit [ItemState] changes driven by selections coming from within this group
	 * @see select
	 * @see deselect
	 */
	internal val outboundSelectionChanges = directSelectionChanges as Observable<ListItemState<DndCharacterClass>>

	constructor(forExistingState: List<ItemState<out DndCharacterClass>>) {
		options = ArrayList(forExistingState)
	}

	constructor(parcel: Parcel) {
		options = ArrayList(ItemStateUtils.readItemStateListFromParcel(parcel))
	}

	fun deselect(index: Int) {
		val target = options.getOrNull(index)

		if (target !is Selected) {
			throw IllegalArgumentException("Unable to deselect $target at $index")
		}

		val normalState = Normal(target.item)
		options[index] = normalState
		directSelectionChanges.onNext(ListItemState(index, normalState))
	}

	fun select(index: Int) {
		logger.writeDebug("Selecting item at index $index")
		val previousSelectionIndex = selectedIndex
		if (previousSelectionIndex == index) {
			return
		}

		val target = options.getOrNull(index)

		if (target !is Normal) {
			throw IllegalArgumentException("Unable to select $target at $index")
		}

		val selectedState = Selected(target.item)
		options[index] = selectedState
		directSelectionChanges.onNext(ListItemState(index, selectedState))

		// Remove the previous selection, if any
		if (previousSelectionIndex > -1) {
			val currentSelection = options[previousSelectionIndex] as Selected
			val updatedState = Normal(currentSelection.item)
			options[previousSelectionIndex] = updatedState
			indirectSelectionChanges.onNext(ListItemState(previousSelectionIndex, updatedState))
		}
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
