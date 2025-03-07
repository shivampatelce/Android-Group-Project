package com.example.firebasegroupapp7

class Order(
    var userId: String? = null,
    var products: ArrayList<Cart>? = null,
    var address: UserAddress? = null
) {
}