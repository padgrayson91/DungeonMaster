package com.tendebit.dungeonmaster.charactercreation.pages.characterlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.tendebit.dungeonmaster.App
import com.tendebit.dungeonmaster.R
import com.tendebit.dungeonmaster.charactercreation.CharacterCreationStateFragment
import com.tendebit.dungeonmaster.charactercreation.CharacterCreationViewModel
import com.tendebit.dungeonmaster.charactercreation.STATE_FRAGMENT_TAG
import com.tendebit.dungeonmaster.charactercreation.model.StoredCharacterSupplier
import com.tendebit.dungeonmaster.core.model.DnDDatabase
import com.tendebit.dungeonmaster.core.view.adapter.SelectionElementAdapter

/**
 * UI Fragment for the list of saved characters
 */
class CharacterListFragment : Fragment() {

    private lateinit var stateFragment: CharacterCreationStateFragment
    private lateinit var characterList: RecyclerView
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
        val addedFragment = activity?.supportFragmentManager?.findFragmentByTag(STATE_FRAGMENT_TAG)
        val viewModelTag = arguments!![CharacterCreationViewModel.ARG_VIEW_MODEL_TAG] as String
        if (addedFragment is CharacterCreationStateFragment) {
            stateFragment = addedFragment
            var viewModel = stateFragment.viewModel.getChildViewModel<CharacterListViewModel>(viewModelTag)
            if (viewModel == null) {
                viewModel = CharacterListViewModel(StoredCharacterSupplier.Impl(
                        DnDDatabase.getInstance(App.instance.applicationContext).characterDao()))
                stateFragment.viewModel.addCharacterList(viewModelTag, viewModel)
            }
            adapter = SelectionElementAdapter(viewModel)
            characterList.adapter = adapter
            fab.setOnClickListener { viewModel.createNewCharacter() }
        } else {
            throw IllegalStateException(CharacterListFragment::class.java.simpleName + " expects a state manager to be provided")
        }
    }

    override fun onPause() {
        super.onPause()
        adapter.clear()
    }
}