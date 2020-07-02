package com.yabaa.tournament.mapper

import com.yabaa.tournament.api.Player
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

object PlayerMapper {

    fun MutableMap<String, AttributeValue>.toPlayer() =
        Player(this["id"]!!.s(), this["pseudo"]!!.s(), this["score"]!!.n().toInt())

}