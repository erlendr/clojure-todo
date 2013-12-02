clojure-todo
============

### Requirements
- [Leiningen](http://leiningen.org/)

## Installing Leiningen for Windows
- Download and install the [Java SE 7 JDK](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
- Download and install [leiningen-win-installer](http://leiningen-win-installer.djpowell.net/)

## Starting the fleetdb database
Run `java -cp fleetdb-standalone.jar fleetdb.server -f clojure-todo.fdb` from the `fleet-db` directory

## Running the app
Run `lein ring server-headeless`