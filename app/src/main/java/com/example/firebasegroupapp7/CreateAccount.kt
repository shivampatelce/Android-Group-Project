package com.example.firebasegroupapp7

import java.io.Serializable

data class CreateAccount(
    var firstName: String? = null,
    var lastName: String? = null,
    var email: String? = null,
    var address: List<UserAddress>? = null
) : Serializable {
}