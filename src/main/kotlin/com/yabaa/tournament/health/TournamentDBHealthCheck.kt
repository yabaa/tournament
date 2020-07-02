package com.yabaa.tournament.health

import com.codahale.metrics.health.HealthCheck
import software.amazon.awssdk.services.dynamodb.DynamoDbClient

class TournamentDBHealthCheck(private val dynamoDbClient: DynamoDbClient) : HealthCheck() {

    override fun check(): Result {
        val tableExists = dynamoDbClient.listTables().tableNames().contains("players")
        return if (tableExists) {
            Result.healthy()
        } else {
            Result.unhealthy("Can not find players table in database.")
        }
    }

}