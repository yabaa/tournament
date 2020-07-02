package com.yabaa.tournament.database

import com.yabaa.tournament.database.configuration.DynamoDBConnection
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition
import software.amazon.awssdk.services.dynamodb.model.DeleteTableRequest
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement
import software.amazon.awssdk.services.dynamodb.model.KeyType
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType
import java.net.URI
import java.util.Optional.ofNullable

class DynamoDBConnectionFactory(val dynamoDbClient: DynamoDbClient) {

    init {
        setupTable()
    }

    companion object {
        private const val DEFAULT_URL = "http://localhost:8000"

        fun connect(config: DynamoDBConnection?): DynamoDBConnectionFactory {
            val dbEndpoint = ofNullable(config?.seeds!!)
                .map { seed -> "http://${seed.host}:${seed.port}" }
                .orElse(DEFAULT_URL) //default address

            val dynamoDbClient = DynamoDbClient.builder()
                .endpointOverride(URI.create(dbEndpoint))
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