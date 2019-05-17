package com.tendebit.dungeonmaster.charactercreation3.characterclass

import android.os.Parcel
import android.os.Parcelable
import com.tendebit.dungeonmaster.charactercreation3.Completed
import com.tendebit.dungeonmaster.charactercreation3.ItemState
import com.tendebit.dungeonmaster.charactercreation3.ItemStateUtils
import com.tendebit.dungeonmaster.charactercreation3.Loading
import com.tendebit.dungeonmaster.charactercreation3.Normal
import com.tendebit.dungeonmaster.charactercreation3.characterclass.data.DndCharacterClassDataStoreImpl
import com.tendebit.dungeonmaster.charactercreation3.characterclass.data.network.DndCharacterClassApiConnection
import io.reactivex.subjects.PublishSubject

class DndClasses : DndCharacterClassProvider, Parcelable {

	override var state: ItemState<out DndCharacterClassSelection> = Loading

	override val internalStateChanges = PublishSubject.create<ItemState<out DndCharacterClassSelection>>()
	override val externalStateChanges = PublishSubject.create<ItemState<out DndCharacterClassSelection>>()

	private val dataStore = DndCharacterClassDataStoreImpl(DndCharacterClassApiConnection.Impl())

	constructor()

	private constructor(parcel: Parcel) {
		state = ItemStateUtils.readItemStateFromParcel(parcel)
		@Suppress("UNCHECKED_CAST")
		val classesFromState = state.item?.options?.map { it.item }?.filter { it != null } as? List<DndCharacterClass>
		if (classesFromState != null) {
			dataStore.restoreCharacterClassList(classesFromState)
		}
	}

	override suspend fun start() {
		doLoadAvailableClasses()
	}

	override fun refreshClassState() {
		val newState = when(val oldState = state) {
			is Completed -> {
				if (oldState.item.selectedItem != null) {
					oldState
				} else {
					Normal(oldState.item)
				}
			}
			is Normal -> {
				if (oldState.item.selectedItem != null) {
					Completed(oldState.item)
				} else {
					oldState
				}
			}
			else -> oldState
		}
		state = newState
		internalStateChanges.onNext(newState)
	}

	private suspend fun doLoadAvailableClasses() {
		val characterClasses = dataStore.getCharacterClassList()
		state = Normal(DndCharacterClassSelection(characterClasses.map { Normal(it) }))
		externalStateChanges.onNext(state)
	}

	override fun writeToParcel(dest: Parcel?, flags: Int) {
		dest?.let {
			ItemStateUtils.writeItemStateToParcel(state, it)
		}
	}

	override fun describeContents(): Int = 0

	companion object CREATOR : Parcelable.Creator<DndClasses> {

		override fun createFromParcel(source: Parcel): DndClasses {
			return DndClasses(source)
		}

		override fun newArray(size: Int): Array<DndClasses?> {
			return arrayOfNulls(size)
		}
	}

}
