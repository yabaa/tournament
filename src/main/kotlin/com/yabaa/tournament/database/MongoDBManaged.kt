package com.yabaa.tournament.database

import com.mongodb.reactivestreams.client.MongoClient
import io.dropwizard.lifecycle.Managed

class MongoDBManaged (

    private val mongoClient: MongoClient

) : Managed {

    @Throws(Exception::class)
    override fun start() {
    }

    @Throws(Exception::class)
    override fun stop() {
        mongoClient.close()
    }

}