package com.tendebit.dungeonmaster.charactercreation.pages.characterlist

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.tendebit.dungeonmaster.charactercreation.pages.characterlist.viewmodel.CharacterListState
import com.tendebit.dungeonmaster.charactercreation.pages.proficiencyselection.view.statefragment.ProficiencySelectionStateFragment
import com.tendebit.dungeonmaster.charactercreation.view.statefragment.CharacterCreationStateFragment
import com.tendebit.dungeonmaster.charactercreation.view.statefragment.STATE_FRAGMENT_TAG
import com.tendebit.dungeonmaster.core.model.DnDDatabase
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.cancelAndJoin
import kotlinx.coroutines.experimental.launch

const val CHARACTER_LIST_STATE_FRAGMENT_TAG = "character_list_state"

class CharacterListStateFragment : Fragment() {
    val state = CharacterListState()
    lateinit var stateFragment : CharacterCreationStateFragment
    private var job: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        val addedFragment = fragmentManager?.findFragmentByTag(STATE_FRAGMENT_TAG) as? CharacterCreationStateFragment
        if (addedFragment != null) {
            stateFragment = addedFragment
            state.changes.subscribe(stateFragment.state.savedCharacterSelectionObserver)
        } else {
            throw IllegalStateException(ProficiencySelectionStateFragment::class.java.simpleName + " expected a state provider")
        }

        // TODO: this should be in a separate class
        job = launch(UI) {
            val db = DnDDatabase.getInstance(activity!!)
            val results = async(parent = job) {
                db.characterDao().getCharacters()
            }.await()
            state.updateOptions(results)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        launch(UI) {
            job?.cancelAndJoin()
        }
    }
}