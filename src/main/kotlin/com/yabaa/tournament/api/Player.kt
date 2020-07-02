package com.yabaa.tournament.api

import java.io.Serializable

class Player(
    val id: String? = null,
    val pseudo: String? = null,
    val score: Int ? = 0
) : Serializable

