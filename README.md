android-simpletodo
==================

[![Build Status](https://travis-ci.org/neuhalje/android-simpletodo.svg?branch=master)](https://travis-ci.org/neuhalje/android-simpletodo)

TODO app for android (AKA learning android).


Architecture
==============

* Views are realised as Fragments.
* The view accesses the (completely anemic) domain model via the android ContentProvider framework.
* The domain model currently only consists of the table definition for the Todo table.

```text
      .---------.        .--------------------------.        .-----------------------------.
      |         |        |   << android >>          |        |    << contentprovider >>    |
      |  Views  |   ---> | Android ContentProvider  |--->    |       infrastructure        |
      .---------.        .--------------------------.        .-----------------------------.
          |                                                            |
         \/                                                     << implements >>
      .---------.                                                      |
      |  domain | /\____\  .--------------------------.                |
      .---------. \/    /  |   << interface >>        |                |
                           |   Todo Table definition  |  <-------------.
                           .--------------------------.
```
                           
Communication
===============

### View internal
Fragments rely on callbacks to communicate with the hosting activity.

### Commands
Views send commands to the domain (actually it is implemented in the content provider implementation) using the ContentProvider abstraction.

### Queries
Views query the ContentProvider abstraction.

### Events
* The content provider publishes some events (e.g. changes to the table).
* The domain (see above) posts events to a global eventbus
                           
                           
                           
