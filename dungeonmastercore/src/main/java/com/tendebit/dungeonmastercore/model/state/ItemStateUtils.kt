package com.tendebit.dungeonmastercore.model.state

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

object ItemStateUtils {

	private const val INDICATE_PARCELABLE = 0
	private const val INDICATE_INT = 1
	private const val INDICATE_STRING = 2
	private const val INDICATE_LONG = 3
	private const val INDICATE_BOOL = 4
	private const val INDICATE_NULL = 99
	private const val INDICATE_SERIALIZABLE = 98

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
		when (val item = itemState.item) {
			is Parcelable -> {
				parcel.writeInt(INDICATE_PARCELABLE)
				parcel.writeSerializable(item.javaClass)
				parcel.writeParcelable(item, 0)
			}
			is Int -> {
				parcel.writeInt(INDICATE_INT)
				parcel.writeInt(item)
			}
			is String -> {
				parcel.writeInt(INDICATE_STRING)
				parcel.writeString(item)
			}
			is Long -> {
				parcel.writeInt(INDICATE_LONG)
				parcel.writeLong(item)
			}
			is Boolean -> {
				parcel.writeInt(INDICATE_BOOL)
				parcel.writeBoolean(item)
			}
			is Serializable -> {
				parcel.writeInt(INDICATE_SERIALIZABLE)
				parcel.writeSerializable(item as? Serializable)
			}
			else -> {
				parcel.writeInt(INDICATE_NULL)
			}
		}
	}

	@Suppress("UNCHECKED_CAST", "IMPLICIT_CAST_TO_ANY")
	fun <T> readItemStateFromParcel(parcel: Parcel): ItemState<out T> {
		val memberAsInt = parcel.readInt()
		val itemTypeAsInt = parcel.readInt()
		val item = when(itemTypeAsInt) {
			INDICATE_PARCELABLE -> {
				val type = parcel.readSerializable() as Class<T>
				parcel.readParcelable(type.classLoader) as? Parcelable
			}
			INDICATE_INT -> {
				parcel.readInt()
			}
			INDICATE_LONG -> {
				parcel.readLong()
			}
			INDICATE_STRING -> {
				parcel.readString()
			}
			INDICATE_BOOL -> {
				parcel.readBoolean()
			}
			INDICATE_SERIALIZABLE -> {
				parcel.readSerializable()
			}
			else -> null
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
