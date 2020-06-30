package com.yabaa.tournament.api

import org.bson.types.ObjectId

class Player(val id: ObjectId? = null, val pseudo: String? = null, val score: Int ? = 0)

class PlayerWithRank(val id: ObjectId? = null, val pseudo: String? = null, val score: Int ? = 0, val rank: Int? =0)