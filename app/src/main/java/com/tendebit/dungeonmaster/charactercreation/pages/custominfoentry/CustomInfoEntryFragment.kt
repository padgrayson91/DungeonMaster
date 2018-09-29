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
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

/**
 * UI Fragment for enter character biographical information
 */
class CustomInfoEntryFragment : Fragment() {
    private lateinit var nameEntry: TextInputLayout
    private lateinit var heightFeetEntry: NumberPicker
    private lateinit var heightInchesEntry: NumberPicker
    private lateinit var weightEntry: TextInputLayout
    private val viewModel: CustomInfoEntryViewModel by inject("newOrExisting") { parametersOf(this) }

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

        updateViewFromViewModel(viewModel)
        return root
    }

    private fun updateViewFromViewModel(viewModel: CustomInfoEntryViewModel) {
        val info = viewModel.info
        nameEntry.editText?.setText(info.name)
        heightFeetEntry.value = info.heightFeet
        heightInchesEntry.value = info.heightInches
        weightEntry.editText?.setText(info.weight?.toString())

        nameEntry.editText?.addTextChangedListener(object : SimpleTW() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.setName(s)
            }
        })
        heightFeetEntry.setOnValueChangedListener { _, _, newVal ->  viewModel.setHeightFeet(newVal) }
        heightInchesEntry.setOnValueChangedListener { _, _, newVal -> viewModel.setHeightInches(newVal) }
        weightEntry.editText?.addTextChangedListener(object : SimpleTW() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.setWeight(s)
            }
        })

    }

    private abstract class SimpleTW : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    }
}