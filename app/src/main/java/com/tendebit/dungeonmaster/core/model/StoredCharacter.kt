package com.tendebit.dungeonmaster.core.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tendebit.dungeonmaster.charactercreation.pages.classselection.model.CharacterClassInfo
import com.tendebit.dungeonmaster.charactercreation.pages.proficiencyselection.model.CharacterProficiencyDirectory
import com.tendebit.dungeonmaster.charactercreation.pages.raceselection.model.CharacterRaceDirectory

@Entity(tableName = "player_characters")
class StoredCharacter(
    @PrimaryKey var id: String,
    @ColumnInfo(name = "character_name") var name: String,
    @ColumnInfo(name = "height_feet") var heightFeet: Int,
    @ColumnInfo(name = "height_inches") var heightInches: Int,
    @ColumnInfo(name = "weight") var weight: Int,
    @ColumnInfo(name = "proficiencies") var proficiencies : List<CharacterProficiencyDirectory>,
    @ColumnInfo(name = "race") var race: CharacterRaceDirectory,
    @ColumnInfo(name = "class") var characterClass: CharacterClassInfo) {

    override fun equals(other: Any?): Boolean {
        return other is StoredCharacter &&
                other.id == id
    }

    override fun hashCode(): Int {
        var result = 21
        result = 12 * result + id.hashCode()
        result = 12 * result + name.hashCode()
        result = 12 * result + heightFeet
        result = 12 * result + weight
        result = 12 * result + proficiencies.hashCode()
        result = 12 * result + race.hashCode()
        result = 12 * result + characterClass.hashCode()
        return result
    }
}