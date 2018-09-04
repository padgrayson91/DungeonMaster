package com.tendebit.dungeonmaster.charactercreation.proficiencyselection.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.tendebit.dungeonmaster.R
import com.tendebit.dungeonmaster.charactercreation.classselection.viewmodel.statefragment.CLASS_SELECTION_FRAGMENT_TAG
import com.tendebit.dungeonmaster.charactercreation.classselection.viewmodel.statefragment.ClassSelectionStateFragment
import com.tendebit.dungeonmaster.charactercreation.proficiencyselection.model.CharacterProficiencyDirectory
import com.tendebit.dungeonmaster.charactercreation.proficiencyselection.model.CharacterProficiencyGroup
import com.tendebit.dungeonmaster.charactercreation.proficiencyselection.viewmodel.CharacterProficiencySelectionState
import com.tendebit.dungeonmaster.charactercreation.proficiencyselection.viewmodel.statefragment.PROFICIENCY_SELECTION_FRAGMENT_TAG
import com.tendebit.dungeonmaster.charactercreation.proficiencyselection.viewmodel.statefragment.ProficiencySelectionStateFragment
import com.tendebit.dungeonmaster.charactercreation.proficiencyselection.viewmodel.statefragment.ProficiencySelectionStateProvider
import io.reactivex.disposables.CompositeDisposable

class ProficiencySelectionFragment : Fragment() {
    private var subscriptions: CompositeDisposable? = null
    private lateinit var chipGroup: ChipGroup
    private lateinit var instructions: TextView
    private lateinit var stateProvider: ProficiencySelectionStateFragment

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_proficiency_selection, container, false)
        chipGroup = root.findViewById(R.id.proficiency_chips)
        instructions = root.findViewById(R.id.instructions)
        return root
    }


    override fun onResume() {
        super.onResume()
        pageEnter()
    }

    override fun onPause() {
        super.onPause()
        pageExit()
    }

    fun pageEnter() {
        subscriptions = CompositeDisposable()
        val addedFragment = fragmentManager?.findFragmentByTag(PROFICIENCY_SELECTION_FRAGMENT_TAG)
        if (addedFragment is ProficiencySelectionStateFragment) {
            stateProvider = addedFragment
        } else {
            stateProvider = ProficiencySelectionStateFragment()
            fragmentManager?.beginTransaction()
                    ?.add(stateProvider, PROFICIENCY_SELECTION_FRAGMENT_TAG)
                    ?.commit()
        }
        subscriptions?.add(stateProvider.stateChanges.subscribe({updateViewFromState(it)}))
    }

    fun pageExit() {
        subscriptions?.dispose()

    }


    private fun updateViewFromState(state: CharacterProficiencySelectionState) {
        state.proficiencyGroup?.let {
            chipGroup.removeAllViews()
            for (proficiency in it.proficiencyOptions) {
                val chip = Chip(activity)
                chip.isCheckable = true
                chip.isChecked = state.selectedProficiencies.contains(proficiency)
                chip.isEnabled = chip.isChecked || state.selectedProficiencies.size < it.choiceCount
                chip.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) stateProvider.onProficiencySelected(proficiency)
                    else stateProvider.onProficiencyUnselected(proficiency)
                }
                chip.text = proficiency.name
                chipGroup.addView(chip)
            }
            instructions.text = String.format(getString(R.string.proficiency_selection), it.choiceCount)
        }
    }
}