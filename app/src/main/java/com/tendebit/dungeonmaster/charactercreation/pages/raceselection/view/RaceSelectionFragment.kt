package com.tendebit.dungeonmaster.charactercreation.pages.raceselection.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tendebit.dungeonmaster.R
import com.tendebit.dungeonmaster.charactercreation.pages.raceselection.model.CharacterRaceDirectory
import com.tendebit.dungeonmaster.core.view.adapter.SelectionElementAdapter
import com.tendebit.dungeonmaster.charactercreation.pages.raceselection.view.statefragment.RACE_SELECTION_FRAGMENT_TAG
import com.tendebit.dungeonmaster.charactercreation.pages.raceselection.view.statefragment.RaceSelectionStateFragment
import com.tendebit.dungeonmaster.charactercreation.pages.raceselection.viewmodel.CharacterRaceSelectionState
import io.reactivex.disposables.CompositeDisposable

class RaceSelectionFragment : Fragment() {

    private lateinit var subscriptions: CompositeDisposable
    private lateinit var recycler: RecyclerView
    private lateinit var stateProvider: RaceSelectionStateFragment
    private val adapter = SelectionElementAdapter<CharacterRaceDirectory, CharacterRaceDirectory>(null)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_generic_list, container, false)
        recycler = root.findViewById(R.id.class_list)
        recycler.layoutManager = LinearLayoutManager(activity)
        recycler.adapter = adapter
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

    private fun pageEnter() {
        subscriptions = CompositeDisposable()
        val addedFragment = fragmentManager?.findFragmentByTag(RACE_SELECTION_FRAGMENT_TAG)
        if (addedFragment is RaceSelectionStateFragment) {
            stateProvider = addedFragment
        } else {
            stateProvider = RaceSelectionStateFragment()
            fragmentManager?.beginTransaction()
                    ?.add(stateProvider, RACE_SELECTION_FRAGMENT_TAG)
                    ?.commit()
        }
        subscriptions.addAll(
                stateProvider.stateChanges.subscribe{updateViewFromState(it)},
                adapter.itemClicks.subscribe{stateProvider.onRaceSelected(it)}
        )
    }

    private fun pageExit() {
        subscriptions.dispose()
    }


    private fun updateViewFromState(state: CharacterRaceSelectionState) {
        if (state.options.size > 0) {
            adapter.update(state)
        }
    }
}