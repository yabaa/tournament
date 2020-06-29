package com.yabaa.tournament.mapper

import com.yabaa.tournament.model.Player
import com.yabaa.tournament.model.PlayerWithRank
import org.bson.Document

object PlayerMapper {

    fun map(document: Document): Player {
        return Player(document.getObjectId("_id"), document.getString("pseudo"), document.getInteger("score"))
    }

    fun mapWithRank(document: Document, rank: Int): PlayerWithRank {
        return PlayerWithRank(
            document.getObjectId("_id"),
            document.getString("pseudo"),
            document.getInteger("score"),
            rank
        )
    }
}