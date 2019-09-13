package com.tendebit.dungeonmaster.charactercreation3.race.data.storage

import com.tendebit.dungeonmaster.charactercreation3.abilitycore.storage.DndAbilityStorage
import com.tendebit.dungeonmaster.charactercreation3.characterclass.logger
import com.tendebit.dungeonmaster.charactercreation3.proficiencycore.DndProficiencyGroup
import com.tendebit.dungeonmaster.charactercreation3.proficiencycore.DndProficiencySelection
import com.tendebit.dungeonmaster.charactercreation3.proficiencycore.data.storage.DndProficiencyStorage
import com.tendebit.dungeonmaster.charactercreation3.race.DndDetailedRace
import com.tendebit.dungeonmaster.charactercreation3.race.DndRace
import com.tendebit.dungeonmaster.charactercreation3.race.DndRaceSelection
import com.tendebit.dungeonmastercore.concurrency.Concurrency
import com.tendebit.dungeonmastercore.model.state.Normal
import com.tendebit.dungeonmastercore.model.state.Selected
import io.reactivex.Maybe
import io.reactivex.subjects.MaybeSubject
import java.util.UUID

class RoomRaceStorage(private val dao: StoredRaceDao, private val concurrency: Concurrency, private val abilityStorage: DndAbilityStorage, private val proficiencyStorage: DndProficiencyStorage) : DndRaceStorage {

	override fun storeSelection(selection: DndRaceSelection, id: CharSequence?): CharSequence {
		val createdOrExistingId = id ?: UUID.randomUUID().toString()
		logger.writeDebug("Got request to store race selection with $createdOrExistingId")

		concurrency.runDiskOrNetwork({
			logger.writeDebug("Storing race selection to DB with ID $createdOrExistingId")
			dao.storeRaceSelection(StoredRaceSelection(createdOrExistingId.toString()))
			val racesToStore = selection.options
			for (state in racesToStore) {
				val stateAsInt = if (state is Selected) 1 else 0
				val dndRace = state.item ?: continue
				dao.storeRaceInfo(StoredRace.fromDndRace(dndRace))
				dao.storeRaceSelectionJoin(StoredRaceSelectionJoin(dndRace.detailsUrl, createdOrExistingId.toString(), stateAsInt))
			}
		})
		return createdOrExistingId
	}

	override fun storeDetails(detailedRace: DndDetailedRace) {
		logger.writeDebug("Storing details for ${detailedRace.origin}")
		concurrency.runDiskOrNetwork({
			abilityStorage.storeAbilityBonuses(detailedRace.dndAbilityBonuses, detailedRace.origin.detailsUrl)
			val optionsId = getStartingOptionsDbId(detailedRace.origin.detailsUrl)
			val raceProficiencySelection = DndProficiencySelection(detailedRace.dndProficiencyOptions)
			proficiencyStorage.storeSelection(raceProficiencySelection, optionsId)
			val nativeId = getNativeProficienciesDbId(detailedRace.origin.detailsUrl)
			proficiencyStorage.storeSelection(DndProficiencySelection(listOf(DndProficiencyGroup(detailedRace.nativeProficiencies.map { Normal(it) }, detailedRace.nativeProficiencies.size))), nativeId)
			dao.storeRaceInfo(StoredRace.fromDetailedRace(detailedRace))
		})
	}

	override fun findDetails(origin: DndRace): Maybe<DndDetailedRace> {
		val subject = MaybeSubject.create<DndDetailedRace>()
		concurrency.runDiskOrNetwork({
			val abilities = abilityStorage.findAbilityBonuses(origin.detailsUrl).blockingGet()
			if (abilities == null) {
				subject.onComplete()
				return@runDiskOrNetwork
			}
			val optionsId = getStartingOptionsDbId(origin.detailsUrl)
			val proficiencyOptions = proficiencyStorage.findSelectionById(optionsId).blockingGet()?.groupStates?.mapNotNull { it.item } ?: emptyList()
			val nativeId = getNativeProficienciesDbId(origin.detailsUrl)
			val nativeProficiencies = proficiencyStorage.findSelectionById(nativeId).blockingGet()?.groupStates?.mapNotNull { it.item }?.firstOrNull()?.options?.mapNotNull { it.item } ?: emptyList()
			subject.onSuccess(DndDetailedRace(origin, abilities, proficiencyOptions, nativeProficiencies))
		})

		return subject
	}

	override fun findSelectionById(id: CharSequence): Maybe<DndRaceSelection> {
		val subject = MaybeSubject.create<DndRaceSelection>()
		logger.writeDebug("Got request to read race selection with $id")

		concurrency.runDiskOrNetwork({
			logger.writeDebug("Reading race selection from DB with ID $id")
			val racesForSelection = dao.getRacesForSelection(id.toString())
			if (racesForSelection.isEmpty()) {
				logger.writeDebug("Did not have any available races matching $id")
				subject.onComplete()
				return@runDiskOrNetwork
			}
			val selectedRace = dao.getSelectedRaceForSelection(id.toString())
			val dndRaces = racesForSelection.map { it.toDndRace() }
			val states = dndRaces.map { if (it.detailsUrl == selectedRace?.id) Selected(it) else Normal(it) }
			subject.onSuccess(DndRaceSelection(states))
		})
		return subject
	}

	// FIXME: Should have a better way of constructing IDs
	private fun getStartingOptionsDbId(raceId: CharSequence): String {
		return "$raceId--starting_proficiencies"
	}

	private fun getNativeProficienciesDbId(raceId: CharSequence): String {
		return "$raceId--native_proficiencies"
	}

}
