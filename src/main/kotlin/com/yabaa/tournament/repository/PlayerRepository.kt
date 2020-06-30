package com.yabaa.tournament.repository

import com.mongodb.BasicDBObject
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoCursor
import com.mongodb.client.model.FindOneAndUpdateOptions
import com.mongodb.client.model.ReturnDocument
import com.mongodb.client.model.Sorts.descending
import com.mongodb.client.model.Sorts.orderBy
import com.mongodb.client.result.DeleteResult
import com.yabaa.tournament.mapper.PlayerMapper
import com.yabaa.tournament.api.Player
import com.yabaa.tournament.api.PlayerWithRank
import org.bson.Document
import org.bson.types.ObjectId
import kotlin.collections.ArrayList


open class PlayerRepository(private var players: MongoCollection<Document>? = null) {

    open fun getAll(): List<Player> {
        val sortOrder = orderBy(descending("score"))
        val players: MongoCursor<Document> = players?.find()!!
            .sort(sortOrder)
            .iterator()
        val playersFound: MutableList<Player> = ArrayList()
        players.use { p ->
            while (p.hasNext()) {
                playersFound.add(PlayerMapper.map(p.next()))
            }
        }
        return playersFound
    }

    open fun getOne(id: ObjectId): PlayerWithRank? {
        val sortOrder = orderBy(descending("score"))
        var rank = 1
        var player: PlayerWithRank? = null
        //TODO: find a better way to rank the player, maybe using aggregate function
        for (doc in players?.find()!!.sort(sortOrder)) {
            if (doc.getObjectId("_id") == id) {
                player = PlayerMapper.mapWithRank(doc, rank)
                break
            }
            rank++
        }
        return player
    }

    open fun create(player: Player): ObjectId? {
        val newPlayer = Document("pseudo", player.pseudo).append("score", 0)
        val inserted = players?.insertOne(newPlayer)
        return inserted?.insertedId?.asObjectId()?.value
    }

    open fun update(id: ObjectId?, player: Player): Player? {
        val findOneAndUpdate = players?.findOneAndUpdate(
            Document("_id", id),
            Document(
                "\$set", Document("score", player.score)
            ),
            FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER)
        )
        return PlayerMapper.map(findOneAndUpdate!!)
    }

    open fun deleteAll(): DeleteResult? {
        return players?.deleteMany(BasicDBObject())
    }

}