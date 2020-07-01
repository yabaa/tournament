package com.yabaa.tournament.database.configuration

class DynamoDBConnection(
    var credentials: Credentials? = null,
    var seeds: List<Seed>? = null,
    var database: String? = null)
{

    override fun toString(): String {
        return ("DynamoDBConnection{"
                + "credentials=" + credentials
                + ", seeds=" + seeds
                + ", database='" + database + '\''
                + '}')
    }
}