package com.tendebit.dungeonmaster.core.model

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tendebit.dungeonmaster.charactercreation.pages.classselection.model.CharacterClassInfo
import com.tendebit.dungeonmaster.charactercreation.pages.proficiencyselection.model.CharacterProficiencyDirectory
import com.tendebit.dungeonmaster.charactercreation.pages.raceselection.model.CharacterRaceDirectory


class CoreTypeConverters {

    val gson = Gson()

    @TypeConverter
    fun proficiencyListToString(input: List<CharacterProficiencyDirectory>) : String {
        return gson.toJson(input)
    }

    @TypeConverter
    fun stringToListOfProficiency(input: String?) : List<CharacterProficiencyDirectory> {
        if (input == null) return ArrayList()

        val listType = object : TypeToken<List<CharacterProficiencyDirectory>>() {}.type
        return gson.fromJson(input, listType)
    }

    @TypeConverter
    fun raceInfoToString(input: CharacterRaceDirectory) : String {
        return gson.toJson(input)
    }

    @TypeConverter
    fun stringToRaceInfo(input: String) : CharacterRaceDirectory {
        return gson.fromJson(input, CharacterRaceDirectory::class.java)
    }

    @TypeConverter
    fun classInfoToString(input: CharacterClassInfo) : String {
        return gson.toJson(input)
    }

    @TypeConverter
    fun stringToClassInfo(input: String) : CharacterClassInfo {
        return gson.fromJson(input, CharacterClassInfo::class.java)
    }


}