package com.example.learngerman.utils

class NotlarModel {
    var not:String?=null

    constructor(not: String?) {
        this.not = not
    }
    constructor(){}

    override fun toString(): String {
        return "Notlar(not=$not)"
    }

}