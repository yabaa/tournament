package com.yabaa.tournament.repository

import com.yabaa.tournament.api.Player
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.DeleteTableRequest
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement
import software.amazon.awssdk.services.dynamodb.model.KeyType
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType

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

    fun getOne(playerId: String?): Player {
        val item = dynamoDbClient.getItem(
            GetItemRequest.builder()
                .tableName("players")
                .key(mapOf("id" to AttributeValue.builder().s(playerId!!).build()))
                .build()
        ).item()

        return item.toPlayer()
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

    private fun MutableMap<String, AttributeValue>.toPlayer() =
        Player(this["id"]!!.s(), this["pseudo"]!!.s(), this["score"]!!.n().toInt())
}