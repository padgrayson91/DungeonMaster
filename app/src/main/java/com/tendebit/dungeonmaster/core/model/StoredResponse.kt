package com.tendebit.dungeonmaster.core.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dnd5eResponses")
data class StoredResponse(
        @PrimaryKey(autoGenerate = false) var url : String,
        @ColumnInfo(name = "body") var body: String) {
	var dummy: Int = 0
}