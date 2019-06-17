package com.tendebit.dungeonmaster.charactercreation3.proficiency.data.storage

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "proficiency_selections")
class StoredProficiencySelection(@PrimaryKey val id: String)