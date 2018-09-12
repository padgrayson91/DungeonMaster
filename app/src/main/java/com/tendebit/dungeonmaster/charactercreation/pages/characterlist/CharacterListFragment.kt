package com.tendebit.dungeonmaster.charactercreation.pages.characterlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.tendebit.dungeonmaster.R
import com.tendebit.dungeonmaster.charactercreation.pages.characterlist.viewmodel.CharacterListState
import com.tendebit.dungeonmaster.core.model.StoredCharacter
import com.tendebit.dungeonmaster.core.view.adapter.SelectionElementAdapter
import io.reactivex.disposables.CompositeDisposable

class CharacterListFragment : Fragment() {

    private lateinit var stateFragment: CharacterListStateFragment
    private lateinit var characterList: RecyclerView
    private lateinit var subscriptions: CompositeDisposable
    private lateinit var fab: FloatingActionButton
    private val adapter = SelectionElementAdapter<StoredCharacter, StoredCharacter>(null)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_character_list, container, false)
        characterList = root.findViewById(R.id.character_list)
        fab = root.findViewById(R.id.new_character_btn)
        characterList.layoutManager = LinearLayoutManager(activity)
        characterList.adapter = adapter
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
        val addedFragment = activity?.supportFragmentManager?.findFragmentByTag(CHARACTER_LIST_STATE_FRAGMENT_TAG)
        if (addedFragment is CharacterListStateFragment) {
            stateFragment = addedFragment
        } else {
            throw IllegalStateException(CharacterListFragment::class.java.simpleName + " expects a state manager to be provided")
        }

        fab.setOnClickListener { stateFragment.state.createNewCharacter() }
        subscriptions.addAll(
                stateFragment.state.changes.subscribe { updateViewFromState(it) },
                adapter.itemClicks.subscribe { stateFragment.state.select(it) }
        )
    }

    private fun pageExit() {
        subscriptions.dispose()
    }

    private fun updateViewFromState(state: CharacterListState) {
        adapter.update(state)

    }
}