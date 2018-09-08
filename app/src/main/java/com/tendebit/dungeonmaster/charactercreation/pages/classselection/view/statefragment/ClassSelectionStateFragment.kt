package com.tendebit.dungeonmaster.charactercreation.pages.classselection.view.statefragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.tendebit.dungeonmaster.charactercreation.pages.classselection.model.CharacterClassInfoSupplier
import com.tendebit.dungeonmaster.charactercreation.pages.classselection.viewmodel.ClassSelectionState
import com.tendebit.dungeonmaster.charactercreation.view.statefragment.CharacterCreationStateFragment
import com.tendebit.dungeonmaster.charactercreation.view.statefragment.STATE_FRAGMENT_TAG

const val CLASS_SELECTION_FRAGMENT_TAG = "class_selection_state_fragment"


class ClassSelectionStateFragment : Fragment() {
    private var stateFragment: CharacterCreationStateFragment? = null
    lateinit var state : ClassSelectionState

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        state = ClassSelectionState(CharacterClassInfoSupplier.Impl(activity!!))
        val addedFragment = fragmentManager?.findFragmentByTag(STATE_FRAGMENT_TAG) as? CharacterCreationStateFragment
        if (addedFragment != null) {
            stateFragment = addedFragment
            stateFragment?.let {
                state.changes
                        .subscribe(it.state.classSelectionObserver)
            }
        }

        retainInstance = true
    }

    override fun onDestroy() {
        super.onDestroy()
        state.cancelAllCalls()
    }
}