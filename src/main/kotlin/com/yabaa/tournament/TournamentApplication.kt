package com.yabaa.tournament

import com.yabaa.tournament.daos.PlayerDAO
import com.yabaa.tournament.database.DynamoDBConnectionFactory
import com.yabaa.tournament.database.MongoDBConnectionFactory
import com.yabaa.tournament.database.MongoDBManaged
import com.yabaa.tournament.health.TournamentDBHealthCheck
import com.yabaa.tournament.repository.PlayerRepository
import com.yabaa.tournament.resources.PlayerController
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
        val dynamoDBManagerConn = DynamoDBConnectionFactory.connect(configuration.dynamoDBConnection)
        val playerResource = PlayerDAO(
            mongoDBManagerConn.getClient()!!
                .getDatabase(configuration.mongoDBConnection?.database!!)
                .getCollection("players")
        )
        environment.lifecycle().manage(mongoDBManaged)
        environment.jersey().register(PlayerResource(playerResource))
        environment.jersey().register(PlayerController(PlayerRepository(dynamoDBManagerConn.dynamoDbClient)))

        environment.healthChecks()
            .register("TournamentDBHealthCheck", TournamentDBHealthCheck(mongoDBManagerConn.getClient()!!)
        )
    }

}