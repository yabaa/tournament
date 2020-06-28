package com.yabaa.tournament.repository

import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoCursor
import com.yabaa.tournament.mapper.PlayerMapper
import com.yabaa.tournament.model.Player
import org.bson.Document
import org.bson.types.ObjectId
import java.util.*


open class PlayerRepository(private var players: MongoCollection<Document>? = null) {

    open fun getAll(): List<Player> {
        val players: MongoCursor<Document> = players?.find()!!.iterator()
        val playersFound: MutableList<Player> = ArrayList()
        players.use { p ->
            while (p.hasNext()) {
                playersFound.add(PlayerMapper.map(p.next()))
            }
        }
        return playersFound
    }

    open fun getOne(id: ObjectId): Player? {
        return Optional.ofNullable(players?.find(Document("_id", id))!!.first())
            .map { player -> PlayerMapper.map(player) }
            .orElse(null)
    }

    open fun create(player: Player): ObjectId? {
        val newPlayer = Document("pseudo", player.pseudo).append("score", 0)
        val inserted = players?.insertOne(newPlayer)
        return inserted?.insertedId?.asObjectId()?.value
    }


}