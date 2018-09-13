package com.tendebit.dungeonmaster.charactercreation.pages.custominfoentry

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputLayout
import com.tendebit.dungeonmaster.R
import com.tendebit.dungeonmaster.charactercreation.pages.custominfoentry.model.CustomInfo
import com.tendebit.dungeonmaster.charactercreation.viewpager.CharacterCreationStateFragment
import com.tendebit.dungeonmaster.charactercreation.viewpager.STATE_FRAGMENT_TAG

class CustomInfoEntryFragment : Fragment() {
    private lateinit var nameEntry: TextInputLayout
    private lateinit var heightFeetEntry: NumberPicker
    private lateinit var heightInchesEntry: NumberPicker
    private lateinit var weightEntry: TextInputLayout
    private lateinit var stateFragment: CharacterCreationStateFragment

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_custom_character_info, container, false)
        nameEntry = root.findViewById(R.id.name_entry)
        heightFeetEntry = root.findViewById(R.id.height_entry_feet)
        heightFeetEntry.maxValue = CustomInfo.MAX_HEIGHT_FEET
        heightFeetEntry.minValue = CustomInfo.MIN_HEIGHT_FEET
        heightInchesEntry = root.findViewById(R.id.height_entry_inches)
        heightInchesEntry.maxValue = CustomInfo.MAX_HEIGHT_INCHES
        heightInchesEntry.minValue = CustomInfo.MIN_HEIGHT_INCHES
        weightEntry = root.findViewById(R.id.weight_entry)

        val addedFragment = activity!!.supportFragmentManager.findFragmentByTag(STATE_FRAGMENT_TAG)
        if (addedFragment is CharacterCreationStateFragment) {
            stateFragment = addedFragment
            updateViewFromState(stateFragment.state.customInfoState)
        } else {
            throw IllegalStateException(CustomInfoEntryFragment::class.java.simpleName + " expected a state provider")
        }
        return root
    }

    private fun updateViewFromState(state: CustomInfoEntryState) {
        val info = state.info
        nameEntry.editText?.setText(info.name)
        heightFeetEntry.value = info.heightFeet
        heightInchesEntry.value = info.heightInches
        weightEntry.editText?.setText(info.weight?.toString())

        nameEntry.editText?.addTextChangedListener(object : SimpleTW() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                state.setName(s)
            }
        })
        heightFeetEntry.setOnValueChangedListener { _, _, newVal ->  state.setHeightFeet(newVal) }
        heightInchesEntry.setOnValueChangedListener { _, _, newVal -> state.setHeightInches(newVal) }
        weightEntry.editText?.addTextChangedListener(object : SimpleTW() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                state.setWeight(s)
            }
        })

    }

    private abstract class SimpleTW : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    }
}