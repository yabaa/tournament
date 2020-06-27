package com.yabaa.tournament.controller

import com.yabaa.tournament.TournamentApplication
import com.yabaa.tournament.configuration.TournamentApplicationConfiguration
import com.yabaa.tournament.model.Player
import io.dropwizard.testing.junit5.DropwizardAppExtension
import io.dropwizard.testing.junit5.DropwizardClientExtension
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.groups.Tuple
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testcontainers.containers.MongoDBContainer
import java.util.UUID
import javax.ws.rs.client.ClientBuilder
import javax.ws.rs.core.GenericType


@ExtendWith(DropwizardExtensionsSupport::class)
class PlayerControllerIntegrationTest {

    companion object {

        private val instance: MongoDBContainer = MongoDBContainer()
            .withExposedPorts(27017)

        @BeforeClass
        @JvmStatic
        internal fun beforeAll() {
            instance.start()
        }

        @AfterClass
        @JvmStatic
        internal fun afterAll() {
            instance.stop()
        }

        private val EXT = DropwizardClientExtension(PlayerController())

    }

    @Test
    fun `can GET players successfully`() {
        //given
        val endpoint = EXT.baseUri().toString() + "/players"

        //when
        val response = ClientBuilder.newClient()
            .target(endpoint)
            .request()
            .get()!!

        //then
        assertThat(response.status).isEqualTo(200)
        val players = response.readEntity(object : GenericType<List<Player>>() {})
        assertThat(players).hasSize(1)
        assertThat(players)
            .extracting("pseudo", "score")
            .containsExactly(Tuple.tuple("test", 0))
    }

    @Test
    fun `can GET one player successfully`() {
        //given
        val playerId = UUID.randomUUID();
        val endpoint = EXT.baseUri().toString() + "/players/$playerId"

        //when
        val response = ClientBuilder.newClient()
            .target(endpoint)
            .request()
            .get()!!

        //then
        assertThat(response.status).isEqualTo(200)
        val player = response.readEntity(Player::class.java)
        assertThat(player)
            .extracting("pseudo", "score")
            .containsExactly("test with $playerId", 0)
    }

}
