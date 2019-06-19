package com.tendebit.dungeonmaster.charactercreation3.feature.viewmodel

import com.tendebit.dungeonmaster.App
import com.tendebit.dungeonmaster.charactercreation3.characterclass.data.DndCharacterClassDataStoreImpl
import com.tendebit.dungeonmaster.charactercreation3.characterclass.data.DndClassPrerequisites
import com.tendebit.dungeonmaster.charactercreation3.characterclass.data.network.DndCharacterClassApiConnection
import com.tendebit.dungeonmaster.charactercreation3.characterclass.data.storage.RoomCharacterClassStorage
import com.tendebit.dungeonmaster.charactercreation3.characterclass.viewmodel.DndCharacterClassSelectionViewModel
import com.tendebit.dungeonmaster.charactercreation3.feature.CharacterCreation
import com.tendebit.dungeonmaster.charactercreation3.feature.storage.CharacterCreationDb
import com.tendebit.dungeonmaster.charactercreation3.proficiency.ProficiencyPrerequisites
import com.tendebit.dungeonmaster.charactercreation3.proficiency.data.storage.RoomProficiencyStorage
import com.tendebit.dungeonmaster.charactercreation3.proficiency.viewmodel.DndProficiencySelectionViewModel
import com.tendebit.dungeonmaster.charactercreation3.race.data.DndRaceDataStoreImpl
import com.tendebit.dungeonmaster.charactercreation3.race.data.DndRacePrerequisites
import com.tendebit.dungeonmaster.charactercreation3.race.data.network.DndRaceApiConnection
import com.tendebit.dungeonmaster.charactercreation3.race.data.storage.RoomRaceStorage
import com.tendebit.dungeonmaster.charactercreation3.race.viewmodel.DndRaceSelectionViewModel
import com.tendebit.dungeonmaster.core.concurrency.CoroutineConcurrency
import com.tendebit.dungeonmaster.core.viewmodel3.Clearable
import com.tendebit.dungeonmaster.core.viewmodel3.ViewModel
import io.reactivex.Observable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class CharacterCreationViewModel(val state: CharacterCreation) : ViewModel, Clearable {

	private val job = Job()
	private val viewModelScope = CoroutineScope(Dispatchers.Main + job)
	private val concurrency = CoroutineConcurrency(viewModelScope)

	val sectionsViewModel = CharacterCreationSectionsViewModel(
			listOf(DndRaceSelectionViewModel(state.races),
					DndCharacterClassSelectionViewModel(state.classes, concurrency),
					DndProficiencySelectionViewModel(state.proficiencies)))

	override val changes: Observable<out ViewModel>
		get() = Observable.just(this)

	init {
		viewModelScope.launch(context = Dispatchers.IO) {
			val db = CharacterCreationDb.getInstance(App.instance.applicationContext)
			val racePrerequisites = DndRacePrerequisites.Impl(concurrency, DndRaceDataStoreImpl(DndRaceApiConnection.Impl(), RoomRaceStorage(db.raceDao(), concurrency)))
			val classPrerequisites = DndClassPrerequisites.Impl(concurrency, DndCharacterClassDataStoreImpl(DndCharacterClassApiConnection.Impl(), RoomCharacterClassStorage(db.classDao(), concurrency)))
			val proficiencyPrerequisites = ProficiencyPrerequisites.Impl(state.classes.externalStateChanges.mergeWith(state.classes.internalStateChanges), RoomProficiencyStorage(db.proficiencyDao(), concurrency))

			state.races.start(racePrerequisites)
			state.classes.start(classPrerequisites)
			state.proficiencies.start(proficiencyPrerequisites, viewModelScope)
		}
	}

	override fun clear() {
		job.cancel()
	}

}
