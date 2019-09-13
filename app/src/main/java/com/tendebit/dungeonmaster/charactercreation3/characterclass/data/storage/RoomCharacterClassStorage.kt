package com.tendebit.dungeonmaster.charactercreation3.characterclass.data.storage

import com.tendebit.dungeonmaster.charactercreation3.characterclass.DndCharacterClass
import com.tendebit.dungeonmaster.charactercreation3.characterclass.DndCharacterClassSelection
import com.tendebit.dungeonmaster.charactercreation3.characterclass.DndDetailedCharacterClass
import com.tendebit.dungeonmaster.charactercreation3.characterclass.logger
import com.tendebit.dungeonmaster.charactercreation3.proficiencycore.DndProficiencyGroup
import com.tendebit.dungeonmaster.charactercreation3.proficiencycore.DndProficiencySelection
import com.tendebit.dungeonmaster.charactercreation3.proficiencycore.data.storage.DndProficiencyStorage
import com.tendebit.dungeonmastercore.concurrency.Concurrency
import com.tendebit.dungeonmastercore.model.state.Normal
import com.tendebit.dungeonmastercore.model.state.Selected
import io.reactivex.Maybe
import io.reactivex.subjects.MaybeSubject
import java.util.UUID

/**
 * Implementation of [DndCharacterClassStorage] which stores information in a Room database
 * @param dao the DAO used to store and retrieve data
 * @param concurrency the [Concurrency] implementation used to run DB operations off the main thread
 * @param proficiencyStorage the [DndProficiencyStorage] implementation used to store proficiency information for classes
 */
class RoomCharacterClassStorage(private val dao: StoredClassDao, private val concurrency: Concurrency, private val proficiencyStorage: DndProficiencyStorage) : DndCharacterClassStorage {

	override fun storeSelection(selection: DndCharacterClassSelection, id: CharSequence?): CharSequence {
		val createdOrExistingId = id ?: UUID.randomUUID().toString()
		logger.writeDebug("Got request to store class selection with $createdOrExistingId")

		concurrency.runDiskOrNetwork({
			logger.writeDebug("Storing class selection to DB with ID $createdOrExistingId")
			dao.storeClassSelection(StoredCharacterClassSelection(createdOrExistingId.toString()))
			val classesToStore = selection.options
			for (state in classesToStore) {
				val stateAsInt = if (state is Selected) 1 else 0
				val dndClass = state.item ?: continue
				dao.storeClassInfo(StoredCharacterClass.fromDndCharacterClass(dndClass))
				dao.storeSelectionClassJoin(StoredSelectionCharacterClassJoin(dndClass.detailsUrl, createdOrExistingId.toString(), stateAsInt))
			}
		})
		return createdOrExistingId
	}

	override fun storeDetails(details: DndDetailedCharacterClass) {
		concurrency.runDiskOrNetwork({
			val optionsId = getStartingOptionsDbId(details.id)
			val nativeId = getNativeProficienciesDbId(details.id)
			proficiencyStorage.storeSelection(DndProficiencySelection(details.dndProficiencyOptions), optionsId)
			proficiencyStorage.storeSelection(DndProficiencySelection(listOf(DndProficiencyGroup(details.nativeProficiencies.map { Normal(it) }, details.nativeProficiencies.size))), nativeId)
			dao.storeClassInfo(StoredCharacterClass.fromDetailedCharacterClass(details))
		})
	}

	override fun findDetails(origin: DndCharacterClass): Maybe<DndDetailedCharacterClass> {
		val subject = MaybeSubject.create<DndDetailedCharacterClass>()
		logger.writeDebug("Got request to find details for $origin in DB")

		concurrency.runDiskOrNetwork({
			val storedInfo = dao.getClassInfo(origin.detailsUrl)
			if (storedInfo?.hitDie == null) {
				subject.onComplete()
				return@runDiskOrNetwork
			}
			val optionsId = getStartingOptionsDbId(origin.detailsUrl)
			val proficiencyOptions = proficiencyStorage.findSelectionById(optionsId).blockingGet()?.groupStates?.mapNotNull { it.item } ?: emptyList()
			val nativeId = getNativeProficienciesDbId(origin.detailsUrl)
			val nativeProficiencies = proficiencyStorage.findSelectionById(nativeId).blockingGet()?.groupStates?.mapNotNull { it.item }?.firstOrNull()?.options?.mapNotNull { it.item } ?: emptyList()
			logger.writeDebug("Found details in the DB for $origin")
			subject.onSuccess(DndDetailedCharacterClass(storedInfo.name, storedInfo.id, proficiencyOptions, storedInfo.hitDie!!, nativeProficiencies))
		})

		return subject
	}

	override fun findSelectionById(id: CharSequence): Maybe<DndCharacterClassSelection> {
		val subject = MaybeSubject.create<DndCharacterClassSelection>()
		logger.writeDebug("Got request to read class selection with $id")

		concurrency.runDiskOrNetwork({
			logger.writeDebug("Reading class selection from DB with ID $id")
			val classesForSelection = dao.getClassesForSelection(id.toString())
			if (classesForSelection.isEmpty()) {
				logger.writeDebug("Did not have any available classes matching $id")
				subject.onComplete()
				return@runDiskOrNetwork
			}
			val selectedClass = dao.getSelectedClassForSelection(id.toString())
			val dndClasses = classesForSelection.map { it.toDndCharacterClass() }
			val states = dndClasses.map { if (it.detailsUrl == selectedClass?.id) Selected(it) else Normal(it) }
			subject.onSuccess(DndCharacterClassSelection(states))
		})
		return subject
	}

	// FIXME: Should have a better way of constructing IDs
	private fun getStartingOptionsDbId(classId: CharSequence): String {
		return "$classId--starting_proficiencies"
	}

	private fun getNativeProficienciesDbId(classId: CharSequence): String {
		return "$classId--native_proficiencies"
	}

}
