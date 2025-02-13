package com.example.firebasegroupapp7

import java.io.Serializable

data class UserAddress(
    var streetAddress: String? = null,
    var city: String? = null,
    var province: String? = null,
    var postalCode: String? = null
): Serializable {
}