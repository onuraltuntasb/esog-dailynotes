package com.example.learngerman

import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.learngerman.utils.EventBusData
import com.example.learngerman.utils.NotlarModel
import com.example.learngerman.utils.NotlarReyclerviewAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_shared_notes.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import android.graphics.Bitmap
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T



class SharedNotesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shared_notes)

    }

    override fun onStart() {
        super.onStart()

        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(sticky = true)
    internal fun onUrlGonder(resminUrlsi: EventBusData.imageurlGonder){
        var gelenResminUrlsi=resminUrlsi.url

        Picasso.get().load(gelenResminUrlsi).into(imgShowBigger)

        btnDownloadNote.setOnClickListener {
            var uri=saveImage((imgShowBigger.drawable as BitmapDrawable).bitmap ,"Note")

            Toast.makeText(this@SharedNotesActivity,"Note is downloaded",Toast.LENGTH_SHORT).show()
        }

    }

    private fun saveImage(bitmap:Bitmap, title:String): Uri {
        // Get the image from drawable resource as drawable object


        // Get the bitmap from drawable object


        // Save image to gallery
        val savedImageURL = MediaStore.Images.Media.insertImage(
            contentResolver,
            bitmap,
            title,
            "Image of $title"
        )

        // Parse the gallery image url to uri
        return Uri.parse(savedImageURL)
    }
}




