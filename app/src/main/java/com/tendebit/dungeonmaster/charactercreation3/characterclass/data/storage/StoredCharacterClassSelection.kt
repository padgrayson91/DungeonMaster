package com.tendebit.dungeonmaster.charactercreation3.characterclass.data.storage

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "class_selection")
data class StoredCharacterClassSelection(@PrimaryKey val id: String)