package com.tendebit.dungeonmaster.charactercreation3.proficiency.data.network

import com.google.gson.Gson
import com.tendebit.dungeonmaster.charactercreation3.characterclass.DndCharacterClass
import com.tendebit.dungeonmaster.charactercreation3.proficiencycore.logger
import com.tendebit.dungeonmaster.charactercreation3.race.DndRace
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

interface DndProficiencyApiConnection {

	suspend fun getProficiencies(sourceClass: DndCharacterClass): List<DndProficiencyGroupDirectory>
	suspend fun getProficiencies(sourceRace: DndRace): List<DndProficiencyGroupDirectory>

	class Impl : DndProficiencyApiConnection {

		private val client = OkHttpClient()
		private val gson = Gson()

		override suspend fun getProficiencies(sourceClass: DndCharacterClass): List<DndProficiencyGroupDirectory> {
			// FIXME: Should check if details for this class have been loaded elsewhere

			val request = Request.Builder()
					.url(sourceClass.detailsUrl)
					.build()

			val response = client.newCall(request).execute()?.body()?.string() ?: return emptyList()
			val classDetail = gson.fromJson(response, DndCharacterClassDetail::class.java)
			return classDetail.proficiencyChoices
		}

		override suspend fun getProficiencies(sourceRace: DndRace): List<DndProficiencyGroupDirectory> {
			val request = Request.Builder()
					.url(sourceRace.detailsUrl)
					.build()

			val response = client.newCall(request).execute()?.body()?.string() ?: return emptyList()
			logger.writeDebug("Got ${JSONObject(response).toString(3)}")
			val raceDetail = gson.fromJson(response, DndRaceDetails::class.java)
			val raceProficiencyGroup = raceDetail.proficiencyChoices
			return if (raceProficiencyGroup == null) emptyList() else listOf(raceProficiencyGroup)
		}
	}

}
