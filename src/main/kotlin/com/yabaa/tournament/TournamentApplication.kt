package com.yabaa.tournament

import com.yabaa.tournament.configuration.TournamentApplicationConfiguration
import com.yabaa.tournament.controller.PlayerController
import com.yabaa.tournament.database.MongoDBConnectionFactory
import com.yabaa.tournament.database.MongoDBManaged
import io.dropwizard.Application
import io.dropwizard.setup.Environment


class TournamentApplication : Application<TournamentApplicationConfiguration>() {
    override fun run(configuration: TournamentApplicationConfiguration, environment: Environment) {
        println("Running tournament server!")
        val playerController = PlayerController()
        val mongoDBManagerConn = MongoDBConnectionFactory(configuration.mongoDBConnection)
        val mongoDBManaged = MongoDBManaged(mongoDBManagerConn.getClient()!!)
//        val donutDAO = DonutDAO(
//            mongoDBManagerConn.getClient()
//                .getDatabase(configuration.mongoDBConnection!!.database)
//                .getCollection("donuts")
//        )
        environment.lifecycle().manage(mongoDBManaged)
//        environment.jersey().register(DonutResource(donutDAO))
//        environment.healthChecks().register(
//            "DropwizardMongoDBHealthCheck",
//            DropwizardMongoDBHealthCheck(mongoDBManagerConn.getClient())
//        )
        environment.jersey().register(playerController)
    }
}