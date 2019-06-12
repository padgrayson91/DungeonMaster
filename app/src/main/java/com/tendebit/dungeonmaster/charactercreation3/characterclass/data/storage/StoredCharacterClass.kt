package com.tendebit.dungeonmaster.charactercreation3.characterclass.data.storage

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "classes")
data class StoredCharacterClass(
		@PrimaryKey var id: String,
		@ColumnInfo(name = "class_name") var name: String)
