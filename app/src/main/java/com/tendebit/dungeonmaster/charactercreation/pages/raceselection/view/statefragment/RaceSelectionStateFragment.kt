package com.tendebit.dungeonmaster.charactercreation.pages.raceselection.view.statefragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.tendebit.dungeonmaster.charactercreation.pages.raceselection.model.CharacterRaceInfoSupplier
import com.tendebit.dungeonmaster.charactercreation.pages.raceselection.viewmodel.RaceSelectionState
import com.tendebit.dungeonmaster.charactercreation.view.statefragment.CharacterCreationStateFragment
import com.tendebit.dungeonmaster.charactercreation.view.statefragment.STATE_FRAGMENT_TAG

const val RACE_SELECTION_FRAGMENT_TAG = "race_selection_state_fragment"

class RaceSelectionStateFragment : Fragment(){
    private var stateFragment: CharacterCreationStateFragment? = null
    lateinit var state :RaceSelectionState

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        state = RaceSelectionState(CharacterRaceInfoSupplier.Impl(activity!!))
        val addedFragment = fragmentManager?.findFragmentByTag(STATE_FRAGMENT_TAG) as? CharacterCreationStateFragment
        if (addedFragment != null) {
            stateFragment = addedFragment
            stateFragment?.let {
                state.stateChanges
                        .subscribe(it.state.raceSelectionObserver)
            }
        }

        retainInstance = true
    }

    override fun onDestroy() {
        super.onDestroy()
        state.cancelAllCalls()
    }
}