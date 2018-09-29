package com.tendebit.dungeonmaster.charactercreation.pages.proficiencyselection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.tendebit.dungeonmaster.R
import com.tendebit.dungeonmaster.charactercreation.pages.proficiencyselection.model.CharacterProficiencyDirectory
import io.reactivex.disposables.CompositeDisposable
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import java.text.MessageFormat

/**
 * UI Fragment for making selections from a single proficiency group
 */
class ProficiencySelectionFragment : Fragment() {

    private lateinit var subscriptions: CompositeDisposable
    private lateinit var chipGroup: ChipGroup
    private lateinit var instructions: TextView
    private val viewModel: ProficiencySelectionViewModel by inject("newOrExisting") { parametersOf(this) }
    private var groupId = 0

    companion object {
        @JvmStatic
        val KEY_PAGE_ID = "proficiency_page_index"
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
        groupId = arguments!!.getInt(KEY_PAGE_ID)
        subscriptions = CompositeDisposable()
        subscriptions.add(viewModel.selectionChanges.filter { it.second.size > groupId }.subscribe{updateViewForSelections(it.first, it.second)})
    }

    override fun onPause() {
        super.onPause()
        subscriptions.dispose()
    }


    private fun updateViewForSelections(selections: Collection<CharacterProficiencyDirectory>, groups: List<ProficiencyGroupSelectionViewModel>) {
        val localState = groups[groupId]
        val group = localState.proficiencyGroup
        chipGroup.removeAllViews()
        for (proficiency in group.proficiencyOptions) {
            val chip = Chip(activity)
            chip.isCheckable = true
            chip.isChecked = selections.contains(proficiency)
            chip.isEnabled = viewModel.isProficiencySelectableForGroup(proficiency, localState, selections)
            chip.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) viewModel.onProficiencySelected(proficiency, groupId)
                else viewModel.onProficiencyUnselected(proficiency, groupId)
            }
            chip.text = proficiency.name
            chipGroup.addView(chip)
        }
        instructions.text = MessageFormat.format(getText(R.string.proficiency_selection).toString(),
                localState.remainingChoices())
    }
}