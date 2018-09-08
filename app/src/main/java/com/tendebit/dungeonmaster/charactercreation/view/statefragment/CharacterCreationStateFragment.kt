package com.tendebit.dungeonmaster.charactercreation.view.statefragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.tendebit.dungeonmaster.charactercreation.viewmodel.CharacterCreationState

const val STATE_FRAGMENT_TAG = "character_creation_state_fragment"

class CharacterCreationStateFragment : Fragment() {


    val state = CharacterCreationState()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onDestroy() {
        super.onDestroy()
        state.cancelAllSubscriptions()
    }

}