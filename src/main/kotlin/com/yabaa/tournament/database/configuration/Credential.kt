package com.yabaa.tournament.database.configuration

//@JsonSerialize(using = PasswordSerializer::class)
class Credentials(var username: String? = null, var password: CharArray? = null) {

    override fun toString(): String {
        return ("Credentials{"
                + "username='" + username + '\''
                + ", password=" + password?.contentToString()
                + '}')
    }
}