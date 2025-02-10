package com.example.firebasegroupapp7

import java.io.Serializable

class UserAddress(
    var streetAddress: String,
    var city: String,
    var province: String,
    var postalCode: String
): Serializable {
}