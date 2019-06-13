package com.tendebit.dungeonmaster.core.model

/**
 * An [ItemState] is an enumeration of possible states that a given piece of data (or lack thereof) may belong to.
 * The primary functions of the [ItemState] are to take advantage of Kotlin's `when` syntax as well as deal with nullability
 * restrictions (especially in RxJava 2's streams) since the [ItemState] itself is always non-null even if data is absent.
 *
 * The use for each member of the sealed class is intentionally broad so that they may be used in a variety of cases, but
 * the meanings for each are provided in their respective docs
 */
sealed class ItemState<T> {
	open val item: T? = null
}

/**
 * An item is [Loading] if no data has been provided but data is guaranteed to be received
 */
object Loading : ItemState<Nothing>()

/**
 * Similar to a [Loading] state, but there is no guarantee that data will ever be provided
 */
object Undefined : ItemState<Nothing>()

/**
 * An item is [Removed] if data was previously provided but is now gone. The [Removed] state
 * is useful in cases where a user may need to be informed that some data is absent (e.g. if
 * a user was attempting to view an image stored online which has been remotely deleted)
 */
object Removed : ItemState<Nothing>()

/**
 * An item is [Selected] if a user has interacted with it in a context where a selection is required
 * (e.g. a list of character feature choices)
 */
data class Selected<T>(override val item: T) : ItemState<T>()

/**
 * An item is [Disabled] if it cannot be interacted with in the current context
 */
data class Disabled<T>(override val item: T) : ItemState<T>()

/**
 * An item is [Locked] if the same data has a [Selected] status in another context mutually exclusive with
 * the current one. An example would be if a DnD character was selecting a secondary class for multi-class: the
 * original class would be in the [Locked] state, because it cannot be selected again as the secondary class (similar to
 * [Disabled]), but it may need to be displayed with some UI to indicate that it is already selected as opposed to
 * simply not allowed
 */
data class Locked<T>(override val item: T) : ItemState<T>()

/**
 * An item is [Normal] if it is not awaiting any data and user interaction may change the state
 */
data class Normal<T>(override val item: T) : ItemState<T>()

/**
 * An item is [Completed] if all data has been provided and no further user interaction is expected
 */
data class Completed<T>(override val item: T) : ItemState<T>()
