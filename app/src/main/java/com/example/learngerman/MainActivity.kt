package com.example.learngerman

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.renderscript.Sampler
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.learngerman.Login.LoginActivity
import com.example.learngerman.utils.*
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.io.ByteArrayOutputStream
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity(), NotEkleFragment.onResimListener {

    var galeridenGelenURI: Uri? = null
    var kameradanGelenBitmap: Bitmap? = null
    var izinlerVerildi: Boolean=false
    lateinit var tumNotlar: ArrayList<NotlarModel>
    var myAdapter_:NotlarReyclerviewAdapter?=null
    var mMesajReferans=FirebaseDatabase.getInstance().reference

    override fun getResimYolu(resimPath: Uri?) {
        galeridenGelenURI = resimPath
        Picasso.get().load(galeridenGelenURI).resize(200, 200).into(imgGoster)
    }

    override fun getResimBitmap(bitmap: Bitmap) {
        kameradanGelenBitmap = bitmap
        imgGoster.setImageBitmap(bitmap)
    }

    inner class BackgroundResimCompress : AsyncTask<Uri, Void, ByteArray?> {
        var myBitmap: Bitmap? = null

        constructor() {}

        constructor(bm: Bitmap) {
            if (bm != null) {
                myBitmap = bm
            }
        }


        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg p0: Uri?): ByteArray? {
            //galerinden resim
            if (myBitmap == null) {
                myBitmap =
                    MediaStore.Images.Media.getBitmap(this@MainActivity.contentResolver, p0[0])
            }

            var resimByte: ByteArray? = null

            resimByte = convertBitmaptoByte(myBitmap, 100)


            return resimByte
        }

        private fun convertBitmaptoByte(myBitmap: Bitmap?, i: Int): ByteArray? {
            var stream = ByteArrayOutputStream()
            myBitmap?.compress(Bitmap.CompressFormat.PNG, i, stream)
            return stream.toByteArray()

        }

        override fun onProgressUpdate(vararg values: Void?) {
            super.onProgressUpdate(*values)
        }

        override fun onPostExecute(result: ByteArray?) {
            super.onPostExecute(result)
            uploadResimtoFirebase(result)

        }
    }

    private fun uploadResimtoFirebase(result: ByteArray?) {

        progressGoster()

        var firebaseurl: String? = null
        var storageReference = FirebaseStorage.getInstance().getReference()

        val filename = UUID.randomUUID().toString()
        var resimEklenecekYer = storageReference.child(filename)
        var uploadGorevi = resimEklenecekYer.putBytes(result!!)

        uploadGorevi.addOnSuccessListener(object : OnSuccessListener<UploadTask.TaskSnapshot> {
            override fun onSuccess(p0: UploadTask.TaskSnapshot?) {


                resimEklenecekYer.downloadUrl.addOnSuccessListener {
                    firebaseurl = it.toString()
                    FirebaseDatabase.getInstance().reference
                        .child("Notlar").push()
                        .setValue(firebaseurl)

//                    Toast.makeText(this@MainActivity, firebaseurl, Toast.LENGTH_SHORT).show()

                }.addOnFailureListener {
                    Toast.makeText(this@MainActivity, "hata :" + it.toString(), Toast.LENGTH_SHORT)
                        .show()
                }

                progressGizle()

            }

        }).addOnFailureListener(object : OnFailureListener {
            override fun onFailure(p0: Exception) {
                Toast.makeText(
                    this@MainActivity,
                    "Resim yüklenirken hata" + p0.toString(),
                    Toast.LENGTH_SHORT
                ).show()
            }

        })
    }

    lateinit var myAuthStateListener: FirebaseAuth.AuthStateListener
    @RequiresApi(Build.VERSION_CODES.O)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initAuthStateListener()


        /* image yüklemek için image'in üzerine tikla. */
        imgGoster.setOnClickListener {
            izinleriIste()

            if (izinlerVerildi) {
                var dialog = NotEkleFragment()
                dialog.show(supportFragmentManager, "fotosec")
            } else {
                izinleriIste()
            }

        }

        baslatMesajListener()

        btnRvUpdate.setOnClickListener {
                paylasilanNotuOku()


        }


        btnChatActivity.setOnClickListener {
            var intent = Intent(this@MainActivity, SohbetActivity::class.java)
            startActivity(intent)
        }
        btnProfilDuzenleActivity.setOnClickListener {
            var intent = Intent(this@MainActivity, EditProfileActivity::class.java)
            startActivity(intent)
        }


        btnNotUpload.setOnClickListener {
            paylasilanNotuOku()
            var Notlar = FirebaseAuth.getInstance().currentUser!!
            var bilgileriGuncelle = UserProfileChangeRequest.Builder()
                .build()
            Notlar.updateProfile(bilgileriGuncelle)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Değişiklikler Yapıldı", Toast.LENGTH_SHORT).show()
                        paylasilanNotuOku()
                    }
                }

            if (galeridenGelenURI != null) {
                fotografCompressed(galeridenGelenURI!!)
            } else if (kameradanGelenBitmap != null) {
                fotografCompressed(kameradanGelenBitmap!!)
            }

        }

    }

    var mValueEventListener:ValueEventListener=object:ValueEventListener{
        override fun onCancelled(p0: DatabaseError) {

        }

        override fun onDataChange(p0: DataSnapshot) {
           paylasilanNotuOku()
        }

    }




    private fun paylasilanNotuOku() {
        var referans = FirebaseDatabase.getInstance().getReference()
        var ref = referans.child("Notlar")
        tumNotlar = ArrayList<NotlarModel>()

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onDataChange(p0: DataSnapshot) {

                for (i in p0!!.children) {


                    var okunanNot = i.value
                    var obj=NotlarModel()
                    obj.not=okunanNot.toString()

//                    Picasso.get().load(okunanNot.toString()).resize(100, 100).into(imgGoster2)
                    tumNotlar.add(obj)
                    setupMesajlarRecylerView()
                    myAdapter_?.notifyDataSetChanged()

//                    Toast.makeText(this@MainActivity, "", Toast.LENGTH_SHORT).show()
                }

            }
        })
    }

    private fun setupMesajlarRecylerView() {
        var myRecylerView:RecyclerView?=rvNotlar
        myRecylerView?.layoutManager=GridLayoutManager(this,4)
        var myAdapter_=NotlarReyclerviewAdapter(tumNotlar,this)
        myAdapter_.notifyDataSetChanged()
        myRecylerView?.adapter=myAdapter_

    }

    private fun baslatMesajListener(){
        mMesajReferans=FirebaseDatabase.getInstance().reference.child("Notlar")
        mMesajReferans?.addValueEventListener(mValueEventListener)
    }


    private fun fotografCompressed(galeridenGelenURI: Uri) {
        var compressed = BackgroundResimCompress()
        compressed.execute(galeridenGelenURI)
    }

    private fun fotografCompressed(kameradanGelenBitmap: Bitmap) {
        var compressed = BackgroundResimCompress(kameradanGelenBitmap)
        var uri = null
        compressed.execute(uri)
    }

    private fun izinleriIste() {
        var izinler = arrayOf(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA
        )
        if (ContextCompat.checkSelfPermission(
                this,
                izinler[0]
            ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this,
                izinler[1]
            ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, izinler[2]) == PackageManager.PERMISSION_GRANTED
        )
            izinlerVerildi = true
        else {
            ActivityCompat.requestPermissions(this, izinler, 150)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        if (requestCode == 150) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                grantResults[2] == PackageManager.PERMISSION_GRANTED
            ) {
                var dialog = NotEkleFragment()
                dialog.show(supportFragmentManager, "fotosec")
            } else {
                Toast.makeText(this, "Tüm izinler verilmeli", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initAuthStateListener() {
        myAuthStateListener = object : FirebaseAuth.AuthStateListener {
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                if (p0.currentUser != null) {

                } else {
                    var intent = Intent(this@MainActivity, LoginActivity::class.java)
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.anamenu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item?.itemId) {
            R.id.cikisyapMenu -> {
                cikisyap()
                return true
            }
            R.id.menuSohbet->{
                var intent=Intent(this,SohbetActivity::class.java)
                startActivity(intent)
                return true
            }

            R.id.menuHesapAyarlari->{
                var intent=Intent(this,EditProfileActivity::class.java)
                startActivity(intent)
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun cikisyap() {
        FirebaseAuth.getInstance().signOut()
        var intent = Intent(this@MainActivity, LoginActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }

    @Subscribe()
    internal fun onUrlGonder(resminUrlsi: EventBusData.imageurlGonder){
        var gelenResminUrlsi=resminUrlsi.url
        val intent=Intent(this,SharedNotesActivity::class.java)
        startActivity(intent)

    }

    override fun onStart() {
        super.onStart()
        FirebaseAuth.getInstance().addAuthStateListener { myAuthStateListener }
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        if (myAuthStateListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener { myAuthStateListener }
        }
        EventBus.getDefault().unregister(this)
    }

    override fun onResume() {
        super.onResume()
        kullaniciyiKontrolet()
    }

    private fun kullaniciyiKontrolet() {
        var kullanici = FirebaseAuth.getInstance().currentUser
        if (kullanici == null) {
            var intent = Intent(this@MainActivity, LoginActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()
        }
    }

    fun progressGoster() {
        progressBar.visibility = View.VISIBLE
    }

    fun progressGizle() {
        progressBar.visibility = View.INVISIBLE
        setupMesajlarRecylerView()

    }

}
