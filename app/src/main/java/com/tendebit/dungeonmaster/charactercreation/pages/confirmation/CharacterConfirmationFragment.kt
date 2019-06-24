package com.tendebit.dungeonmaster.charactercreation.pages.confirmation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tendebit.dungeonmaster.R
import com.tendebit.dungeonmaster.charactercreation.pages.proficiencyselection.model.CharacterProficiencyDirectory
import com.tendebit.dungeonmaster.core.view.adapter.SimpleElementAdapter

/**
 * UI Fragment for character confirmation/review
 */
class CharacterConfirmationFragment : Fragment() {
    private lateinit var characterNameText: TextView
    private lateinit var raceNameText: TextView
    private lateinit var classNameText: TextView
    private lateinit var heightText: TextView
    private lateinit var weightText: TextView
    private val adapter = SimpleElementAdapter<CharacterProficiencyDirectory>()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_character_confirmation, container, false)
        characterNameText = root.findViewById(R.id.character_name_txt)
        raceNameText = root.findViewById(R.id.race_name_txt)
        classNameText = root.findViewById(R.id.class_name_txt)
        heightText = root.findViewById(R.id.height_value)
        weightText = root.findViewById(R.id.weight_value)

        val recycler = root.findViewById<RecyclerView>(R.id.proficiency_list)
        recycler.layoutManager = LinearLayoutManager(activity)
        recycler.adapter = adapter
        return root
    }

}