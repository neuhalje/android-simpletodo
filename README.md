android-simpletodo
==================

[![Build Status](https://travis-ci.org/neuhalje/android-simpletodo.svg?branch=master)](https://travis-ci.org/neuhalje/android-simpletodo)

TODO app for android (AKA learning android and learning scala).

Gradle project structure
===========================

![Structural](website/gradle-projects.png "Structure of the buildfiles")

Architecture
==============

![Structural](website/structural.png "Structure of the application")


Validation
-----------

No validation yet.

Dependency Injection
--------------------

Domain, and android app use `@Inject` to mark injectable objects. The DI implementation used in the app is [Dagger](http://square.github.io/dagger/). The DI implementation used by the domain tests (scala) is [Guice](https://code.google.com/p/google-guice/), as Dagger won't work with scala.

Communication
----------------

### View internal
Fragments rely on callbacks to communicate with the hosting activity.

### Commands
Views send commands to the domain.

### Queries
Views query the ContentProvider abstraction.

### Events
* The content provider publishes some events (e.g. changes to the view table). These are handled by the views themselves.
* The domain (the implementation in the android project) posts events to a global eventbus

Releases
===========
- currently undecided on how to release.

Branching & Feature Developement
---------------------

Is done via [gitflow](http://nvie.com/posts/a-successful-git-branching-model/). Also look [here](http://danielkummer.github.io/git-flow-cheatsheet/) for a nice cheat sheet.
                           
                           
