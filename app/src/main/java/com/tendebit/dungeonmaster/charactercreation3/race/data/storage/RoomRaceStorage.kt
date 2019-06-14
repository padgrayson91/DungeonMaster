package com.tendebit.dungeonmaster.charactercreation3.race.data.storage

import com.tendebit.dungeonmaster.charactercreation3.characterclass.logger
import com.tendebit.dungeonmaster.charactercreation3.race.DndRace
import com.tendebit.dungeonmaster.charactercreation3.race.DndRaceSelection
import com.tendebit.dungeonmaster.core.concurrency.Concurrency
import com.tendebit.dungeonmaster.core.model.Normal
import com.tendebit.dungeonmaster.core.model.Selected
import io.reactivex.Maybe
import io.reactivex.subjects.MaybeSubject
import java.util.UUID

class RoomRaceStorage(private val dao: StoredRaceDao, private val concurrency: Concurrency) : DndRaceStorage {

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
				dao.storeRaceInfo(StoredRace(dndRace.detailsUrl, dndRace.name))
				dao.storeRaceSelectionJoin(StoredRaceSelectionJoin(dndRace.detailsUrl, createdOrExistingId.toString(), stateAsInt))
			}
		})
		return createdOrExistingId
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
			val dndRaces = racesForSelection.map { DndRace(it.name, it.id) }
			val states = dndRaces.map { if (it.detailsUrl == selectedRace?.id) Selected(it) else Normal(it) }
			subject.onSuccess(DndRaceSelection(states))
		})
		return subject
	}

}
