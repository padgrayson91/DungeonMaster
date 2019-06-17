package com.tendebit.dungeonmaster.charactercreation3.characterclass.data.storage

import com.tendebit.dungeonmaster.charactercreation3.characterclass.DndCharacterClassSelection
import com.tendebit.dungeonmaster.charactercreation3.characterclass.logger
import com.tendebit.dungeonmaster.core.concurrency.Concurrency
import com.tendebit.dungeonmaster.core.model.Normal
import com.tendebit.dungeonmaster.core.model.Selected
import io.reactivex.Maybe
import io.reactivex.subjects.MaybeSubject
import java.util.UUID

class RoomCharacterClassStorage(private val dao: StoredClassDao, private val concurrency: Concurrency) : DndCharacterClassStorage {

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

}
