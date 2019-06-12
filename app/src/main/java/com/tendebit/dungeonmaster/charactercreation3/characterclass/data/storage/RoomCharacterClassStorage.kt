package com.tendebit.dungeonmaster.charactercreation3.characterclass.data.storage

import com.tendebit.dungeonmaster.charactercreation3.Normal
import com.tendebit.dungeonmaster.charactercreation3.Selected
import com.tendebit.dungeonmaster.charactercreation3.characterclass.DndCharacterClass
import com.tendebit.dungeonmaster.charactercreation3.characterclass.DndCharacterClassSelection
import com.tendebit.dungeonmaster.charactercreation3.storage.CharacterCreationDb
import com.tendebit.dungeonmaster.core.concurrency.Concurrency
import io.reactivex.Maybe
import io.reactivex.subjects.MaybeSubject
import java.util.LinkedList
import java.util.UUID

class RoomCharacterClassStorage(private val database: CharacterCreationDb, private val concurrency: Concurrency) : DndCharacterClassStorage {

	private val storageQueue = LinkedList<Pair<CharSequence, DndCharacterClassSelection>>()
	private val readQueue = LinkedList<Pair<CharSequence, MaybeSubject<DndCharacterClassSelection>>>()

	override fun storeSelection(selection: DndCharacterClassSelection, id: CharSequence?): CharSequence {
		val createdOrExistingId = id ?: UUID.randomUUID().toString()

		synchronized(storageQueue) {
			storageQueue.add(Pair(createdOrExistingId, selection))
		}

		concurrency.runDiskOrNetwork(::doStoreSelection)
		return createdOrExistingId
	}

	override fun findSelectionById(id: CharSequence): Maybe<DndCharacterClassSelection> {
		val subject = MaybeSubject.create<DndCharacterClassSelection>()

		synchronized(readQueue) {
			readQueue.add(Pair(id, subject))
		}

		concurrency.runDiskOrNetwork(::doReadSelection)
		return subject
	}

	private suspend fun doStoreSelection() {
		val nextRead = synchronized(storageQueue) {
			storageQueue.removeAt(0)
		}

		val id = nextRead.first
		val selection = nextRead.second
		database.classDao().storeClassSelection(StoredCharacterClassSelection(id.toString()))
		val classesToStore = selection.options
		for (state in classesToStore) {
			val stateAsInt = if (state is Selected) 1 else 0
			val dndClass = state.item ?: continue
			database.classDao().storeClassInfo(StoredCharacterClass(dndClass.detailsUrl, dndClass.name))
			database.classDao().storeSelectionClassJoin(StoredSelectionCharacterClassJoin(dndClass.detailsUrl, id.toString(), stateAsInt))
		}
	}

	private suspend fun doReadSelection() {
		val nextRead = synchronized(readQueue) {
			readQueue.removeAt(0)
		}

		val id = nextRead.first
		val subject = nextRead.second
		val classesForSelection = database.classDao().getClassesForSelection(id.toString())
		if (classesForSelection.isEmpty()) {
			subject.onComplete()
			return
		}
		val selectedClass = database.classDao().getSelectedClassForSelection(id.toString())
		val dndClasses = classesForSelection.map { DndCharacterClass(it.name, it.id) }
		val states = dndClasses.map { if (it.detailsUrl == selectedClass?.id) Selected(it) else Normal(it) }
		subject.onSuccess(DndCharacterClassSelection(states))
	}

}
