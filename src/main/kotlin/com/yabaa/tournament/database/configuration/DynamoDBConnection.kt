package com.yabaa.tournament.database.configuration

class DynamoDBConnection(
    var seeds: Seed? = null,
    var database: String? = null)
{

    override fun toString(): String {
        return ("DynamoDBConnection{"
                + "seeds=" + seeds
                + ", database='" + database + '\''
                + '}')
    }
}