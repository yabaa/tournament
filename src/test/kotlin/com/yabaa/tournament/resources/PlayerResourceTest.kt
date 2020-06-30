package com.yabaa.tournament.resources

import com.mongodb.client.result.DeleteResult
import com.yabaa.tournament.api.Player
import com.yabaa.tournament.api.PlayerWithRank
import com.yabaa.tournament.daos.PlayerDAO
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport
import io.dropwizard.testing.junit5.ResourceExtension
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.groups.Tuple
import org.bson.types.ObjectId
import org.junit.ClassRule
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito
import javax.ws.rs.client.Entity
import javax.ws.rs.core.GenericType
import javax.ws.rs.core.MediaType


@ExtendWith(DropwizardExtensionsSupport::class)
class PlayerResourceTest {

    companion object {
        val playerRepository = Mockito.mock(PlayerDAO::class.java)!!

        @ClassRule
        val playerController: ResourceExtension = ResourceExtension.builder()
            .addResource(PlayerResource(playerRepository))
            .build()
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
        Mockito.`when`(playerRepository.getOne(playerId)).thenReturn(PlayerWithRank(playerId, "test", 4, 1))

        //when
        val response = playerController.target("/players/$playerId").request().get()!!

        //then
        assertThat(response.status).isEqualTo(200)
        val player = response.readEntity(PlayerWithRank::class.java)
        assertThat(player)
            .extracting("pseudo", "score", "rank")
            .containsExactly("test", 4, 1)
    }

    @Test
    fun `should return NotFound status when player is not found`() {
        //given
        val playerId = ObjectId("5ef8299deace171074fb61ed")
        Mockito.`when`(playerRepository.getOne(playerId)).thenReturn(null)

        //when
        val response = playerController.target("/players/$playerId").request().get()!!

        //then
        val message = response.readEntity(String::class.java)
        assertThat(response.status).isEqualTo(404)
        assertThat(message).isEqualTo("Player with id $playerId not found.")
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

    @Test
    fun `can UPDATE a new successfully`() {
        //given
        val playerId = ObjectId("5ef8299deace171074fb61ed")
        val player = Player(playerId, "newPlayer", 10)
        Mockito.`when`(playerRepository.update(eq(playerId), any())).thenReturn(player)

        //when
        val response = playerController
            .target("/players/$playerId")
            .request()
            .put(Entity.entity(player, MediaType.APPLICATION_JSON))!!

        //then
        assertThat(response.status).isEqualTo(200)
        val updatedPlayer = response.readEntity(Player::class.java)
        assertThat(updatedPlayer)
            .isEqualToIgnoringGivenFields(player, "id")
    }

    @Test
    fun `should return error on UPDATE when any issue occurred`() {
        //given
        val playerId = ObjectId("5ef8299deace171074fb61ed")
        val player = Player(playerId, "newPlayer", 10)
        Mockito.`when`(playerRepository.update(eq(playerId), any())).thenReturn(null)

        //when
        val response = playerController
            .target("/players/$playerId")
            .request()
            .put(Entity.entity(player, MediaType.APPLICATION_JSON))!!

        //then
        assertThat(response.status).isEqualTo(500)
        val message = response.readEntity(String::class.java)
        assertThat(message).isEqualTo("Something went wrong while trying to update player with id $playerId.")
    }

    @Test
    fun `can DELETE all players`() {
        //given
        Mockito.`when`(playerRepository.deleteAll()).thenReturn(DeleteResult.acknowledged(1))

        //when
        val response = playerController
            .target("/players")
            .request()
            .delete()!!

        //then
        assertThat(response.status).isEqualTo(204)
        val message = response.readEntity(String::class.java)
        assertThat(message).isEqualTo("1 player(s) successfully deleted.")
    }

    @Test
    fun `should return error when unable to DELETE all players`() {
        //given
        Mockito.`when`(playerRepository.deleteAll()).thenReturn(DeleteResult.unacknowledged())

        //when
        val response = playerController
            .target("/players")
            .request()
            .delete()!!

        //then
        assertThat(response.status).isEqualTo(500)
        val message = response.readEntity(String::class.java)
        assertThat(message).isEqualTo("Something went wrong while trying to delete all players.")
    }

    private fun <T> any(): T {
        return Mockito.any<T>()
    }
}
