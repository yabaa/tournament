package com.yabaa.tournament.helper

import com.yabaa.tournament.api.Player
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.DeleteTableRequest
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement
import software.amazon.awssdk.services.dynamodb.model.KeyType
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType
import java.net.URI


class DynamoDBHelper(val dynamoDbClient: DynamoDbClient) {

    init {
        setupTable()
    }

    companion object {

        fun connect(endpoint: String = "http://localhost:8000"): DynamoDBHelper {

            val dynamoDbClient = DynamoDbClient.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.EU_WEST_1)
                .build() ?: throw IllegalStateException()

            return DynamoDBHelper(dynamoDbClient)
        }
    }

    fun findById(playerId: String): Player {
        val item = dynamoDbClient.getItem(
            GetItemRequest.builder()
                .tableName("players")
                .key(mapOf("id" to AttributeValue.builder().n(playerId).build()))
                .build()
        ).item()

        return Player(item["id"]!!.n().toInt(), item["pseudo"]!!.s(), item["score"]!!.n().toInt())
    }

    fun save(vararg players: Player) {
        players.forEach {
            dynamoDbClient.putItem(
                PutItemRequest.builder()
                    .tableName("players")
                    .item(mapOf(
                        "id" to AttributeValue.builder().n(it.id.toString()).build(),
                        "pseudo" to AttributeValue.builder().s(it.pseudo).build(),
                        "score" to AttributeValue.builder().n(it.score.toString()).build() // default init score
                    ))
                    .conditionExpression("attribute_not_exists(task_id)")
                    .build())
        }
    }

    private fun setupTable() {
        dropTable()
        createTable()
    }

    private fun createTable() {
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
                    .attributeType(ScalarAttributeType.N)
                    .build()
            )
        }
    }

    private fun dropTable() {
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
}