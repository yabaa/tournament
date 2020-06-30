package com.yabaa.tournament.api

import com.fasterxml.jackson.databind.ObjectMapper
import io.dropwizard.jackson.Jackson
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


class PlayerTest {

    private val objectMapper: ObjectMapper = Jackson.newObjectMapper()

    private val playerJson: String = "{\n" +
            "  \"id\":\"5efb38edd39d973add75764b\",\n" +
            "  \"pseudo\": \"Player 1\",\n" +
            "  \"score\":10\n" +
            "}"

    private val playerWithRankJson: String = "{\n" +
            "  \"id\":\"5efb38edd39d973add75764b\",\n" +
            "  \"pseudo\": \"Player 1\",\n" +
            "  \"score\":10,\n" +
            "  \"rank\":4\n" +
            "}"

    @Test
    fun `can serialize Player object to JSON`() {
        val player = Player("5efb38edd39d973add75764b", "Player 1", 10)
        val expected = objectMapper.writeValueAsString(objectMapper.readValue(playerJson, Player::class.java))
        assertThat(objectMapper.writeValueAsString(player)).isEqualTo(expected)
    }

    @Test
    fun `can deserialize Player object from JSON`() {
        val player = Player("5efb38edd39d973add75764b", "Player 1", 10)
        assertThat(objectMapper.readValue(playerJson, Player::class.java))
            .isEqualToComparingFieldByField(player)
    }

    @Test
    fun `can serialize PlayerWithRank object to JSON`() {
        val playerWithRank = PlayerWithRank("5efb38edd39d973add75764b", "Player 1", 10, 4)
        val expected = objectMapper.writeValueAsString(objectMapper.readValue(playerWithRankJson, PlayerWithRank::class.java))
        assertThat(objectMapper.writeValueAsString(playerWithRank)).isEqualTo(expected)
    }

    @Test
    fun `can deserialize PlayerWithRank object from JSON`() {
        val playerWithRank = PlayerWithRank("5efb38edd39d973add75764b", "Player 1", 10, 4)
        assertThat(objectMapper.readValue(playerWithRankJson, PlayerWithRank::class.java))
            .isEqualToComparingFieldByField(playerWithRank)
    }

}