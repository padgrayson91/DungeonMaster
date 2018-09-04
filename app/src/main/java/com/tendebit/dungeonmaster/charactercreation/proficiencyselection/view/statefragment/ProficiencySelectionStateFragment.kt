package com.tendebit.dungeonmaster.charactercreation.proficiencyselection.view.statefragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.tendebit.dungeonmaster.charactercreation.classselection.model.CharacterClassInfoService
import com.tendebit.dungeonmaster.charactercreation.proficiencyselection.model.CharacterProficiencyDirectory
import com.tendebit.dungeonmaster.charactercreation.proficiencyselection.viewmodel.CharacterProficiencySelectionState
import com.tendebit.dungeonmaster.charactercreation.view.statefragment.CharacterCreationStateFragment
import com.tendebit.dungeonmaster.charactercreation.view.statefragment.STATE_FRAGMENT_TAG
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.coroutines.experimental.Job

const val PROFICIENCY_SELECTION_FRAGMENT_TAG = "proficiency_selection_state_fragment"


class ProficiencySelectionStateFragment: Fragment(), ProficiencySelectionStateProvider {
    private val service = CharacterClassInfoService.Impl()
    private val stateSubject = BehaviorSubject.create<CharacterProficiencySelectionState>()
    private val disposables = CompositeDisposable()
    private var job: Job? = null
    private lateinit var stateFragment: CharacterCreationStateFragment
    override val stateChanges = stateSubject as Observable<CharacterProficiencySelectionState>

    private val state = CharacterProficiencySelectionState()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        val addedFragment = fragmentManager?.findFragmentByTag(STATE_FRAGMENT_TAG) as? CharacterCreationStateFragment
        if (addedFragment != null) {
            stateFragment = addedFragment
            disposables.add(stateFragment.stateChanges
                    .map { it.selectedClass }
                    .distinct()
                    .subscribe({
                        state.proficiencyGroup = it?.proficiencyChoices?.get(0)
                        state.selectedProficiencies.clear()
                        notifyStateChange()
                    }))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.dispose()
    }

    override fun onProficiencySelected(proficiency: CharacterProficiencyDirectory) {
        state.selectedProficiencies.add(proficiency)
        if (state.proficiencyGroup!!.choiceCount == state.selectedProficiencies.size) {
            notifyStateChange()
        }
    }

    override fun onProficiencyUnselected(proficiency: CharacterProficiencyDirectory) {
        state.selectedProficiencies.remove(proficiency)
        if (state.proficiencyGroup!!.choiceCount == state.selectedProficiencies.size + 1) {
            notifyStateChange()
        }
    }

    override fun onProficienciesConfirmed() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun notifyStateChange() {
        stateSubject.onNext(state)
    }
}