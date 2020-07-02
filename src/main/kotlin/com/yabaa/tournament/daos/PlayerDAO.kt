package com.yabaa.tournament.daos

import com.yabaa.tournament.api.Player
import com.yabaa.tournament.api.PlayerWithRank
import com.yabaa.tournament.mapper.PlayerMapper.toPlayer
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeAction
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.AttributeValueUpdate
import software.amazon.awssdk.services.dynamodb.model.DeleteTableRequest
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement
import software.amazon.awssdk.services.dynamodb.model.KeyType
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest

open class PlayerDAO(private val dynamoDbClient: DynamoDbClient) {

    private val tableName = "players"

     open fun create(player: Player): String {
         val nextId = getNextId()
         val item = mapOf(
            "id" to AttributeValue.builder().n(nextId.toString()).build(),
            "pseudo" to AttributeValue.builder().s(player.pseudo).build(),
            "score" to AttributeValue.builder().n("0").build() // default init score
        )

        dynamoDbClient.putItem(
            PutItemRequest.builder()
                .tableName(tableName)
                .item(item)
                .conditionExpression("attribute_not_exists(pseudo)")
                .build())

         return nextId.toString()
    }

    open fun getAll(): List<Player> {
        return getOrderedPlayers()
    }

    open fun getOne(playerId: String?): PlayerWithRank? {
        var player: PlayerWithRank? = null
        val players = getOrderedPlayers()
        //TODO: find a better way to rank the player
        run outside@{
            players.forEachIndexed { rank, p ->
                if (p.id.toString() == playerId) {
                    player = PlayerWithRank(p.id, p.pseudo, p.score, rank + 1)
                    return@outside
                }
            }
        }
        return player
    }

    open fun update(playerId: String?, player: Player?): PlayerWithRank? {
        val itemKey = mapOf(
            "id" to AttributeValue.builder().n(playerId!!).build()
        )
        val updatedValues = mapOf(
            "score" to AttributeValueUpdate.builder()
                .value(AttributeValue.builder().n(player?.score!!.toString()).build())
                .action(AttributeAction.PUT)
                .build()
        )

        dynamoDbClient.updateItem(UpdateItemRequest.builder()
            .tableName(tableName)
            .key(itemKey)
            .attributeUpdates(updatedValues)
            .build()
        )

        return getOne(playerId)
    }

    open fun deleteAll() {
        deleteTable()
        createTable()
    }

    private fun deleteTable() {
        val tableExists = dynamoDbClient.listTables()
            .tableNames()
            .contains(tableName)

        if (tableExists) {
            dynamoDbClient.deleteTable(
                DeleteTableRequest
                    .builder()
                    .tableName(tableName)
                    .build()
            )
        }
    }

    private fun createTable() {
        dynamoDbClient.createTable { builder ->
            builder.tableName(tableName)

            builder.provisionedThroughput { provisionedThroughput ->
                provisionedThroughput.readCapacityUnits(5)
                provisionedThroughput.writeCapacityUnits(5)
            }

            builder.keySchema(
                KeySchemaElement.builder()
                    .attributeName("id")
                    .keyType(KeyType.HASH)
                    .build()
            )

            builder.attributeDefinitions(
                AttributeDefinition.builder()
                    .attributeName("id")
                    .attributeType(ScalarAttributeType.N)
                    .build()
            )
        }
    }

    private fun getOrderedPlayers(): List<Player> {
        return dynamoDbClient.scan { scan ->
            scan.tableName(tableName)
        }.items()
            .map { it.toPlayer() }
            .sortedBy { it.score }
            .asReversed()
    }

    private fun getNextId(): Int {
        val items = dynamoDbClient.scan { scan ->
            scan.tableName(tableName)
            scan.attributesToGet("id")
        }.items()

        val lastId = items
            .map { it["id"]!!.n().toInt()  }
            .max() ?: 0

        return lastId + 1
    }


}