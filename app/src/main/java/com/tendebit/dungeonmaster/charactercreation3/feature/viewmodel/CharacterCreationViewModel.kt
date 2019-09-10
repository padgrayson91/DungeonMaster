package com.tendebit.dungeonmaster.charactercreation3.feature.viewmodel

import com.tendebit.dungeonmaster.App
import com.tendebit.dungeonmaster.charactercreation3.ability.viewmodel.DndAbilitySelectionViewModel
import com.tendebit.dungeonmaster.charactercreation3.abilitycore.DndAbilityPrerequisites
import com.tendebit.dungeonmaster.charactercreation3.abilitycore.DndAbilitySource
import com.tendebit.dungeonmaster.charactercreation3.characterclass.data.DndCharacterClassDataStoreImpl
import com.tendebit.dungeonmaster.charactercreation3.characterclass.data.DndClassPrerequisites
import com.tendebit.dungeonmaster.charactercreation3.characterclass.data.network.DndCharacterClassApiConnection
import com.tendebit.dungeonmaster.charactercreation3.characterclass.data.storage.RoomCharacterClassStorage
import com.tendebit.dungeonmaster.charactercreation3.characterclass.viewmodel.DndCharacterClassSelectionViewModel
import com.tendebit.dungeonmaster.charactercreation3.feature.CharacterCreation
import com.tendebit.dungeonmaster.charactercreation3.feature.storage.CharacterCreationDb
import com.tendebit.dungeonmaster.charactercreation3.proficiency.storage.RoomProficiencyStorage
import com.tendebit.dungeonmaster.charactercreation3.proficiency.viewmodel.DndProficiencySelectionViewModel
import com.tendebit.dungeonmaster.charactercreation3.proficiencycore.DndProficiencySource
import com.tendebit.dungeonmaster.charactercreation3.proficiencycore.ProficiencyPrerequisites
import com.tendebit.dungeonmaster.charactercreation3.race.data.DndRaceDataStoreImpl
import com.tendebit.dungeonmaster.charactercreation3.race.data.DndRacePrerequisites
import com.tendebit.dungeonmaster.charactercreation3.race.data.network.DndRaceApiConnection
import com.tendebit.dungeonmaster.charactercreation3.race.data.storage.RoomRaceStorage
import com.tendebit.dungeonmaster.charactercreation3.race.viewmodel.DndRaceSelectionViewModel
import com.tendebit.dungeonmastercore.concurrency.CoroutineConcurrency
import com.tendebit.dungeonmastercore.model.state.ItemState
import com.tendebit.dungeonmastercore.viewmodel3.Clearable
import com.tendebit.dungeonmastercore.viewmodel3.ViewModel
import io.reactivex.Observable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Suppress("UNCHECKED_CAST")
class CharacterCreationViewModel(val state: CharacterCreation) : ViewModel, Clearable {

	private val job = Job()
	private val viewModelScope = CoroutineScope(Dispatchers.Main + job)
	private val concurrency = CoroutineConcurrency(viewModelScope)

	val sectionsViewModel = CharacterCreationSectionsViewModel(
			listOf(DndRaceSelectionViewModel(state.races),
					DndCharacterClassSelectionViewModel(state.classes, concurrency),
					DndProficiencySelectionViewModel(state.proficiencies, concurrency),
					DndAbilitySelectionViewModel(state.abilities, concurrency)))

	override val changes: Observable<out ViewModel>
		get() = Observable.just(this)

	init {
		viewModelScope.launch(context = Dispatchers.IO) {
			val db = CharacterCreationDb.getInstance(App.instance.applicationContext)
			val racePrerequisites = DndRacePrerequisites.Impl(concurrency, DndRaceDataStoreImpl(DndRaceApiConnection.Impl(), RoomRaceStorage(db.raceDao(), concurrency)))
			val classPrerequisites = DndClassPrerequisites.Impl(concurrency, DndCharacterClassDataStoreImpl(DndCharacterClassApiConnection.Impl(), RoomCharacterClassStorage(db.classDao(), concurrency)))
			val proficiencyPrerequisites = ProficiencyPrerequisites.Impl(concurrency,
					listOf(
							state.classes.selectedClassDetails as Observable<ItemState<out DndProficiencySource>>,
							state.races.selectedRaceDetails as Observable<ItemState<out DndProficiencySource>>),
					RoomProficiencyStorage(db.proficiencyDao(), concurrency))
			val abilityPrerequisites = DndAbilityPrerequisites.Impl(concurrency, listOf(state.races.selectedRaceDetails as Observable<ItemState<out DndAbilitySource>>))

			state.races.start(racePrerequisites)
			state.classes.start(classPrerequisites)
			state.proficiencies.start(proficiencyPrerequisites)
			state.abilities.start(abilityPrerequisites)
		}
	}

	override fun clear() {
		job.cancel()
		state.proficiencies.stop()
	}

}
