package com.yabaa.tournament

import com.yabaa.tournament.configuration.TournamentApplicationConfiguration
import com.yabaa.tournament.controller.PlayerController
import io.dropwizard.Application
import io.dropwizard.setup.Environment

class TournamentApplication : Application<TournamentApplicationConfiguration>() {
    override fun run(configuration: TournamentApplicationConfiguration, environment: Environment) {
        println("Running tournament server!")
        val playerController = PlayerController()
        environment.jersey().register(playerController)
    }
}