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
import com.tendebit.dungeonmaster.charactercreation.viewpager.CharacterCreationStateFragment
import com.tendebit.dungeonmaster.charactercreation.viewpager.STATE_FRAGMENT_TAG
import com.tendebit.dungeonmaster.core.model.StoredCharacter
import com.tendebit.dungeonmaster.core.view.adapter.SelectionElementAdapter
import io.reactivex.disposables.CompositeDisposable

class CharacterListFragment : Fragment() {

    private lateinit var stateFragment: CharacterCreationStateFragment
    private lateinit var characterList: RecyclerView
    private lateinit var subscriptions: CompositeDisposable
    private lateinit var fab: FloatingActionButton
    private lateinit var adapter: SelectionElementAdapter<StoredCharacter, StoredCharacter>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_character_list, container, false)
        characterList = root.findViewById(R.id.character_list)
        fab = root.findViewById(R.id.new_character_btn)
        characterList.layoutManager = LinearLayoutManager(activity)
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
            stateFragment = addedFragment
        } else {
            throw IllegalStateException(CharacterListFragment::class.java.simpleName + " expects a state manager to be provided")
        }


        val state = stateFragment.viewModel.listViewModel
        adapter = SelectionElementAdapter(state)
        characterList.adapter = adapter
        fab.setOnClickListener { state.createNewCharacter() }
        subscriptions.addAll(
                adapter.itemClicks.subscribe { state.select(it) }
        )
    }

    private fun pageExit() {
        subscriptions.dispose()
    }
}