package com.tendebit.dungeonmaster.charactercreation.viewpager

import android.util.Log
import com.tendebit.dungeonmaster.charactercreation.pages.classselection.model.CharacterClassInfo
import com.tendebit.dungeonmaster.charactercreation.pages.custominfoentry.CustomInfoEntryViewModel
import com.tendebit.dungeonmaster.charactercreation.viewpager.adapter.CharacterCreationPageCollection
import io.reactivex.subjects.BehaviorSubject

/**
 * ViewModel for the current state of the workflow page progression.  Contains a [CharacterCreationPageCollection]
 * which provides specific details about pages and workflow progress
 */
class CharacterCreationPagesViewModel {

    private companion object {
        private val DEFAULT_FIRST_PAGE = CharacterCreationPageDescriptor(
                CharacterCreationPageDescriptor.PageType.CHARACTER_LIST,
                emptyList())
    }

    var pageCollection = CharacterCreationPageCollection(arrayListOf(DEFAULT_FIRST_PAGE))
    val pageChanges = BehaviorSubject.create<CharacterCreationPageCollection>()

    init {
        notifyPagesChanged()
    }

    fun onPageSelected(selection: Int) {
        pageCollection.currentPageIndex = selection
        if (selection == 0) clearPagesStartingAt(1)
        notifyPagesChanged()
    }

    fun performAction(action: PageAction) {
        when(action) {
            PageAction.NAVIGATE_BACK -> onPageSelected(pageCollection.currentPageIndex - 1)
            PageAction.NAVIGATE_FORWARD -> onPageSelected(pageCollection.currentPageIndex + 1)
            PageAction.CONFIRM -> Log.d("CHARACTER_C", "User selected save character") // TODO
        }
    }

    fun startNewCharacterCreation() {
        clearPagesAfter(CharacterCreationPageDescriptor.PageType.CHARACTER_LIST)
        val actions = arrayListOf(PageAction.NAVIGATE_BACK, PageAction.NAVIGATE_FORWARD)
        addPage(CharacterCreationPageDescriptor(CharacterCreationPageDescriptor.PageType.RACE_SELECTION, actions))
        pageCollection.currentPageIndex = findStartOfGroup(CharacterCreationPageDescriptor.PageType.RACE_SELECTION)
        notifyPagesChanged()
    }

    fun switchToSavedCharacterPage() {
        clearPagesAfter(CharacterCreationPageDescriptor.PageType.CHARACTER_LIST)
        val actions = arrayListOf(PageAction.NAVIGATE_BACK)
        addPage(CharacterCreationPageDescriptor(CharacterCreationPageDescriptor.PageType.CONFIRMATION, actions))
        pageCollection.currentPageIndex = findStartOfGroup(CharacterCreationPageDescriptor.PageType.CONFIRMATION)
        notifyPagesChanged()
    }

    fun handleProficiencyStatusChange(isComplete: Boolean, isCustomInfoComplete: Boolean) {
        // If all proficiencies are selected and the next page hasn't been added already
        val actions = arrayListOf(PageAction.NAVIGATE_BACK, PageAction.NAVIGATE_FORWARD)
        if (isComplete) {
            addPage(
                    CharacterCreationPageDescriptor(CharacterCreationPageDescriptor.PageType.CUSTOM_INFO, actions)
            )
            if (isCustomInfoComplete) {
                // user has info from before that allows them to proceed to confirmation screen
                addPage(
                        CharacterCreationPageDescriptor(CharacterCreationPageDescriptor.PageType.CONFIRMATION,
                                actions)
                )
            }
        } else {
            clearPagesAfter(CharacterCreationPageDescriptor.PageType.PROFICIENCY_SELECTION)
        }
        notifyPagesChanged()
    }

    fun handleCustomDataChanged(viewModel: CustomInfoEntryViewModel) {
        if (viewModel.isEntryComplete() && pageCollection.pages[pageCollection.size - 1].type
                != CharacterCreationPageDescriptor.PageType.CONFIRMATION) {
            val actions = arrayListOf(PageAction.NAVIGATE_BACK, PageAction.CONFIRM)
            addPage(
                    CharacterCreationPageDescriptor(CharacterCreationPageDescriptor.PageType.CONFIRMATION,
                            actions)
            )
            notifyPagesChanged()
        } else if(!viewModel.isEntryComplete() && pageCollection.pages[pageCollection.size - 1].type
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
                                CharacterCreationPageDescriptor.PageType.PROFICIENCY_SELECTION, actions, i))
            }
        }
        pageCollection.currentPageIndex = findStartOfGroup(CharacterCreationPageDescriptor.PageType.PROFICIENCY_SELECTION)
        notifyPagesChanged()
    }

    fun handleCharacterRaceSelected(isNew: Boolean) {
        if (isNew) {
            if (findStartOfGroup(CharacterCreationPageDescriptor.PageType.CLASS_SELECTION) == -1) {
                val actions = arrayListOf(PageAction.NAVIGATE_BACK, PageAction.NAVIGATE_FORWARD)
                addPage(
                        CharacterCreationPageDescriptor(
                                CharacterCreationPageDescriptor.PageType.CLASS_SELECTION, actions))
            }
        }
        pageCollection.currentPageIndex = findStartOfGroup(CharacterCreationPageDescriptor.PageType.CLASS_SELECTION)
        notifyPagesChanged()
    }

    private fun notifyPagesChanged() {
        pageChanges.onNext(pageCollection)
    }

    private fun clearPagesStartingAt(index: Int) {
        if (index >= pageCollection.size || index < 0) return
        val pagesToKeep = pageCollection.pages.subList(0, index)
        val previousIndex = pageCollection.currentPageIndex
        pageCollection = CharacterCreationPageCollection(pagesToKeep)
        pageCollection.currentPageIndex = if (previousIndex >= pageCollection.size) {
            pageCollection.size
        } else previousIndex
    }

    private fun clearPagesAfter(pageType: CharacterCreationPageDescriptor.PageType) {
        val startIndex = findStartOfGroup(pageType)
        var index = -1
        if (startIndex >= 0) {
            for (i in startIndex until pageCollection.size) {
                if (pageCollection.pages[i].type != pageType) {
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
            if (pageCollection.pages[i].type == pageType) {
                index = i
                break
            }
        }
        return index
    }

    private fun addPage(pageDescriptor: CharacterCreationPageDescriptor) {
        val updatedPages = ArrayList(pageCollection.pages)
        updatedPages.add(pageDescriptor)
        val previousIndex = pageCollection.currentPageIndex
        pageCollection = CharacterCreationPageCollection(updatedPages)
        pageCollection.currentPageIndex = previousIndex
    }
}