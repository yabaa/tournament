package com.yabaa.tournament.controller

import com.yabaa.tournament.model.Player
import com.yabaa.tournament.repository.PlayerRepository
import org.bson.types.ObjectId
import java.net.URI
import javax.validation.constraints.NotNull
import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response


@Path("/")
@Produces(MediaType.APPLICATION_JSON)
class PlayerController(private val playerRepository: PlayerRepository? = null) {

    @Path("/players")
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

    @Path("/players")
    @GET
    fun getAll(): Response {
        val all = playerRepository?.getAll()
        return if (all!!.isEmpty()) {
            Response.status(Response.Status.NOT_FOUND).build()
        } else {
            Response.ok(all).build()
        }
    }

    @Path("/players/{id}")
    @GET
    fun getOne(@PathParam("id") id: @NotNull ObjectId): Response {
        val one = playerRepository?.getOne(id)
        return if (one == null) {
            Response.status(Response.Status.NOT_FOUND).build()
        } else {
            Response.ok(one).build()
        }
    }

}