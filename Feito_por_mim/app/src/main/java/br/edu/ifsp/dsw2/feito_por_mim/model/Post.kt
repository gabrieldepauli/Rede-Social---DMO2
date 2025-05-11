package br.edu.ifsp.dsw2.feito_por_mim.model

import android.graphics.Bitmap

class Post(private val descricao: String, private val foto: Bitmap, private val localizacao: String, private val username: String) {

    public fun getDescription() : String{
        return descricao
    }

    public fun getPhoto() : Bitmap{
        return foto
    }

    public fun getLocation(): String{
        return localizacao
    }

    public fun getUsername(): String{
        return username
    }

}