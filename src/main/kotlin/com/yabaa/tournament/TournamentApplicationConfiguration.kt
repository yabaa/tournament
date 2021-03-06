package com.yabaa.tournament

import com.fasterxml.jackson.annotation.JsonProperty
import com.yabaa.tournament.database.DynamoDBConnection
import io.dropwizard.Configuration
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration

class TournamentApplicationConfiguration : Configuration() {

    var dynamoDBConnection: DynamoDBConnection? = null

    @JsonProperty("swagger")
    var swaggerBundleConfiguration: SwaggerBundleConfiguration? = null

}
