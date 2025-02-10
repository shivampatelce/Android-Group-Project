package com.example.firebasegroupapp7

import java.io.Serializable

class CreateAccount(
    var firstName: String,
    var lastName: String,
    var email: String,
    var address: List<UserAddress>? = null
) : Serializable {
}