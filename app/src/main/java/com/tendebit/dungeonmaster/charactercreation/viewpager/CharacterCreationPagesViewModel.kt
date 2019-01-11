package com.tendebit.dungeonmaster.charactercreation.viewpager

import android.util.Log
import com.tendebit.dungeonmaster.charactercreation.CharacterCreationViewModel.Companion.TAG_CHARACTER_LIST
import com.tendebit.dungeonmaster.charactercreation.CharacterCreationViewModel.Companion.TAG_CLASS_LIST
import com.tendebit.dungeonmaster.charactercreation.CharacterCreationViewModel.Companion.TAG_CONFIRMATION
import com.tendebit.dungeonmaster.charactercreation.CharacterCreationViewModel.Companion.TAG_CUSTOM_ENTRY
import com.tendebit.dungeonmaster.charactercreation.CharacterCreationViewModel.Companion.TAG_PROFICIENCY_SELECTION
import com.tendebit.dungeonmaster.charactercreation.CharacterCreationViewModel.Companion.TAG_RACE_LIST
import com.tendebit.dungeonmaster.charactercreation.CharacterCreationViewModel.Companion.TAG_REVIEW
import com.tendebit.dungeonmaster.charactercreation.pages.classselection.model.CharacterClassInfo
import com.tendebit.dungeonmaster.charactercreation.pages.custominfoentry.CustomInfoEntryViewModel
import com.tendebit.dungeonmaster.core.model.AsyncViewModel
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * ViewModel for the current state of the workflow page progression.  Contains a list of [CharacterCreationPageDescriptor]
 * which provides specific details about pages and an index of the currently selected page
 */
class CharacterCreationPagesViewModel : AsyncViewModel {

    private companion object {

        private val DEFAULT_FIRST_PAGE = CharacterCreationPageDescriptor(
                CharacterCreationPageDescriptor.PageType.CHARACTER_LIST,
                emptyList(), viewModelTag = TAG_CHARACTER_LIST)
    }

    var currentPageIndex = 0
    override var activeAsyncCalls = 0
    val pageCollection = arrayListOf(DEFAULT_FIRST_PAGE)
    val indexChanges = BehaviorSubject.create<Int>()
    val pageChanges = BehaviorSubject.create<List<CharacterCreationPageDescriptor>>()
    val clearedPages = PublishSubject.create<List<CharacterCreationPageDescriptor>>()
    private val delayJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + delayJob)
    override val asyncCallChanges = PublishSubject.create<Int>()

    init {
        notifyPagesChanged()
        notifyIndexChangedImmediate()
    }

    override fun onDetach() {}

    fun onPageSelected(selection: Int) {
        currentPageIndex = selection
        // When we return to the beginning of the workflow, clear the rest (there is definitely a better way to do this)
        if (selection == 0) {
            clearPagesStartingAt(1)
            notifyPagesChanged()
        }
        notifyIndexChangedImmediate()
    }

    fun performAction(action: PageAction) {
        when(action) {
            PageAction.NAVIGATE_BACK -> onPageSelected(currentPageIndex - 1)
            PageAction.NAVIGATE_FORWARD -> onPageSelected(currentPageIndex + 1)
            PageAction.CONFIRM -> Log.d("CHARACTER_C", "User selected save character") // TODO
        }
    }

    fun resetPages() {
        clearPagesStartingAt(pageCollection.indexOf(DEFAULT_FIRST_PAGE) + 1, false)
        notifyPagesChanged()
    }

    fun startNewCharacterCreation() {
        clearPagesAfter(CharacterCreationPageDescriptor.PageType.CHARACTER_LIST)
        val actions = arrayListOf(PageAction.NAVIGATE_BACK, PageAction.NAVIGATE_FORWARD)
        addPage(CharacterCreationPageDescriptor(CharacterCreationPageDescriptor.PageType.RACE_SELECTION, actions,
                viewModelTag = TAG_RACE_LIST))
        currentPageIndex = findStartOfGroup(CharacterCreationPageDescriptor.PageType.RACE_SELECTION)
        notifyPagesChanged()
        notifyIndexChangeDelayed()
    }

    fun switchToSavedCharacterPage() {
        clearPagesAfter(CharacterCreationPageDescriptor.PageType.CHARACTER_LIST)
        val actions = arrayListOf(PageAction.NAVIGATE_BACK)
        addPage(CharacterCreationPageDescriptor(CharacterCreationPageDescriptor.PageType.CONFIRMATION, actions,
                viewModelTag = TAG_REVIEW))
        currentPageIndex = findStartOfGroup(CharacterCreationPageDescriptor.PageType.CONFIRMATION)
        notifyPagesChanged()
        notifyIndexChangedImmediate()
    }

    fun handleProficiencyStatusChange(isComplete: Boolean, isCustomInfoComplete: Boolean) {
        // If all proficiencies are selected and the next page hasn't been added already
        val actions = arrayListOf(PageAction.NAVIGATE_BACK, PageAction.NAVIGATE_FORWARD)
        if (isComplete) {
            addPage(
                    CharacterCreationPageDescriptor(CharacterCreationPageDescriptor.PageType.CUSTOM_INFO, actions,
                            viewModelTag = TAG_CUSTOM_ENTRY)
            )
            if (isCustomInfoComplete) {
                // user has info from before that allows them to proceed to confirmation screen
                addPage(
                        CharacterCreationPageDescriptor(CharacterCreationPageDescriptor.PageType.CONFIRMATION,
                                actions, viewModelTag = TAG_CONFIRMATION)
                )
            }
        } else {
            clearPagesAfter(CharacterCreationPageDescriptor.PageType.PROFICIENCY_SELECTION)
        }
        notifyPagesChanged()
    }

    fun handleCustomDataChanged(viewModel: CustomInfoEntryViewModel) {
        if (viewModel.isEntryComplete() && pageCollection[pageCollection.size - 1].type
                != CharacterCreationPageDescriptor.PageType.CONFIRMATION) {
            val actions = arrayListOf(PageAction.NAVIGATE_BACK, PageAction.CONFIRM)
            addPage(
                    CharacterCreationPageDescriptor(CharacterCreationPageDescriptor.PageType.CONFIRMATION,
                            actions, viewModelTag = TAG_CONFIRMATION)
            )
            notifyPagesChanged()
        } else if(!viewModel.isEntryComplete() && pageCollection[pageCollection.size - 1].type
                == CharacterCreationPageDescriptor.PageType.CONFIRMATION) {
            clearPagesStartingAt(pageCollection.size - 1)
            notifyPagesChanged()
        }
    }

    fun handleCharacterClassSelected(selection: CharacterClassInfo, isNew: Boolean) {
        // Only clear pages if the selection actually changed
        if (isNew) {

            clearPagesAfter(CharacterCreationPageDescriptor.PageType.CLASS_SELECTION)
            val actions = arrayListOf(PageAction.NAVIGATE_BACK, PageAction.NAVIGATE_FORWARD)
            for (i in 0 until selection.proficiencyChoices.size) {
                addPage(
                        CharacterCreationPageDescriptor(
                                CharacterCreationPageDescriptor.PageType.PROFICIENCY_SELECTION, actions, i,
                                TAG_PROFICIENCY_SELECTION))
            }
        }
        currentPageIndex = findStartOfGroup(CharacterCreationPageDescriptor.PageType.PROFICIENCY_SELECTION)
        notifyPagesChanged()
        notifyIndexChangedImmediate()
    }

    fun handleCharacterRaceSelected(isNew: Boolean) {
        if (isNew && findStartOfGroup(CharacterCreationPageDescriptor.PageType.CLASS_SELECTION) == -1) {
                val actions = arrayListOf(PageAction.NAVIGATE_BACK, PageAction.NAVIGATE_FORWARD)
                addPage(
                        CharacterCreationPageDescriptor(
                                CharacterCreationPageDescriptor.PageType.CLASS_SELECTION, actions,
                                viewModelTag = TAG_CLASS_LIST))
                currentPageIndex = findStartOfGroup(CharacterCreationPageDescriptor.PageType.CLASS_SELECTION)
                notifyPagesChanged()
                notifyIndexChangeDelayed()
        } else {
            currentPageIndex = findStartOfGroup(CharacterCreationPageDescriptor.PageType.CLASS_SELECTION)
            notifyPagesChanged()
            notifyIndexChangedImmediate()
        }
    }

    private fun notifyPagesChanged() {
        pageChanges.onNext(pageCollection)
    }

    private fun notifyIndexChangedImmediate()  {
        indexChanges.onNext(currentPageIndex)
    }

    private fun notifyIndexChangeDelayed() {
        uiScope.launch {
            onAsyncCallStart()
            // TODO: rather than a fixed delay, this should be waiting on a callback indicating the
            // target page is added to the viewpager
            runBlocking { Thread.sleep(500) }
            indexChanges.onNext(currentPageIndex)
            onAsyncCallFinish()
        }
    }

    private fun clearPagesStartingAt(index: Int, shouldDelayIndexChange: Boolean = false) {
        if (index >= pageCollection.size || index < 0) return
        val pagesToKeep =
                ArrayList(pageCollection.subList(0, index))
        val discardedPages =
                ArrayList(pageCollection.subList(index, pageCollection.size))
        clearedPages.onNext(discardedPages)
        pageCollection.clear()
        pageCollection.addAll(pagesToKeep)
        if (currentPageIndex >= pageCollection.size) {
            currentPageIndex = pageCollection.size - 1
            if (shouldDelayIndexChange) notifyIndexChangeDelayed()
            else notifyIndexChangedImmediate()
        }
    }

    private fun clearPagesAfter(pageType: CharacterCreationPageDescriptor.PageType) {
        val startIndex = findStartOfGroup(pageType)
        var index = -1
        if (startIndex >= 0) {
            for (i in startIndex until pageCollection.size) {
                if (pageCollection[i].type != pageType) {
                    index = i
                    break
                }
            }
        }
        clearPagesStartingAt(index)
    }

    private fun findStartOfGroup(pageType: CharacterCreationPageDescriptor.PageType) : Int {
        var index = -1
        for (i in 0 until pageCollection.size) {
            if (pageCollection[i].type == pageType) {
                index = i
                break
            }
        }
        return index
    }

    private fun addPage(pageDescriptor: CharacterCreationPageDescriptor) {
        pageCollection.add(pageDescriptor)
    }
}