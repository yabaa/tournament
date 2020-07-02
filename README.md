# tournament

Coding test to manage tournament players and scores.

## Technical Stack

* Dropwizard 2.0.10
* Kotlin 1.3.72
* DynamoDB
* Swagger
* Docker
* Gradle
* Nginx

## How to run it?

To run the application, you need first to execute `./gradlew clean build`. And then you can execute `docker-compose up`.

You can also run `./start.sh` which runs both commands for you, and then visit [swagger url](http://localhost:8080/tournament/swagger).

## Application HealthCheck

Application health is visible at `http://localhost:8081/healthcheck`

## Swagger documentation

Swagger documentation is visible at `http://localhost:8080/tournament/swagger`

## Project Structure

### Code
The overall code layout is:

* Code is in `src/main/kotlin`
* Tests are in `src/test/kotlin`

Within these directories things are organised by package:

* DropWizardConfiguration in `com.yabaa.tournament.configuration`
* Endpoints definitions in `com.yabaa.tournament.resources`
* Database configuration (DynamoDB) in `com.yabaa.tournament.database`
* Database HealthCheck in `com.yabaa.tournament.health`
* Data transformation from DynamoDB item to model `com.yabaa.tournament.mapper`
* Entities that are persisted in our database `com.yabaa.tournament.api`
* Interaction with the database `com.yabaa.tournament.daos`
* `TournamentApplication` is the starter point for our Dropwizard application
* `MainApp` is our entry point to run the TournamentApplication

### Resources
The `resources` folder contains:
* Server and Database configuration are in `application.yml`

## Side notes
The actual version of `com.smoketurner:dropwizard-swagger` has an issue with the `org.reflections` project.
You'll see a bunch of warning in the console saying something like 
`WARN org.reflections.Reflections: could not get type for name <className> from any class loader`
But this doesn't affect this project.

