package com.yabaa.tournament

import com.yabaa.tournament.database.DynamoDBConnectionFactory
import com.yabaa.tournament.health.TournamentDBHealthCheck
import com.yabaa.tournament.daos.PlayerDAO
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
        val dynamoDBManagerConn = DynamoDBConnectionFactory.connect(configuration.dynamoDBConnection)

        environment.jersey()
            .register(PlayerResource(PlayerDAO(dynamoDBManagerConn.dynamoDbClient)))

        environment.healthChecks()
            .register("TournamentDBHealthCheck", TournamentDBHealthCheck(dynamoDBManagerConn.dynamoDbClient))
    }

}