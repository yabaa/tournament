package com.yabaa.tournament.controller

import com.yabaa.tournament.model.Player
import java.util.UUID
import javax.ws.rs.Path
import javax.ws.rs.GET
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
class PlayerController {

    @Path("/players")
    @GET
    fun getAll(): List<Player> {
        return listOf(Player("test"))
    }

    @Path("/players/{id}")
    @GET
    fun getOne(@PathParam("id") id: UUID): Player {
        return Player("test with $id")
    }

}