package com.tendebit.dungeonmaster.charactercreation3

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

object ItemStateUtils {

	fun <T> writeItemStateToParcel(itemState: ItemState<T>, parcel: Parcel) {
		val memberAsInt = when(itemState) {
			Loading -> 0
			Undefined -> 1
			Removed -> 2
			is Selected -> 3
			is Disabled -> 4
			is Locked -> 5
			is Normal -> 6
			is Completed -> 7
		}
		parcel.writeInt(memberAsInt)
		val item = itemState.item
		if (item is Parcelable) {
			parcel.writeInt(0)
			parcel.writeSerializable(item.javaClass)
			parcel.writeParcelable(item, 0)
		} else {
			parcel.writeInt(1)
			parcel.writeSerializable(item as? Serializable)
		}
	}

	@Suppress("UNCHECKED_CAST", "IMPLICIT_CAST_TO_ANY")
	fun <T> readItemStateFromParcel(parcel: Parcel): ItemState<out T> {
		val memberAsInt = parcel.readInt()
		val isItemParcelable = parcel.readInt() == 0
		val item = if (isItemParcelable) {
			val type = parcel.readSerializable() as Class<T>
			parcel.readParcelable(type.classLoader) as Parcelable
		} else {
			parcel.readSerializable()
		} as? T

		return when(memberAsInt) {
			0 -> Loading
			1 -> Undefined
			2 -> Removed
			3 -> Selected(item!!)
			4 -> Disabled(item!!)
			5 -> Locked(item!!)
			6 -> Normal(item!!)
			7 -> Completed(item!!)
			else -> Undefined
		}
	}

	fun <T> writeItemStateListToParcel(list: List<ItemState<out T>>, parcel: Parcel) {
		parcel.writeInt(list.size)
		for (item in list) {
			writeItemStateToParcel(item, parcel)
		}
	}

	fun <T> readItemStateListFromParcel(parcel: Parcel): List<ItemState<out T>> {
		val result = ArrayList<ItemState<out T>>()
		val itemCount = parcel.readInt()
		for (i in 0 until itemCount) {
			result.add(readItemStateFromParcel(parcel))
		}
		return result
	}

}
