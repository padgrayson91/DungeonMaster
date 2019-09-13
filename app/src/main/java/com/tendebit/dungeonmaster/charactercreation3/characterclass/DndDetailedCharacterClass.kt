package com.tendebit.dungeonmaster.charactercreation3.characterclass

import com.tendebit.dungeonmaster.charactercreation3.proficiencycore.DndProficiency
import com.tendebit.dungeonmaster.charactercreation3.proficiencycore.DndProficiencyGroup
import com.tendebit.dungeonmaster.charactercreation3.proficiencycore.DndProficiencySource

class DndDetailedCharacterClass(val name: String, val id: String, override val dndProficiencyOptions: List<DndProficiencyGroup>,
								val hitDie: Int, val nativeProficiencies: List<DndProficiency>) : DndProficiencySource