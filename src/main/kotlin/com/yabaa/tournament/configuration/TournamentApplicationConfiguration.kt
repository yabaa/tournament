package com.yabaa.tournament.configuration

import com.yabaa.tournament.database.configuration.MongoDBConnection
import io.dropwizard.Configuration


class TournamentApplicationConfiguration(var mongoDBConnection: MongoDBConnection? = null) : Configuration()
