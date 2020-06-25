package com.yabaa.tournament.controller

import com.yabaa.tournament.model.Player
import java.util.UUID
import javax.ws.rs.Path
import javax.ws.rs.GET
import javax.ws.rs.PathParam

@Path("/")
class PlayerController {

    @Path("/players")
    @GET
    fun getAll(): String {
        return Player("test").toString()
    }

    @Path("/players/{id}")
    @GET
    fun getOne(@PathParam("id") id: UUID): String {
        return Player("test with $id").toString()
    }

}