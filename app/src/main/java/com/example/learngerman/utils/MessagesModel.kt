package com.example.learngerman.utils

class MessagesModel {
    var message:String?=null

    constructor(not: String?) {
        this.message = not
    }
    constructor(){}

    override fun toString(): String {
        return "Notlar(not=$message)"
    }

}