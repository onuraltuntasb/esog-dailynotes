package com.example.learngerman.Models

class Notes {
    var user_id:String?=null
    var notes_id:String?=null
    var yuklenme_tarihi:String?=null
    var aciklama:String?=null
    var photo_url:String?=null

    constructor(
        user_id: String?,
        notes_id: String?,
        yuklenme_tarihi: String?,
        aciklama: String?,
        photo_url: String?
    ) {
        this.user_id = user_id
        this.notes_id = notes_id
        this.yuklenme_tarihi = yuklenme_tarihi
        this.aciklama = aciklama
        this.photo_url = photo_url
    }

    constructor()

    override fun toString(): String {
        return "Notes(user_id=$user_id, notes_id=$notes_id, yuklenme_tarihi=$yuklenme_tarihi, aciklama=$aciklama, photo_url=$photo_url)"
    }

}