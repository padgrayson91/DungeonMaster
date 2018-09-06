package com.tendebit.dungeonmaster.sandbox.model

import com.tendebit.dungeonmaster.charactercreation.pages.classselection.model.CharacterClassManifest

interface SandboxService {

    suspend fun getCharacterClasses() : CharacterClassManifest

}