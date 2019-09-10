package com.tendebit.dungeonmaster.charactercreation3.proficiencycore

import java.io.Serializable

/**
 * Represents a single proficiency which may apply to a DnD character
 */
data class DndProficiency(val name: String, val identifier: String) : Serializable