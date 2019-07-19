package com.tendebit.dungeonmaster.charactercreation3.race

import com.tendebit.dungeonmaster.charactercreation3.abilitycore.DndAbilityBonus
import com.tendebit.dungeonmaster.charactercreation3.abilitycore.DndAbilitySource

class DndDetailedRace(val origin: DndRace, override val dndAbilityBonuses: Array<DndAbilityBonus>) : DndAbilitySource