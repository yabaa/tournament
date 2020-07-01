package com.yabaa.tournament.database

import com.mongodb.ServerAddress
import com.yabaa.tournament.database.configuration.DynamoDBConnection
import com.yabaa.tournament.database.configuration.Seed
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition
import software.amazon.awssdk.services.dynamodb.model.DeleteTableRequest
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement
import software.amazon.awssdk.services.dynamodb.model.KeyType
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType
import java.net.URI
import java.util.stream.Collectors


class DynamoDBConnectionFactory(val dynamoDbClient: DynamoDbClient) {

    init {
        setupTable()
    }

    companion object {

        fun connect(config: DynamoDBConnection?): DynamoDBConnectionFactory {
            val server = config?.seeds!!.stream()
                .map { seed: Seed -> ServerAddress(seed.host, seed.port!!) }
                .findFirst()
                .orElse(ServerAddress("localhost", 8080))

            val dynamoDbClient = DynamoDbClient.builder()
                .endpointOverride(URI.create("http://${server.host}:${server.port}"))
                .region(Region.EU_WEST_1)
                .build() ?: throw IllegalStateException()

            return DynamoDBConnectionFactory(dynamoDbClient)
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
                    .attributeType(ScalarAttributeType.S)
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