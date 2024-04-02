package com.example.learngerman

class Kullanici {

    constructor()
    {}

    var isim: String? = null
    var kullanici_id: String? = null
    var email: String? = null
    var profilPicture: String? = null


    constructor(isim: String?, kullanici_id: String?) {
        this.isim = isim
        this.kullanici_id = kullanici_id
        this.email = email
        this.profilPicture = profilPicture
    }


    override fun toString(): String {
        return "Kullanici(isim=$isim, kullanici_id=$kullanici_id,email=$email,profilPicture=$profilPicture)"
    }

}