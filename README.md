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
* Use of headless fragments to maintain UI state rather than using the more traditional Bundle-based model
* Koin Dependency Injection (this may negate the above use of headless fragments in the long term)

The following items are on the roadmap to include in future development

- [X] Unit tests
- [ ] UI tests using Barista/Espresso

### MVVM Implementation

My current MVVM implementation is built from the ground up rather than leveraging the `ViewModel` from Android Architecture Components.  The main reason for this is that I have not gotten a chance to use MVVM architecture at a large scale, and I want to have a solid understanding of what a custom solution would entail before leveraging the library.  This will allow me to identify how much if any convenience is added by using the Android Architecture Components version as well as what benefits and drawbacks there are when compared to my tailored solution (e.g. are certain behaviors not achievable with the library, does the library promote better or worse code style)

### Blueprint

As of mid-January 2019, I have begun experimenting with a new architecture for my model classes which I am calling *Blueprint*. Because the app relies so heavily on input forms, I was running into an issue where my ViewModel needed to do a ton of work to keep track of what forms to show and in what order based on the current state.
As my ViewModels became more complicated, I decided I needed to rethink the approach. At first pass, it might seem like the ordering of a form is the role of the View or ViewModel (it seemed that way to me anyway), but given that the collection of data to build out a character/monster/location etc. is the core functionality of the app, it is
my opinion that this falls firmly into the territory of the model. The model still doesn't need to know anything about the way this data is provided, and for all it cares the data could be coming not from a UI but from some AI (or more likely a unit test), but the data that is required and the order in which it must be given
is something the model cares about. The ViewModel still gets to decide what sort of View should be used to render a certain request, and the View itself still gets to decide what that looks like when it is sitting on the users screen, but neither needs to care about what happens after a piece of information is provided; the model
should tell them that.

Blueprint allows me to emit requests for information as an ordered list, including those which have already been satisfied, only some of which will need to be rendered by the UI. I have separated this process into 4 main components detailed below

## Requirement

A `Requirement` represents an actual request for information, which can exist in a fixed number of states (currently `FULFILLED` or `NOT_FULFILLED`, but at least one additional state will be needed for data that is being provided by some asynchronous means).  As mentioned above this request may or may not require user input (e.g. a
database read or API call would not), but regardless the UI would probably need to make some indication of what is going on.  A `Requirement` has a type parameter for the data type it is expecting, and it holds a nullable reference to an object of that type which represents information that has previously been provided to satisfy the
requirement. Specific requirements may contain additional data about the request for information: for example, if a request asks the user to choose a single item from a list, the list of options would be part of the requirement. The `Requirement` does not need to know anything about the broader application state.

## Fulfillment

A `Fulfillment` has the role of updating the current application state based on the information from a `Requirement`. A `Fulfillment` in general doesn't need to be tied to any one implementation of a requirement, but needs to be tied to the specific data type for the requirement and
know how to add/remove items of that data type from the application state when the `Requirement` status changes.

## Examiner

An `Examiner` observes the current application state and determines what `Fulfilment` items are needed based upon that. Currently I'm leaving this somewhat loose, with an `Examiner` able to emit more than one `Fulfillment` type, but trying to make sensible separations for what a single examiner should be looking for. For example, if we were constructing
a house, it might make sense for a single `Examiner` to emit one `Fulfillment` for the lot size and a second `Fulfillment` for the house size, since these are related items which would not likely be needed independently, but it would probably be best to use a separate `Examiner` to emit a `Fulfillment` for the street address, which might need to be swapped
if the house building application were used outside the US and required a different address format. An `Examiner` also has the role of determining whether or not the items it just emitted should force a halt on querying other `Examiner`s until the next state change.  The intent of this is to allow an `Examiner` to tell us "don't bother even checking for
other `Requirement`s until these ones are done, because there's going to be no way to fulfill them".

## Blueprint

A `Blueprint` is effectively a collection of `Examiner`, and it is the responsibility of the `Blueprint` to retain state information and query `Examiner`s when something changes in order to get the most up-to-date list of `Fulfillment`. The `Blueprint` then combines all `Requirement`s into a single list, which is all that gets exposed to downstream application
components. In this way the ViewModel (or Presenter, or Controller) can treat the `Blueprint` as a black box that emits `Requirement`s


