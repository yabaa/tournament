package com.yabaa.tournament

import com.yabaa.tournament.configuration.TournamentApplicationConfiguration
import com.yabaa.tournament.controller.PlayerController
import com.yabaa.tournament.database.MongoDBConnectionFactory
import com.yabaa.tournament.database.MongoDBManaged
import com.yabaa.tournament.health.TournamentDBHealthCheck
import com.yabaa.tournament.repository.PlayerRepository
import io.dropwizard.Application
import io.dropwizard.setup.Environment

class TournamentApplication : Application<TournamentApplicationConfiguration>() {
    override fun run(configuration: TournamentApplicationConfiguration, environment: Environment) {
        println("Running tournament server!")
        val mongoDBManagerConn = MongoDBConnectionFactory(configuration.mongoDBConnection)
        val mongoDBManaged = MongoDBManaged(mongoDBManagerConn.getClient()!!)
        val playerRepository = PlayerRepository(
            mongoDBManagerConn.getClient()!!
                .getDatabase(configuration.mongoDBConnection?.database!!)
                .getCollection("players")
        )
        environment.jersey().register(PlayerController(playerRepository))
        environment.lifecycle().manage(mongoDBManaged)
        environment.healthChecks().register(
            "TournamentDBHealthCheck",
            TournamentDBHealthCheck(mongoDBManagerConn.getClient()!!)
        )
    }
}