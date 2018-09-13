package com.tendebit.dungeonmaster.charactercreation.pages.classselection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tendebit.dungeonmaster.R
import com.tendebit.dungeonmaster.charactercreation.pages.classselection.model.CharacterClassDirectory
import com.tendebit.dungeonmaster.charactercreation.pages.classselection.model.CharacterClassInfo
import com.tendebit.dungeonmaster.charactercreation.viewpager.CharacterCreationStateFragment
import com.tendebit.dungeonmaster.charactercreation.viewpager.STATE_FRAGMENT_TAG
import com.tendebit.dungeonmaster.core.view.adapter.SelectionElementAdapter
import io.reactivex.disposables.CompositeDisposable


class ClassSelectionFragment : Fragment() {

    private lateinit var subscriptions: CompositeDisposable
    private lateinit var recycler: RecyclerView
    private lateinit var stateProvider: CharacterCreationStateFragment
    private lateinit var adapter: SelectionElementAdapter<CharacterClassDirectory, CharacterClassInfo>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_generic_list, container, false)
        recycler = root.findViewById(R.id.class_list)
        recycler.layoutManager = LinearLayoutManager(activity)
        return root
    }


    override fun onResume() {
        super.onResume()
        subscriptions = CompositeDisposable()
        val addedFragment = activity?.supportFragmentManager?.findFragmentByTag(STATE_FRAGMENT_TAG)
        if (addedFragment is CharacterCreationStateFragment) {
            stateProvider = addedFragment
        } else {
            throw IllegalStateException(ClassSelectionFragment::class.java.simpleName + " expects a state manager to be provided")
        }

        val state = stateProvider.viewModel.classViewModel
        adapter = SelectionElementAdapter(state)
        recycler.adapter = adapter
        subscriptions.addAll(
                adapter.itemClicks.subscribe{state.select(it)}
        )
    }

    override fun onPause() {
        super.onPause()
        subscriptions.dispose()
        adapter.clear()
    }

}