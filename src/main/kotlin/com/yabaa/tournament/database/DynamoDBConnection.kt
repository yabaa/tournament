package com.yabaa.tournament.database

class DynamoDBConnection(
    var host: String? = null,
    var port: String? = null
)
{

    override fun toString(): String {
        return ("DynamoDBConnection{"
                + "host=" + host
                + ", port='" + port + '\''
                + '}')
    }
}