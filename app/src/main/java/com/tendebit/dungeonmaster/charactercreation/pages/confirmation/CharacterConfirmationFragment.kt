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
import com.tendebit.dungeonmaster.charactercreation.pages.proficiencyselection.model.CharacterProficiencyDirectory
import com.tendebit.dungeonmaster.charactercreation.view.statefragment.CharacterCreationStateFragment
import com.tendebit.dungeonmaster.charactercreation.view.statefragment.STATE_FRAGMENT_TAG
import com.tendebit.dungeonmaster.charactercreation.viewmodel.CharacterCreationState
import com.tendebit.dungeonmaster.core.view.adapter.SimpleElementAdapter
import io.reactivex.disposables.CompositeDisposable

class CharacterConfirmationFragment : Fragment() {
    private lateinit var characterNameText: TextView
    private lateinit var raceNameText: TextView
    private lateinit var classNameText: TextView
    private lateinit var disposable : CompositeDisposable
    private val adapter = SimpleElementAdapter<CharacterProficiencyDirectory>()
    private var stateFragment: CharacterCreationStateFragment? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_character_confirmation, container, false)
        characterNameText = root.findViewById(R.id.character_name_txt)
        raceNameText = root.findViewById(R.id.race_name_txt)
        classNameText = root.findViewById(R.id.class_name_txt)

        val recycler = root.findViewById<RecyclerView>(R.id.proficiency_list)
        recycler.layoutManager = LinearLayoutManager(activity)
        recycler.adapter = adapter

        return root
    }

    override fun onResume() {
        super.onResume()
        disposable = CompositeDisposable()
        val addedFragment = activity!!.supportFragmentManager?.findFragmentByTag(STATE_FRAGMENT_TAG) as? CharacterCreationStateFragment
        if (addedFragment != null) {
            stateFragment = addedFragment
            stateFragment?.let {
                disposable.addAll(it.state.changes.subscribe {
                    updateViewFromState(it)
                })
            }
        }
    }

    override fun onPause() {
        super.onPause()
        disposable.dispose()
    }

    private fun updateViewFromState(state: CharacterCreationState) {
        characterNameText.text = getString(R.string.character_name_placeholder) // TODO: name should be part of state
        raceNameText.text = state.selectedRace?.primaryText()
        classNameText.text = state.selectedClass?.primaryText()
        adapter.update(state.selectedProficiencies)
    }
}