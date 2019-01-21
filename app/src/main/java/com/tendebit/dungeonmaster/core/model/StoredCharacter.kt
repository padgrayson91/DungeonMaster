package com.tendebit.dungeonmaster.core.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tendebit.dungeonmaster.charactercreation.pages.classselection.model.CharacterClassInfo
import com.tendebit.dungeonmaster.charactercreation.pages.proficiencyselection.model.CharacterProficiencyDirectory
import com.tendebit.dungeonmaster.charactercreation.pages.raceselection.model.CharacterRaceDirectory

@Entity(tableName = "player_characters")
data class StoredCharacter(
    @PrimaryKey var id: String,
    @ColumnInfo(name = "character_name") var name: String,
    @ColumnInfo(name = "height_feet") var heightFeet: Int,
    @ColumnInfo(name = "height_inches") var heightInches: Int,
    @ColumnInfo(name = "weight") var weight: Int,
    @ColumnInfo(name = "proficiencies") var proficiencies : List<CharacterProficiencyDirectory>,
    @ColumnInfo(name = "race") var race: CharacterRaceDirectory,
    @ColumnInfo(name = "class") var characterClass: CharacterClassInfo)