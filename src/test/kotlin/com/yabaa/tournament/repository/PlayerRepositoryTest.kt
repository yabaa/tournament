package com.yabaa.tournament.repository

import com.yabaa.tournament.api.Player
import com.yabaa.tournament.daos.KGenericContainer
import com.yabaa.tournament.helper.DynamoDBHelper
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
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
        var playerRepository: PlayerRepository? = null

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

    @BeforeEach
    fun setup() {
        playerRepository = PlayerRepository(dynamoDbClient!!)
    }

//    @AfterEach
//    fun tearDown() {
//        playerRepository?.deleteAll();
//    }

    @Test
    fun `can create a Player`() {
        //given
        val playerId = "12345"
        val player = Player(playerId, "player5", 0)

        //when
        playerRepository?.create(player)

        //then
        val storedPlayer = dynamoDbHelper?.findById(playerId)
        Assertions.assertThat(storedPlayer).isEqualToComparingFieldByField(player)
    }

    @Test
    fun `can GET ALL Players sorted by score`() {
        //given
        val player1 = Player("1", "player1", 20)
        val player2 = Player("2", "player2", 10)
        val player3 = Player("3", "player3", 50)

        dynamoDbHelper?.save(player1, player2, player3)

        //when
        val foundPlayers = playerRepository?.getAll()

        //then
        Assertions.assertThat(foundPlayers)
            .hasSize(3)
            .extracting("id", "pseudo", "score")
            .containsExactly(
                Assertions.tuple("3", "player3", 50),
                Assertions.tuple("1", "player1", 20),
                Assertions.tuple("2", "player2", 10)
            )
    }
}