# Dungeon Master
Android App for Dungeons and Dragons (or other tabletop RPGs)

## About the App

For those unfamiliar with tabletop RPGs, the short explanation with respect to this project is that they involve a lot of bookkeeping.  A game is generally run by a single individual (known as a "Dungeon Master" in Dungeons and Dragons), and that individual is responsible for creating/managing all kinds of game data, such as storylines, a map of the playable area, lists of creatures that populate the area, and even the items and buildings with which those creatures can interact.  The players of the game then create their own characters, and the Dungeon Master reveals information about the in-game world as the players make decisions to progress through the storyline.  The app is intended to help supplement this process for both players and Dungeon Masters by providing various user interfaces to create and manage in-game content as well as connect to web APIs to access pre-made content that is used for certain games (e.g. the types of weapons that are available to a player). 

This project is very much a work-in-progress.  There are certainly other apps out there that can do similar things, but I feel that this project is a good sandbox for working with various Android libraries as well as architecture designs that I don't necessarily get to use for my professional projects.  So far the app only contains a simple character creation tool and connects to a single API, an open source project which can be found [here](https://github.com/adrpadua/5e-srd-api), but as features are added I'll keep this doc updated with explanations of the features and the underlying technologies.

## Technologies Used

So far I'm focusing on the following

* Kotlin
* Kotlin coroutines
* RxJava2
* Room Database
* Model View ViewModel architecture (MVVM)
* Feature-based package structure (As opposed to layer-based)
* Koin Dependency Injection

The following items are on the roadmap to include in future development

- [X] Unit tests
- [ ] UI tests using Barista/Espresso
- [ ] Use `@Parcelize` annotation from Kotlin Android Extensions to eliminate some `Parcelable` boilerplate
- [ ] Kotlin Flow (to potentially replace RxJava2)

### MVVM Implementation

My current MVVM implementation is built from the ground up rather than leveraging the `ViewModel` from Android Architecture Components.  The main reason for this is that I have not gotten a chance to use MVVM architecture at a large scale, and I want to have a solid understanding of what a custom solution would entail before leveraging the library.  This will allow me to identify how much if any convenience is added by using the Android Architecture Components version as well as what benefits and drawbacks there are when compared to my tailored solution (e.g. are certain behaviors not achievable with the library, does the library promote better or worse code style)

### Third Try is the Charm

I have iterated over 2 variations of architecture for the app and am now working on a third. While still a MVVM architecture, I believe this third implementation is cleaner
as it removes a few logic items from the view layer. The defining characteristic of this new architecture is the use of the `ItemState` sealed class

`ItemState` serves as a global wrapper for any model object which may change states through various interactions with the application. While the state of the model could
have been stored internally within the model itself, I believe the use of a wrapper had the following advantages:

* Avoids a bunch of nullable fields for models where data is not yet provided, instead using a wrapper which is guaranteed non-null
where the sealed class member used indicates whether the model itself is null (e.g. in the `Loading` state we know the model is null, but in a `Normal` state
we know it is non-null. Then the fields within the model can be made non-nullable
* A non-nullable wrapper means that there are no issues with the nullability restrictions in RxJava2 streams
* Draws a clear distinction so that only parent models, which have greater contextual understanding, control the states of their children
rather than children controlling their own state. An example illustrating why I find this is useful is a mutually exclusive selection
(such as a radio group): each child in the group has a state (selected or not selected), but because changing the selection state from one child impacts
its siblings, I believe it makes most sense for the parent, which understands the full context, to have control over all state changes

My ViewModel classes can then take these `ItemState` objects + the models they wrap and convert them to values which are directly consumed by the View.
For example a `Selected` state might use a different text color than a `Normal` state, and the ViewModel would handle that logic