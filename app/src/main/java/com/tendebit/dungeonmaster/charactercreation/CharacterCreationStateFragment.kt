package com.tendebit.dungeonmaster.charactercreation

import android.os.Bundle
import androidx.fragment.app.Fragment
import org.koin.android.ext.android.inject

const val STATE_FRAGMENT_TAG = "character_creation_state_fragment"

/**
 * Headless fragment which maintains references to the ViewModel for character creation.  Because this
 * fragment is headless, it does not get destroyed/recreated when the UI is redrawn, so the data will
 * be preserved here across orientation change.  When we are exiting the activity entirely, this fragment will
 * be destroyed and the data cleaned up
 */
class CharacterCreationStateFragment : Fragment() {


    val viewModel: CharacterCreationViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDetach()
        viewModel.resetWorkflow ()
    }

}