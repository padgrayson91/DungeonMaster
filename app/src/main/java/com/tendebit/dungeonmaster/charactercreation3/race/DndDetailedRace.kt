package com.tendebit.dungeonmaster.charactercreation3.race

import com.tendebit.dungeonmaster.charactercreation3.abilitycore.DndAbilityBonus
import com.tendebit.dungeonmaster.charactercreation3.abilitycore.DndAbilitySource
import com.tendebit.dungeonmaster.charactercreation3.proficiencycore.DndProficiency
import com.tendebit.dungeonmaster.charactercreation3.proficiencycore.DndProficiencyGroup
import com.tendebit.dungeonmaster.charactercreation3.proficiencycore.DndProficiencySource

class DndDetailedRace(val origin: DndRace, override val dndAbilityBonuses: Array<DndAbilityBonus>,
					  override val dndProficiencyOptions: List<DndProficiencyGroup>, val nativeProficiencies: List<DndProficiency>) : DndAbilitySource, DndProficiencySource