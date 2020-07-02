package com.yabaa.tournament.api

import java.io.Serializable

class Player(
    val id: Int? = null,
    val pseudo: String? = null,
    val score: Int ? = 0
) : Serializable

