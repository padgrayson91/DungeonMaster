package com.tendebit.dungeonmaster.charactercreation.pages.proficiencyselection.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.tendebit.dungeonmaster.R
import com.tendebit.dungeonmaster.charactercreation.pages.proficiencyselection.view.statefragment.PROFICIENCY_SELECTION_FRAGMENT_TAG
import com.tendebit.dungeonmaster.charactercreation.pages.proficiencyselection.view.statefragment.ProficiencySelectionStateFragment
import com.tendebit.dungeonmaster.charactercreation.pages.proficiencyselection.viewmodel.CharacterProficiencySelectionState
import io.reactivex.disposables.CompositeDisposable
import java.text.MessageFormat

class ProficiencySelectionFragment : Fragment() {

    private var subscriptions: CompositeDisposable? = null
    private lateinit var chipGroup: ChipGroup
    private lateinit var instructions: TextView
    private lateinit var stateProvider: ProficiencySelectionStateFragment
    private var groupId = 0

    companion object {
        @JvmStatic
        private val KEY_PAGE_ID = "proficiency_page_index"
        @JvmStatic
        fun newInstance(id: Int) : ProficiencySelectionFragment {
            val fragment = ProficiencySelectionFragment()
            val args = Bundle()
            args.putInt(KEY_PAGE_ID, id)
            fragment.arguments = args
            return fragment
        }
    }

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
        groupId = arguments!!.getInt(KEY_PAGE_ID)
        subscriptions = CompositeDisposable()
        val addedFragment = fragmentManager?.findFragmentByTag(PROFICIENCY_SELECTION_FRAGMENT_TAG)
        if (addedFragment is ProficiencySelectionStateFragment) {
            stateProvider = addedFragment
        } else {
            stateProvider = ProficiencySelectionStateFragment()
            fragmentManager?.beginTransaction()
                    ?.add(stateProvider, PROFICIENCY_SELECTION_FRAGMENT_TAG)
                    ?.commitNow()
        }
        subscriptions?.add(stateProvider.stateChanges.filter { it.proficiencyGroups.size > groupId }.subscribe{updateViewFromState(it)})
    }

    fun pageExit() {
        subscriptions?.dispose()

    }


    private fun updateViewFromState(state: CharacterProficiencySelectionState) {
        val localState = state.proficiencyGroups[groupId]
        val group = localState.proficiencyGroup
        chipGroup.removeAllViews()
        for (proficiency in group.proficiencyOptions) {
            val chip = Chip(activity)
            chip.isCheckable = true
            chip.isChecked = state.isProficiencySelected(proficiency)
            chip.isEnabled = state.isProficiencySelectableForGroup(proficiency, groupId)
            chip.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) stateProvider.onProficiencySelected(proficiency, groupId)
                else stateProvider.onProficiencyUnselected(proficiency, groupId)
            }
            chip.text = proficiency.name
            chipGroup.addView(chip)
        }
        instructions.text = MessageFormat.format(getText(R.string.proficiency_selection).toString(), localState.remainingChoices())
    }
}