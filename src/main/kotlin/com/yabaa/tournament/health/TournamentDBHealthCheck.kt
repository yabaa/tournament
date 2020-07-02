package com.yabaa.tournament.health

import com.codahale.metrics.health.HealthCheck
import software.amazon.awssdk.services.dynamodb.DynamoDbClient


class TournamentDBHealthCheck(private val dynamoDbClient: DynamoDbClient) : HealthCheck() {

    override fun check(): Result {
        val tableExists = dynamoDbClient.listTables().tableNames().contains("players")
        return if (tableExists) {
            Result.unhealthy("Can not perform operation buildInfo in Database.")
        } else {
            Result.unhealthy("Can not get the information from database.")
        }
    }

}