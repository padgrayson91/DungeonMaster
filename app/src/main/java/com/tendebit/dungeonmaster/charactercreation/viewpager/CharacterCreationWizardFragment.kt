package com.tendebit.dungeonmaster.charactercreation.viewpager

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.tendebit.dungeonmaster.R
import com.tendebit.dungeonmaster.charactercreation.viewpager.adapter.CharacterCreationPagerAdapter
import com.tendebit.dungeonmaster.core.view.BackNavigationHandler
import com.tendebit.dungeonmaster.core.view.LoadingDialog
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.experimental.DefaultDispatcher
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext


class CharacterCreationWizardFragment: Fragment(), BackNavigationHandler {
    private lateinit var adapter: CharacterCreationPagerAdapter
    private lateinit var viewPager: ViewPager
    private lateinit var buttonWrapper: ViewGroup
    private lateinit var backButton: Button
    private lateinit var forwardButton: Button
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var stateFragment: CharacterCreationStateFragment
    private lateinit var subscription: CompositeDisposable
    private var configured = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_generic_viewpager, container, false)
        addStateManagers()
        initializeViews(root)


        return root
    }

    private fun addStateManagers() {
        // TODO: should be a single state fragment with stored states for each page
        stateFragment = addFragmentIfMissing(CharacterCreationStateFragment(), STATE_FRAGMENT_TAG)
    }

    private fun initializeViews(root: View) {
        viewPager = root.findViewById(R.id.view_pager)
        buttonWrapper = root.findViewById(R.id.button_wrapper)
        backButton = root.findViewById(R.id.button_back)
        forwardButton = root.findViewById(R.id.button_forward)
        loadingDialog = root.findViewById(R.id.loading_dialog)
        backButton.setOnClickListener { onBackPressed() }
        viewPager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                stateFragment.state.onPageSelected(position)
                // hide the soft keyboard
                val imm = context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.hideSoftInputFromWindow(view?.windowToken, 0)

            }
        })
        adapter = CharacterCreationPagerAdapter(childFragmentManager)
        viewPager.adapter = adapter
    }

    private fun <T:Fragment> addFragmentIfMissing(fragment: T, tag: String) : T {
        @Suppress("UNCHECKED_CAST")
        val addedFragment = fragmentManager?.findFragmentByTag(tag) as? T
        if (addedFragment == null) {
            fragmentManager?.beginTransaction()
                    ?.add(fragment, tag)
                    ?.commit()
            return fragment
        }
        return addedFragment
    }

    override fun onResume() {
        super.onResume()
        subscription = CompositeDisposable()
        subscription.addAll(stateFragment.state.changes.subscribe{ updateViewFromState(it) })
    }

    override fun onBackPressed() : Boolean {
        if (viewPager.currentItem > 0) {
            viewPager.setCurrentItem(viewPager.currentItem - 1, true)
            return true
        }
        return false
    }

    private fun updateViewFromState(creationState: CharacterCreationState) {
        adapter.update(creationState.pageCollection)
        // ... etc ...
        if (viewPager.currentItem != creationState.currentPage) {
            val previouslyConfigured = configured
            launch(UI) {
                if (previouslyConfigured) withContext(DefaultDispatcher) { Thread.sleep(200) }
                viewPager.setCurrentItem(creationState.currentPage, true)
            }
        }

        val currentPage = creationState.pageCollection.pages[creationState.currentPage]
        backButton.isEnabled = creationState.currentPage != 0
        backButton.visibility = if (creationState.currentPage != 0) View.VISIBLE else View.INVISIBLE
        forwardButton.isEnabled = currentPage.isLastPage || creationState.currentPage < creationState.pageCollection.size - 1
        forwardButton.text = if (currentPage.isLastPage) getString(R.string.confirm) else getString(R.string.next)
        forwardButton.setOnClickListener {
            if (currentPage.isLastPage) creationState.saveCharacter(activity!!)
            else viewPager.setCurrentItem(viewPager.currentItem + 1, true)
        }
        // If neither of the navigation buttons are enabled, hide them
        if (backButton.isEnabled || forwardButton.isEnabled) {
            buttonWrapper.visibility = View.VISIBLE
        } else {
            buttonWrapper.visibility = View.GONE
        }
        // For now, block the whole UI while anything is loading, but in the future
        // the user should still be allowed to interact
        loadingDialog.visibility = if(creationState.isLoading) View.VISIBLE else View.GONE
        configured = true
    }

    override fun onPause() {
        super.onPause()
        subscription.dispose()
    }
}