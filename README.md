# Dungeon Master
Android App for Dungeons and Dragons (or other tabeltop RPGs)

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

The following items are on the roadmap to include in future development

* Dependency Injection (I have worked with Dagger and Dagger2 in the past, but I am considering Koin for this project)
* Unit tests
* UI tests using Barista/Espresso

### MVVM Implementation

My current MVVM implementation is built from the ground up rather than levaraging the `ViewModel` from Android Architecture Components.  The main reason for this is that I have not gotten a chance to use MVVM architecture at a large scale, and I want to have a solid understanding of what a custom solution would entail before levaraging the library.  This will allow me to identify how much if any convenience is added by using the Android Architecture Components version as well as what benefits and drawbacks there are when compared to my tailored solution (e.g. are certain behaviors not achievable with the library, does the library promote better or worse code style) 

