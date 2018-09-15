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
import com.tendebit.dungeonmaster.charactercreation.CharacterCreationStateFragment
import com.tendebit.dungeonmaster.charactercreation.STATE_FRAGMENT_TAG
import com.tendebit.dungeonmaster.core.view.adapter.SelectionElementAdapter
import com.tendebit.dungeonmaster.core.viewmodel.DisplayedCharacter
import com.tendebit.dungeonmaster.core.viewmodel.ItemAction
import io.reactivex.disposables.CompositeDisposable

/**
 * UI Fragment for the list of saved characters
 */
class CharacterListFragment : Fragment() {

    private lateinit var stateFragment: CharacterCreationStateFragment
    private lateinit var characterList: RecyclerView
    private lateinit var subscriptions: CompositeDisposable
    private lateinit var fab: FloatingActionButton
    private lateinit var adapter: SelectionElementAdapter<DisplayedCharacter, DisplayedCharacter>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_generic_list, container, false)
        characterList = root.findViewById(R.id.item_list)
        fab = root.findViewById(R.id.create_new_btn)
        fab.contentDescription = getString(R.string.add_character_button_accessible)
        fab.show()
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


        val viewModel = stateFragment.viewModel.listViewModel
        adapter = SelectionElementAdapter(viewModel)
        characterList.adapter = adapter
        fab.setOnClickListener { viewModel.createNewCharacter() }
        subscriptions.addAll(
                adapter.itemActions.subscribe { handleItemActions(viewModel, it.first, it.second) }
        )
    }

    private fun handleItemActions(viewModel: CharacterListViewModel, character: DisplayedCharacter, actions: List<ItemAction>) {
        for (action in actions) {
            when (action) {
                ItemAction.SELECT -> viewModel.select(character)
                ItemAction.DELETE -> viewModel.delete(character)
                else -> throw RuntimeException("User requested action ${action.name} but no action could be performed")
            }
        }
    }

    private fun pageExit() {
        subscriptions.dispose()
    }
}