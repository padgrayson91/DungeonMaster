package com.tendebit.dungeonmaster.charactercreation.pages.proficiencyselection.view.statefragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.tendebit.dungeonmaster.charactercreation.pages.proficiencyselection.viewmodel.CharacterProficiencySelectionState
import com.tendebit.dungeonmaster.charactercreation.view.statefragment.CharacterCreationStateFragment
import com.tendebit.dungeonmaster.charactercreation.view.statefragment.STATE_FRAGMENT_TAG

const val PROFICIENCY_SELECTION_FRAGMENT_TAG = "proficiency_selection_state_fragment"


class ProficiencySelectionStateFragment: Fragment() {
    private lateinit var stateFragment: CharacterCreationStateFragment

    lateinit var state : CharacterProficiencySelectionState

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        val addedFragment = fragmentManager?.findFragmentByTag(STATE_FRAGMENT_TAG) as? CharacterCreationStateFragment
        if (addedFragment != null) {
            stateFragment = addedFragment
            state = CharacterProficiencySelectionState(stateFragment.state.changes)
        } else {
            throw IllegalStateException(ProficiencySelectionStateFragment::class.java.simpleName + " expected a state provider")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        state.cancelAllSubscriptions()
    }
}