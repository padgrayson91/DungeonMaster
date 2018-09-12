package com.tendebit.dungeonmaster.charactercreation.view.statefragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.tendebit.dungeonmaster.charactercreation.pages.characterlist.viewmodel.CharacterListState
import com.tendebit.dungeonmaster.charactercreation.pages.classselection.model.CharacterClassInfoSupplier
import com.tendebit.dungeonmaster.charactercreation.pages.classselection.viewmodel.ClassSelectionState
import com.tendebit.dungeonmaster.charactercreation.pages.custominfoentry.viewmodel.CustomInfoEntryState
import com.tendebit.dungeonmaster.charactercreation.pages.proficiencyselection.viewmodel.ProficiencySelectionState
import com.tendebit.dungeonmaster.charactercreation.pages.raceselection.model.CharacterRaceInfoSupplier
import com.tendebit.dungeonmaster.charactercreation.pages.raceselection.viewmodel.RaceSelectionState
import com.tendebit.dungeonmaster.charactercreation.viewmodel.CharacterCreationState
import com.tendebit.dungeonmaster.core.model.DnDDatabase
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.cancelAndJoin
import kotlinx.coroutines.experimental.launch

const val STATE_FRAGMENT_TAG = "character_creation_state_fragment"

class CharacterCreationStateFragment : Fragment() {


    val state = CharacterCreationState()
    val savedCharacterListState = CharacterListState()
    lateinit var raceState : RaceSelectionState
    lateinit var classState: ClassSelectionState
    lateinit var proficiencyState: ProficiencySelectionState
    val customInfoState = CustomInfoEntryState()
    lateinit var db : DnDDatabase
    var job : Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        db = DnDDatabase.getInstance(activity!!)
        raceState = RaceSelectionState(CharacterRaceInfoSupplier.Impl(activity!!))
        classState = ClassSelectionState(CharacterClassInfoSupplier.Impl(activity!!))
        proficiencyState = ProficiencySelectionState(state.changes)

        savedCharacterListState.changes.subscribe(state.savedCharacterSelectionObserver)
        raceState.stateChanges.subscribe(state.raceSelectionObserver)
        classState.changes.subscribe(state.classSelectionObserver)
        proficiencyState.changes.subscribe(state.proficiencySelectionObserver)
        customInfoState.changes.subscribe(state.customInfoObserver)
        attemptLoadSavedCharactersFromDb()
    }

    override fun onDestroy() {
        super.onDestroy()
        state.cancelAllSubscriptions()
        raceState.cancelAllCalls()
        classState.cancelAllCalls()
        proficiencyState.cancelAllSubscriptions()
        launch(UI) {
            job?.cancelAndJoin()
        }
    }

    private fun attemptLoadSavedCharactersFromDb()  {
        // TODO: this should be in a separate class
        job = launch(UI) {
            val db = DnDDatabase.getInstance(activity!!)
            val results = async(parent = job) {
                db.characterDao().getCharacters()
            }.await()
            savedCharacterListState.updateOptions(results)
        }
    }

}