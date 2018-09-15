package com.tendebit.dungeonmaster.charactercreation.pages.custominfoentry

import com.tendebit.dungeonmaster.charactercreation.pages.custominfoentry.model.CustomInfo
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

/**
 * ViewModel for character biographical information entry which exposes methods to update various
 * aspects of the biographical info
 */
class CustomInfoEntryViewModel {
    private val stateSubject = BehaviorSubject.create<CustomInfoEntryViewModel>()
    val changes = stateSubject as Observable<CustomInfoEntryViewModel>
    val info = CustomInfo()


    fun isEntryComplete() : Boolean {
        return info.isComplete()
    }

    fun setName(name: CharSequence?) {
        info.name = name
        notifyDataChanged()
    }

    fun setHeightFeet(feet: Int) {
        info.heightFeet = feet
        notifyDataChanged()
    }

    fun setHeightInches(inches: Int) {
        info.heightInches = inches
        notifyDataChanged()
    }

    fun setWeight(weight: CharSequence?) {
        if (weight != null && !weight.isEmpty()) {
            info.weight = weight.toString().toInt()
        } else {
            info.weight = null
        }
        notifyDataChanged()
    }



    private fun notifyDataChanged() {
        stateSubject.onNext(this)
    }


}