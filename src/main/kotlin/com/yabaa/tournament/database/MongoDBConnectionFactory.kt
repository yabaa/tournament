package com.yabaa.tournament.database

import com.mongodb.MongoClientSettings
import com.mongodb.MongoCredential
import com.mongodb.ServerAddress
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import com.mongodb.connection.ClusterSettings
import com.yabaa.tournament.database.configuration.Credentials
import com.yabaa.tournament.database.configuration.MongoDBConnection
import com.yabaa.tournament.database.configuration.Seed
import java.util.stream.Collectors


class MongoDBConnectionFactory(var mongoDBConnection: MongoDBConnection? = null) {

    fun getClient(): MongoClient? {
        println("Creating mongoDB client.")
        val configCredentials: Credentials? = mongoDBConnection!!.credentials
        val credentials = MongoCredential.createCredential(
            configCredentials!!.username!!,
            mongoDBConnection!!.database!!,
            configCredentials.password!!
        )
        return MongoClients.create(
            MongoClientSettings.builder()
                .credential(credentials)
                .applyToClusterSettings { builder: ClusterSettings.Builder ->
                    builder.hosts(
                        getServers()
                    )
                }.build()
        )
    }

    private fun getServers(): List<ServerAddress?>? {
        val seeds = mongoDBConnection!!.seeds
        return seeds!!.stream()
            .map { seed: Seed -> ServerAddress(seed.host, seed.port!!) }
            .collect(Collectors.toList())
    }
}