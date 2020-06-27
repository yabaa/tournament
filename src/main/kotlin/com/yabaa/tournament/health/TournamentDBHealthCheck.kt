package com.yabaa.tournament.health

import com.codahale.metrics.health.HealthCheck
import com.mongodb.reactivestreams.client.MongoClient
import org.bson.Document


class TournamentDBHealthCheck(private val mongoClient: MongoClient) : HealthCheck() {

    override fun check(): Result {
        try {
            val document = mongoClient.getDatabase("tournament").runCommand(Document("buildInfo", 1))
                ?: return Result.unhealthy("Can not perform operation buildInfo in Database.")
        } catch (e: Exception) {
            return Result.unhealthy("Can not get the information from database.")
        }
        return Result.healthy()
    }

}