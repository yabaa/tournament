package com.yabaa.tournament.resources

import com.yabaa.tournament.api.Player
import com.yabaa.tournament.repository.PlayerRepository
import io.swagger.annotations.Api
import java.net.URI
import javax.validation.constraints.NotNull
import javax.ws.rs.Consumes
import javax.ws.rs.DELETE
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.PUT
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response


@Api(
    value = "Player Controller",
    description = "Player REST API to handle CRUD operations on players collection."
)
@Path("/players2")
@Produces(MediaType.APPLICATION_JSON)
class PlayerController(private val playerRepository: PlayerRepository? = null) {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    fun create(player: @NotNull Player): Response? {
        val createdId = playerRepository?.create(player)
        return if (createdId == null) {
            Response.status(Response.Status.INTERNAL_SERVER_ERROR).build()
        } else {
            Response.created(URI.create("/players/$createdId"))
                .entity(createdId.toString())
                .status(Response.Status.CREATED).build()
        }
    }

    @GET
    fun getAll(): Response {
        val all = playerRepository?.getAll()
        return if (all!!.isEmpty()) {
            Response.status(Response.Status.NOT_FOUND).entity("No players have been found.").build()
        } else {
            Response.ok(all).build()
        }
    }

    @Path("/{id}")
    @GET
    fun getOne(@PathParam("id") id: @NotNull String): Response {
        val one = playerRepository?.getOne(id)
        return if (one == null) {
            Response.status(Response.Status.NOT_FOUND).entity("Player with id $id not found.").build()
        } else {
            Response.ok(one).build()
        }
    }

    @PUT
    @Path("/{id}")
    fun update(@PathParam("id") id: @NotNull String, player: @NotNull Player): Response? {
        val result = playerRepository?.update(id, player)
        return if (result == null) {
            Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Something went wrong while trying to update player with id $id.")
                .build()
        } else {
            Response.ok(result).build()
        }
    }

    @DELETE
    fun deleteAll(): Response {
        playerRepository?.deleteAll()
        return Response.noContent()
                .entity("Player(s) successfully deleted.")
                .build()
    }

}