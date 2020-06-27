package com.yabaa.tournament.database.configuration

class MongoDBConnection(
    var credentials: Credentials? = null,
    var seeds: List<Seed>? = null,
    var database: String? = null)
{

    override fun toString(): String {
        return ("MongoDBConnection{"
                + "credentials=" + credentials
                + ", seeds=" + seeds
                + ", database='" + database + '\''
                + '}')
    }
}