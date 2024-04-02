package com.example.learngerman


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment

/**
 * A simple [Fragment] subclass.
 */
class PpEkleFragment : DialogFragment() {

    lateinit var tvGaleridenSec: TextView
    lateinit var tvKameradanSec: TextView



    interface onProfilResimListener{
        fun getResimYolu(resimPath: Uri?)
        fun getResimBitmap(bitmap: Bitmap)
    }

    lateinit var mResimListener: onProfilResimListener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        var v= inflater.inflate(R.layout.fragment_not_ekle, container, false)

        tvGaleridenSec=v.findViewById(R.id.tvGaleridenSec)
        tvKameradanSec=v.findViewById(R.id.tvKameradanFoto)

        tvGaleridenSec.setOnClickListener {
            var intent= Intent(Intent.ACTION_GET_CONTENT)
            intent.type="image/*"
            startActivityForResult(intent,100)
        }
        tvKameradanSec.setOnClickListener {
            var intent= Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent,200)
        }



        return v
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //galeriden resim seciliyor
        if(requestCode==100 && resultCode== Activity.RESULT_OK && data!= null){

            var galeridenSecilenResimYolu=data.data
            mResimListener.getResimYolu(galeridenSecilenResimYolu)
            dismiss()

        } //kameradan resim seciliyor
        else if(requestCode==100 && resultCode== Activity.RESULT_OK && data!= null){

            var kameradanÇekilenResim: Bitmap
            kameradanÇekilenResim=data.extras!!.get("data") as Bitmap
            mResimListener.getResimBitmap(kameradanÇekilenResim)
            dismiss()

        }

    }

    override fun onAttach(context: Context?) {

        mResimListener=activity as onProfilResimListener

        super.onAttach(context)
    }
}
