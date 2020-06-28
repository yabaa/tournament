package com.yabaa.tournament.repository

import com.mongodb.BasicDBObject
import com.mongodb.ConnectionString
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoCollection
import com.yabaa.tournament.model.Player
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.tuple
import org.bson.Document
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import org.testcontainers.containers.MongoDBContainer


class TestcontainersTests {
    companion object {
        private var playerCollection: MongoCollection<Document>? = null

        private val instance: MongoDBContainer = MongoDBContainer()
            .withExposedPorts(27017)

        @BeforeClass
        @JvmStatic
        internal fun beforeAll() {
            instance.start()
            val mongoClient: MongoClient = MongoClients.create(ConnectionString("mongodb://admin:admin@localhost:27017"))
            val database = mongoClient.getDatabase("testingDB")
            playerCollection = database.getCollection("players")
        }

        @AfterClass
        @JvmStatic
        internal fun afterAll() {
            instance.stop()
        }

    }

    @Test
    fun `can GET all players ordered by score`() {
        //given
        val player1 = Document("pseudo", "player1").append("score", 10)
        val player2 = Document("pseudo", "player2").append("score", 25)
        val player3 = Document("pseudo", "player3").append("score", 15)

        playerCollection?.insertMany(listOf(player1, player2, player3))

        val playerRepository = PlayerRepository(playerCollection)

        //when
        val foundPlayers = playerRepository.getAll()

        //then
        assertThat(foundPlayers)
            .hasSize(3)
            .extracting("pseudo", "score")
            .containsExactly(
                tuple("player2", 25),
                tuple("player3", 15),
                tuple("player1", 10)
            )
        playerCollection?.deleteMany(BasicDBObject()) //TODO: make it as @AfterEach or @BeforeEach
    }

    @Test
    fun `can GET one player by his id`() {
        //given
        val player1 = Document("pseudo", "player1").append("score", 10)
        val player2 = Document("pseudo", "player2").append("score", 25)
        val player3 = Document("pseudo", "player3").append("score", 15)

        val insertPlayerResult1 = playerCollection?.insertOne(player1)
        playerCollection?.insertOne(player2)
        playerCollection?.insertOne(player3)

        val playerRepository = PlayerRepository(playerCollection)

        //when
        val foundPlayer = playerRepository.getOne(insertPlayerResult1?.insertedId!!.asObjectId().value)

        //then
        assertThat(foundPlayer)
            .isNotNull
            .extracting("pseudo", "score")
            .containsExactly("player1", 10)

        playerCollection?.deleteMany(BasicDBObject()) //TODO: make it as @AfterEach or @BeforeEach
    }

    @Test
    fun `can CREATE a new player`() {
        //given
        val player1 = Player(null, "newPlayer", 0)
        val playerRepository = PlayerRepository(playerCollection)

        //when
        val createdPlayerId = playerRepository.create(player1)

        //then
        assertThat(createdPlayerId).isNotNull()
        val createdPlayer = playerRepository.getOne(createdPlayerId!!)

        assertThat(createdPlayer)
            .isEqualToIgnoringGivenFields(player1, "id")

        playerCollection?.deleteMany(BasicDBObject()) //TODO: make it as @AfterEach or @BeforeEach
    }

    @Test
    fun `can UPDATE player's score`() {
        //given
        val player = Document("pseudo", "player1").append("score", 0)
        val insertPlayerResult = playerCollection?.insertOne(player)

        val playerId = insertPlayerResult?.insertedId!!.asObjectId().value
        val newPlayer = Player(playerId, "player1", 5)

        val playerRepository = PlayerRepository(playerCollection)

        //when
        val updatedPlayer = playerRepository.update(playerId, newPlayer)

        //then
        assertThat(updatedPlayer)
            .isNotNull
            .isEqualToComparingFieldByField(newPlayer)

        playerCollection?.deleteMany(BasicDBObject()) //TODO: make it as @AfterEach or @BeforeEach
    }

}
