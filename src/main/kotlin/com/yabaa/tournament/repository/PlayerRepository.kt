package com.yabaa.tournament.repository

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

open class PlayerRepository(private val dynamoDbClient: DynamoDbClient) {

     open fun create(player: Player): String {
        val item = mapOf(
            "id" to AttributeValue.builder().s(player.id.toString()).build(),
            "pseudo" to AttributeValue.builder().s(player.pseudo).build(),
            "score" to AttributeValue.builder().n("0").build() // default init score
        )

        dynamoDbClient.putItem(
            PutItemRequest.builder()
                .tableName("players")
                .item(item)
                .conditionExpression("attribute_not_exists(pseudo)")
                .build())

         return player.id!!
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
                if (p.id == playerId) {
                    player = PlayerWithRank(playerId, p.pseudo, p.score, rank + 1)
                    return@outside
                }
            }
        }
        return player
    }

    open fun update(playerId: String?, player: Player?): PlayerWithRank? {
        val itemKey = mapOf(
            "id" to AttributeValue.builder().s(playerId!!).build()
        )
        val updatedValues = mapOf(
            "score" to AttributeValueUpdate.builder()
                .value(AttributeValue.builder().n(player?.score!!.toString()).build())
                .action(AttributeAction.PUT)
                .build()
        )

        dynamoDbClient.updateItem(UpdateItemRequest.builder()
            .tableName("players")
            .key(itemKey)
            .attributeUpdates(updatedValues)
            .build()
        )

        return getOne(playerId)
    }

    open fun deleteAll() {
        val tableExists = dynamoDbClient.listTables()
            .tableNames()
            .contains("players")

        if (tableExists) {
            dynamoDbClient.deleteTable(
                DeleteTableRequest
                    .builder()
                    .tableName("players")
                    .build()
            )
        }

        dynamoDbClient.createTable { builder ->
            builder.tableName("players")

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
                    .attributeType(ScalarAttributeType.S)
                    .build()
            )
        }
    }

    private fun getOrderedPlayers(): List<Player> {
        return dynamoDbClient.scan { scan ->
            scan.tableName("players")
        }.items()
            .map { it.toPlayer() }
            .sortedBy { it.score }
            .asReversed()
    }


}