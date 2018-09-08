package com.tendebit.dungeonmaster.charactercreation.pages.proficiencyselection.view.statefragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.tendebit.dungeonmaster.charactercreation.pages.proficiencyselection.model.CharacterProficiencyDirectory
import com.tendebit.dungeonmaster.charactercreation.pages.proficiencyselection.viewmodel.CharacterProficiencyGroupSelectionState
import com.tendebit.dungeonmaster.charactercreation.pages.proficiencyselection.viewmodel.CharacterProficiencySelectionState
import com.tendebit.dungeonmaster.charactercreation.view.statefragment.CharacterCreationStateFragment
import com.tendebit.dungeonmaster.charactercreation.view.statefragment.STATE_FRAGMENT_TAG
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject

const val PROFICIENCY_SELECTION_FRAGMENT_TAG = "proficiency_selection_state_fragment"


class ProficiencySelectionStateFragment: Fragment(), ProficiencySelectionStateProvider {
    private val stateSubject = BehaviorSubject.create<CharacterProficiencySelectionState>()
    private val disposables = CompositeDisposable()
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
                    .filter { it.selectedClass != null }
                    .map { it.selectedClass }
                    .distinctUntilChanged()
                    .subscribe{
                        state.proficiencyGroups.clear()
                        state.selectedProficiencies.clear()
                        state.proficiencyGroups.addAll(Observable.fromIterable(it!!.proficiencyChoices)
                                .map { CharacterProficiencyGroupSelectionState(it) }
                                .toList()
                                .blockingGet())
                        notifyStateChange()
                    })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.dispose()
    }

    override fun onProficiencySelected(proficiency: CharacterProficiencyDirectory, id: Int) {
        state.selectedProficiencies.add(proficiency)
        state.proficiencyGroups[id].selectedProficiencies.add(proficiency)
        notifyStateChange()
    }

    override fun onProficiencyUnselected(proficiency: CharacterProficiencyDirectory, id: Int) {
        state.selectedProficiencies.remove(proficiency)
        state.proficiencyGroups[id].selectedProficiencies.remove(proficiency)
        notifyStateChange()
    }

    override fun onProficienciesConfirmed() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun notifyStateChange() {
        stateSubject.onNext(state)
    }
}