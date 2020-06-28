package com.yabaa.tournament.controller

import com.yabaa.tournament.model.Player
import com.yabaa.tournament.repository.PlayerRepository
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport
import io.dropwizard.testing.junit5.ResourceExtension
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.groups.Tuple
import org.bson.types.ObjectId
import org.junit.After
import org.junit.ClassRule
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import javax.ws.rs.client.Entity
import javax.ws.rs.core.GenericType
import javax.ws.rs.core.MediaType


@ExtendWith(DropwizardExtensionsSupport::class)
class PlayerControllerTest {

    companion object {
        val playerRepository = Mockito.mock(PlayerRepository::class.java)!!

        @ClassRule
        val playerController: ResourceExtension = ResourceExtension.builder()
            .addResource(PlayerController(playerRepository))
            .build()

        @After
        fun tearDown() {
            Mockito.reset<Any>(playerRepository)
        }

    }

    @Test
    fun `can GET players successfully`() {
        //given
        Mockito.`when`(playerRepository.getAll()).thenReturn(listOf(Player(null, "test", 10)))

        //when
        val response = playerController.target("/players").request().get()!!

        //then
        assertThat(response.status).isEqualTo(200)
        val players = response.readEntity(object : GenericType<List<Player>>() {})
        assertThat(players).hasSize(1)
        assertThat(players)
            .extracting("pseudo", "score")
            .containsExactly(Tuple.tuple("test", 10))
    }

    @Test
    fun `can GET one player successfully`() {
        //given
        val playerId = ObjectId("5ef8299deace171074fb61ed")
        Mockito.`when`(playerRepository.getOne(playerId)).thenReturn(Player(playerId, "test", 4))

        //when
        val response = playerController.target("/players/$playerId").request().get()!!

        //then
        assertThat(response.status).isEqualTo(200)
        val player = response.readEntity(Player::class.java)
        assertThat(player)
            .extracting("pseudo", "score")
            .containsExactly("test", 4)
    }

    @Test
    fun `should return NotFound status when player is not found`() {
        //given
        val playerId = ObjectId("5ef8299deace171074fb61ed")
        Mockito.`when`(playerRepository.getOne(playerId)).thenReturn(null)

        //when
        val response = playerController.target("/players/$playerId").request().get()!!

        //then
        assertThat(response.status).isEqualTo(404)
    }

    @Test
    fun `can POST a new player successfully`() {
        //given
        val player = Player(null, "newPlayer", null)
        val playerId = ObjectId("5ef8299deace171074fb61ed")
        Mockito.`when`(playerRepository.create(any())).thenReturn(playerId)

        //when
        val response = playerController
            .target("/players")
            .request()
            .post(Entity.entity(player, MediaType.APPLICATION_JSON))!!

        //then
        assertThat(response.status).isEqualTo(201)
        val createdId = response.readEntity(String::class.java)
        assertThat(createdId).isEqualTo(playerId.toString())
    }

    private fun <T> any(): T {
        return Mockito.any<T>()
    }
}
