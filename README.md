android-simpletodo
==================

TODO app for android (AKA learning android).


Architecture
==============

* Views are realised as Fragments.
* The view accesses the (completely anemic) domain model via the android ContentProvider framework.
* The domain model currently only consists of the table definition for the Todo table.


      .---------.        .--------------------------.        .-----------------------------.
      |         |        |   << android >>          |        |    << contentprovider >>    |
      |  Views  |   ---> | Android ContentProvider  |--->    |       infrastructure        |
      .---------.        .--------------------------.        .-----------------------------.
          |                                                            |
         \/                                                     << implements >>
      .---------.                                                      |
      |  domain | /\____>  .--------------------------.                |
      .---------. \/       |   << interface >>        |                |
                           |   Todo Table definition  |  <-------------.
                           .--------------------------.
                           
                           
                           
                           
                           
