package com.yabaa.tournament.api

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.yabaa.tournament.configuration.ObjectIdSerializer
import org.jongo.marshall.jackson.oid.MongoObjectId
import java.io.Serializable

class Player(
    @MongoObjectId @JsonSerialize(using = ObjectIdSerializer::class) val id: String? = null,
    val pseudo: String? = null,
    val score: Int ? = 0
) : Serializable

