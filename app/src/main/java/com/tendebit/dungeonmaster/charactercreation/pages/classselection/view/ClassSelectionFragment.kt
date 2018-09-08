package com.tendebit.dungeonmaster.charactercreation.pages.classselection.view

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
import com.tendebit.dungeonmaster.charactercreation.pages.classselection.view.statefragment.CLASS_SELECTION_FRAGMENT_TAG
import com.tendebit.dungeonmaster.charactercreation.pages.classselection.view.statefragment.ClassSelectionStateFragment
import com.tendebit.dungeonmaster.charactercreation.pages.classselection.viewmodel.ClassSelectionState
import com.tendebit.dungeonmaster.core.view.adapter.SelectionElementAdapter
import io.reactivex.disposables.CompositeDisposable


class ClassSelectionFragment : Fragment() {

    private lateinit var subscriptions: CompositeDisposable
    private lateinit var recycler: RecyclerView
    private lateinit var stateProvider: ClassSelectionStateFragment
    private val adapter = SelectionElementAdapter<CharacterClassDirectory, CharacterClassInfo>(null)

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
        val addedFragment = activity?.supportFragmentManager?.findFragmentByTag(CLASS_SELECTION_FRAGMENT_TAG)
        if (addedFragment is ClassSelectionStateFragment) {
            stateProvider = addedFragment
        } else {
            throw IllegalStateException(ClassSelectionFragment::class.java.simpleName + " expects a state manager to be provided")
        }
        subscriptions.addAll(
                stateProvider.state.changes.subscribe{updateViewFromState(it)},
                adapter.itemClicks.distinctUntilChanged().subscribe{stateProvider.state.select(it)}
        )
    }

    private fun pageExit() {
        subscriptions.dispose()
    }


    private fun updateViewFromState(state: ClassSelectionState) {
        if (state.options.size > 0) {
            adapter.update(state)
        }
    }
}