package com.yabaa.tournament.repository

import com.yabaa.tournament.api.Player
import com.yabaa.tournament.daos.KGenericContainer
import com.yabaa.tournament.helper.DynamoDBHelper
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.testcontainers.containers.wait.strategy.Wait
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import java.io.File


class PlayerRepositoryTest {

    companion object {

        val instance: KGenericContainer = KGenericContainer(File("./src/test/resources/dynamodb-docker-compose.yml"))
            .withLocalCompose(true)
            .waitingFor("dynamo_1", Wait.forListeningPort())

        var dynamoDbHelper: DynamoDBHelper? = null
        var dynamoDbClient: DynamoDbClient? = null

        @BeforeAll
        @JvmStatic
        internal fun beforeAll() {
            instance.start()
            dynamoDbHelper = DynamoDBHelper.connect()
            dynamoDbClient = dynamoDbHelper?.dynamoDbClient
        }

        @AfterAll
        @JvmStatic
        internal fun afterAll() {
            instance.stop()
        }
    }

    @Test
    internal fun `add Player to DynamoDB`() {
        val playerId = "5efb38edd39d973add75764b"
        val player = Player(playerId, "player 1", 0)

        val playerRepository = PlayerRepository(dynamoDbClient!!)
        playerRepository.create(player)

        val storedPlayer = dynamoDbHelper?.findById(playerId)

        Assertions.assertThat(storedPlayer).isEqualToComparingFieldByField(player);

    }
}