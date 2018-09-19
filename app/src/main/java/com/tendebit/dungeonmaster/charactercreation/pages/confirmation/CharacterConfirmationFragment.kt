package com.tendebit.dungeonmaster.charactercreation.pages.confirmation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tendebit.dungeonmaster.R
import com.tendebit.dungeonmaster.charactercreation.CharacterCreationStateFragment
import com.tendebit.dungeonmaster.charactercreation.CharacterCreationViewModel
import com.tendebit.dungeonmaster.charactercreation.STATE_FRAGMENT_TAG
import com.tendebit.dungeonmaster.charactercreation.pages.proficiencyselection.model.CharacterProficiencyDirectory
import com.tendebit.dungeonmaster.core.view.adapter.SimpleElementAdapter
import io.reactivex.disposables.CompositeDisposable

/**
 * UI Fragment for character confirmation/review
 */
class CharacterConfirmationFragment : Fragment() {
    private lateinit var characterNameText: TextView
    private lateinit var raceNameText: TextView
    private lateinit var classNameText: TextView
    private lateinit var heightText: TextView
    private lateinit var weightText: TextView
    private lateinit var disposable : CompositeDisposable
    private val adapter = SimpleElementAdapter<CharacterProficiencyDirectory>()
    private var stateFragment: CharacterCreationStateFragment? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_character_confirmation, container, false)
        characterNameText = root.findViewById(R.id.character_name_txt)
        raceNameText = root.findViewById(R.id.race_name_txt)
        classNameText = root.findViewById(R.id.class_name_txt)
        heightText = root.findViewById(R.id.height_value)
        weightText = root.findViewById(R.id.weight_value)

        val recycler = root.findViewById<RecyclerView>(R.id.proficiency_list)
        recycler.layoutManager = LinearLayoutManager(activity)
        recycler.adapter = adapter
        val addedFragment = activity!!.
                supportFragmentManager?.findFragmentByTag(STATE_FRAGMENT_TAG) as? CharacterCreationStateFragment
        if (addedFragment != null) {
            stateFragment = addedFragment
            updateViewFromState(stateFragment?.viewModel ?: return root)
        }

        return root
    }

    override fun onResume() {
        super.onResume()
        disposable = CompositeDisposable()
        stateFragment?.let {
            disposable.addAll(it.viewModel.changes.subscribe { vm ->
                updateViewFromState(vm)
            })
        }
    }

    override fun onPause() {
        super.onPause()
        disposable.dispose()
    }

    private fun updateViewFromState(viewModel: CharacterCreationViewModel) {
        val displayedName = if (viewModel.customInfo.name != null) viewModel.customInfo.name else getString(R.string.character_name_placeholder)
        characterNameText.text = displayedName
        raceNameText.text = viewModel.selectedRace?.primaryText()
        classNameText.text = viewModel.selectedClass?.primaryText()
        heightText.text = String.format(getString(R.string.character_combined_height_format),
                viewModel.customInfo.heightFeet, viewModel.customInfo.heightInches)
        weightText.text = if (viewModel.customInfo.weight != null)
                String.format(getString(R.string.character_weight_format), viewModel.customInfo.weight)
                else null
        adapter.update(viewModel.selectedProficiencies.union(viewModel.selectedClass!!.proficiencies!!))
    }
}