package com.tendebit.dungeonmaster.charactercreation3.proficiency.data.storage

import com.tendebit.dungeonmaster.charactercreation3.proficiency.DndProficiencyGroup
import com.tendebit.dungeonmaster.charactercreation3.proficiency.DndProficiencySelection
import com.tendebit.dungeonmaster.charactercreation3.proficiency.logger
import com.tendebit.dungeonmastercore.concurrency.Concurrency
import com.tendebit.dungeonmastercore.model.state.Normal
import com.tendebit.dungeonmastercore.model.state.Selected
import io.reactivex.Maybe
import io.reactivex.subjects.MaybeSubject
import java.util.UUID

class RoomProficiencyStorage(private val dao: StoredProficiencyDao, private val concurrency: Concurrency): DndProficiencyStorage {

	override fun storeSelection(selection: DndProficiencySelection, id: CharSequence?): CharSequence {
		val createdOrExistingId = id ?: UUID.randomUUID().toString()
		logger.writeDebug("Got request to store proficiency selection with $createdOrExistingId")

		concurrency.runDiskOrNetwork({
			logger.writeDebug("Storing proficiency selection to DB with ID $createdOrExistingId")
			dao.storeProficiencySelection(StoredProficiencySelection(createdOrExistingId.toString()))
			val proficiencyGroupsToStore = selection.groupStates
			for (indexedState in proficiencyGroupsToStore.withIndex()) {
				val groupIndex = indexedState.index
				val state = indexedState.value
				val dndGroup = state.item ?: continue
				val storedGroup = StoredProficiencyGroup.fromDndGroup(dndGroup)
				dao.storeProficiencyGroup(storedGroup)
				val proficienciesToStore = dndGroup.options
				for (profState in proficienciesToStore) {
					val dndProficiency = profState.item ?: continue
					val stateAsInt = if (profState is Selected) 1 else 0
					val storedProficiency = StoredProficiency.fromDndProficiency(dndProficiency)
					dao.storeProficiencyInfo(storedProficiency)
					dao.storeProficiencyGroupJoin(StoredProficiencyGroupJoin(storedProficiency.id, storedGroup.id))
					dao.storeProficiencySelectionToProficiencyJoin(StoredProficiencySelectionToProficiencyJoin(createdOrExistingId.toString(),
							storedGroup.id, dndProficiency.identifier, stateAsInt))
				}
				dao.storeProficiencySelectionJoin(StoredProficiencySelectionJoin(storedGroup.id, createdOrExistingId.toString(), groupIndex))
			}
		})
		return createdOrExistingId
	}

	override fun findSelectionById(id: CharSequence): Maybe<DndProficiencySelection> {
		val subject = MaybeSubject.create<DndProficiencySelection>()
		logger.writeDebug("Got request to read proficiency selection with $id")

		concurrency.runDiskOrNetwork({
			logger.writeDebug("Reading proficiency selection from DB with ID $id")
			val groupsForSelection = dao.getGroupsForSelection(id.toString())
			if (groupsForSelection.isEmpty()) {
				logger.writeDebug("Did not have any available proficiency groups matching $id")
				subject.onComplete()
				return@runDiskOrNetwork
			}

			val groups = arrayListOf<DndProficiencyGroup>()

			for (group in groupsForSelection) {
				logger.writeDebug("Reading proficiencies for group ${group.id}")
				val selectedProficiencies = dao.getSelectedProficienciesInGroup(group.id, id.toString()).map { it.toDndProficiency() }
				val dndProficiencies = dao.getProficienciesForGroup(group.id).map { it.toDndProficiency() }
				val states = dndProficiencies.map { if (selectedProficiencies.contains(it)) Selected(it) else Normal(it) }
				groups.add(DndProficiencyGroup(states, group.choiceCount))
			}

			val selection = DndProficiencySelection(groups)
			subject.onSuccess(selection)
		})
		return subject
	}

}
