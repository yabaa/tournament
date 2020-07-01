package com.yabaa.tournament.repository

import com.yabaa.tournament.api.Player
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.DeleteTableRequest
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest

class PlayerRepository(private val dynamoDbClient: DynamoDbClient) {

     fun create(player: Player): String {
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

    fun getAll(): List<Player> {
        val scanResponse = dynamoDbClient.scan { scan ->
            scan.tableName("players")
        }

        return scanResponse.items().map { it.toPlayer() }.sortedBy { it.score }.asReversed()
    }

    fun deleteAll() {
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
    }

    private fun MutableMap<String, AttributeValue>.toPlayer() =
        Player(this["id"]!!.s(), this["pseudo"]!!.s(), this["score"]!!.n().toInt())
}