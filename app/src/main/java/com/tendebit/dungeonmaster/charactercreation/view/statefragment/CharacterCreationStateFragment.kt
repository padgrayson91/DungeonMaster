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
import kotlinx.coroutines.experimental.cancelAndJoin
import kotlinx.coroutines.experimental.launch

const val STATE_FRAGMENT_TAG = "character_creation_state_fragment"

class CharacterCreationStateFragment : Fragment() {


    val state = CharacterCreationState()
    lateinit var savedCharacterListState: CharacterListState
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
        savedCharacterListState = CharacterListState(db)
        raceState = RaceSelectionState(CharacterRaceInfoSupplier.Impl(db))
        classState = ClassSelectionState(CharacterClassInfoSupplier.Impl(db))
        proficiencyState = ProficiencySelectionState(state.changes)

        savedCharacterListState.changes.subscribe(state.savedCharacterSelectionObserver)
        raceState.stateChanges.subscribe(state.raceSelectionObserver)
        classState.changes.subscribe(state.classSelectionObserver)
        proficiencyState.changes.subscribe(state.proficiencySelectionObserver)
        customInfoState.changes.subscribe(state.customInfoObserver)
    }

    override fun onDestroy() {
        super.onDestroy()
        state.cancelAllSubscriptions()
        savedCharacterListState.cancelAllCalls()
        raceState.cancelAllCalls()
        classState.cancelAllCalls()
        proficiencyState.cancelAllSubscriptions()
        launch(UI) {
            job?.cancelAndJoin()
        }
    }

}