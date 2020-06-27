package com.yabaa.tournament.database.configuration


class Seed(var host: String? = null, var port: Int? = 0) {

    override fun toString(): String {
        return "Seed{" +
                "host='" + host + '\'' +
                ", port=" + port +
                '}'
    }
}