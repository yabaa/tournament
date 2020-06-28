package com.yabaa.tournament.model

import org.bson.types.ObjectId

class Player(val id: ObjectId? = null, val pseudo: String? = null, val score: Int ? = 0)