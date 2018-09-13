package com.tendebit.dungeonmaster.charactercreation.viewpager

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.tendebit.dungeonmaster.charactercreation.pages.characterlist.CharacterListState
import com.tendebit.dungeonmaster.charactercreation.pages.classselection.ClassSelectionState
import com.tendebit.dungeonmaster.charactercreation.pages.classselection.model.CharacterClassInfoSupplier
import com.tendebit.dungeonmaster.charactercreation.pages.custominfoentry.CustomInfoEntryState
import com.tendebit.dungeonmaster.charactercreation.pages.proficiencyselection.ProficiencySelectionState
import com.tendebit.dungeonmaster.charactercreation.pages.raceselection.RaceSelectionState
import com.tendebit.dungeonmaster.charactercreation.pages.raceselection.model.CharacterRaceInfoSupplier
import com.tendebit.dungeonmaster.core.model.DnDDatabase
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.cancelAndJoin
import kotlinx.coroutines.experimental.launch

const val STATE_FRAGMENT_TAG = "character_creation_state_fragment"

class CharacterCreationStateFragment : Fragment() {


    lateinit var state: CharacterCreationState
    private lateinit var savedCharacterListState: CharacterListState
    private lateinit var raceState : RaceSelectionState
    private lateinit var classState: ClassSelectionState
    private lateinit var proficiencyState: ProficiencySelectionState
    private val customInfoState = CustomInfoEntryState()
    lateinit var db : DnDDatabase
    var job : Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        db = DnDDatabase.getInstance(activity!!)
        savedCharacterListState = CharacterListState(db)
        raceState = RaceSelectionState(CharacterRaceInfoSupplier.Impl(db))
        classState = ClassSelectionState(CharacterClassInfoSupplier.Impl(db))
        proficiencyState = ProficiencySelectionState()
        state = CharacterCreationState(db, savedCharacterListState, raceState,
                classState, proficiencyState, customInfoState)
    }

    override fun onDestroy() {
        super.onDestroy()
        state.cancelAllSubscriptions()
        savedCharacterListState.cancelAllCalls()
        raceState.cancelAllCalls()
        classState.cancelAllCalls()
        launch(UI) {
            job?.cancelAndJoin()
        }
    }

}