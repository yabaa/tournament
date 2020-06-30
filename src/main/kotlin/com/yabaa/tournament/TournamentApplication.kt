package com.yabaa.tournament

import com.yabaa.tournament.configuration.ObjectIdSerializer
import com.yabaa.tournament.daos.PlayerDAO
import com.yabaa.tournament.database.MongoDBConnectionFactory
import com.yabaa.tournament.database.MongoDBManaged
import com.yabaa.tournament.health.TournamentDBHealthCheck
import com.yabaa.tournament.resources.PlayerResource
import io.dropwizard.Application
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import io.federecio.dropwizard.swagger.SwaggerBundle
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration


class TournamentApplication : Application<TournamentApplicationConfiguration>() {

    override fun initialize(bootstrap: Bootstrap<TournamentApplicationConfiguration?>) {
        bootstrap.addBundle(object : SwaggerBundle<TournamentApplicationConfiguration>() {
            override fun getSwaggerBundleConfiguration(configuration: TournamentApplicationConfiguration): SwaggerBundleConfiguration {
                return configuration.swaggerBundleConfiguration!!
            }
        })
    }

    override fun run(configuration: TournamentApplicationConfiguration, environment: Environment) {
        println("Running tournament server!")
        val mongoDBManagerConn = MongoDBConnectionFactory(configuration.mongoDBConnection)
        val mongoDBManaged = MongoDBManaged(mongoDBManagerConn.getClient()!!)
        val playerRepository = PlayerDAO(
            mongoDBManagerConn.getClient()!!
                .getDatabase(configuration.mongoDBConnection?.database!!)
                .getCollection("players")
        )
        environment.lifecycle().manage(mongoDBManaged)
        environment.jersey().register(PlayerResource(playerRepository))

        environment.healthChecks()
            .register("TournamentDBHealthCheck", TournamentDBHealthCheck(mongoDBManagerConn.getClient()!!)
        )
    }

}