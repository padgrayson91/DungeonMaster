package com.tendebit.dungeonmaster.charactercreation.pages.custominfoentry.view.statefragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.tendebit.dungeonmaster.charactercreation.pages.custominfoentry.viewmodel.CustomInfoEntryState
import com.tendebit.dungeonmaster.charactercreation.view.statefragment.CharacterCreationStateFragment
import com.tendebit.dungeonmaster.charactercreation.view.statefragment.STATE_FRAGMENT_TAG

const val CUSTOM_INFO_STATE_FRAGMENT_TAG = "custom_info_state"

class CustomInfoStateFragment : Fragment() {
    val state = CustomInfoEntryState()
    lateinit var stateFragment: CharacterCreationStateFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        val addedFragment = fragmentManager?.findFragmentByTag(STATE_FRAGMENT_TAG) as? CharacterCreationStateFragment
        if (addedFragment != null) {
            stateFragment = addedFragment
            state.changes.subscribe(stateFragment.state.customInfoObserver)
        } else {
            throw IllegalStateException(CustomInfoStateFragment::class.java.simpleName + " expected a state provider")
        }


    }
}