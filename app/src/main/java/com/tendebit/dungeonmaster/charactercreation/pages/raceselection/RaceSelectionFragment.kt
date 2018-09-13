package com.tendebit.dungeonmaster.charactercreation.pages.raceselection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tendebit.dungeonmaster.R
import com.tendebit.dungeonmaster.charactercreation.pages.raceselection.model.CharacterRaceDirectory
import com.tendebit.dungeonmaster.charactercreation.viewpager.CharacterCreationStateFragment
import com.tendebit.dungeonmaster.charactercreation.viewpager.STATE_FRAGMENT_TAG
import com.tendebit.dungeonmaster.core.view.adapter.SelectionElementAdapter
import io.reactivex.disposables.CompositeDisposable

class RaceSelectionFragment : Fragment() {

    private lateinit var subscriptions: CompositeDisposable
    private lateinit var recycler: RecyclerView
    private lateinit var stateProvider: CharacterCreationStateFragment
    private lateinit var adapter: SelectionElementAdapter<CharacterRaceDirectory, CharacterRaceDirectory>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_generic_list, container, false)
        recycler = root.findViewById(R.id.class_list)
        recycler.layoutManager = LinearLayoutManager(activity)
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
        val addedFragment = activity?.supportFragmentManager?.findFragmentByTag(STATE_FRAGMENT_TAG)
        if (addedFragment is CharacterCreationStateFragment) {
            stateProvider = addedFragment
        } else {
            throw IllegalStateException(RaceSelectionFragment::class.java.simpleName + " expects a state manager to be provided")

        }

        val state = stateProvider.state.raceState
        adapter = SelectionElementAdapter(state)
        recycler.adapter = adapter
        subscriptions.addAll(
                adapter.itemClicks.subscribe{state.select(it)}
        )
    }

    private fun pageExit() {
        subscriptions.dispose()
    }
}