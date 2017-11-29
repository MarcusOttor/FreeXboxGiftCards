package com.freexbox.freegiftcards.xboxgiftcards.data

import io.realm.RealmObject

open class Card() : RealmObject() {

    var price: Int = 0
    var type: Int = 0

    constructor(price: Int, type: Int) : this() {
        this.price = price
        this.type = type
    }
}