# tournament

Coding test to manage tournament players and scores.

## Technical Stack

* Dropwizard 2.0.10
* Kotlin 1.3.72
* MongoDB
* Docker

## How to run it?

TODO

## Project Structure

### Code
The overall code layout is:

* Code is in `src/main/kotlin`
* Tests are in `src/test/kotlin`

Within these directories things are organised by package:

* DropWizardConfiguration in `com.yabaa.tournament.configuration`
* Endpoints definitions in `com.yabaa.tournament.controller`
* Database configuration (MongoDB) in `com.yabaa.tournament.database`
* Database HealthCheck in `com.yabaa.tournament.health`
* Data transformation from MongoDB document to model `com.yabaa.tournament.mapper`
* Entities that are persisted in our database `com.yabaa.tournament.model`
* Interaction with the database `com.yabaa.tournament.repository`
* `TournamentApplication` is the starter point for our Dropwizard application
* `MainApp` is our entry point to run the TournamentApplication

### Resources
The `resources` folder contains:
* Server and Database configuration are in `application.yml`
* Default MongoDB user creation script in `init-mongo.js`

