package com.tendebit.dungeonmaster.core.blueprint

import com.tendebit.dungeonmaster.core.blueprint.requirement.Requirement
import io.reactivex.Observable

interface IBlueprint<StateType> {
	val requirements: Observable<List<Delta<Requirement<*>>>>
	val state: StateType
}