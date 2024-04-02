package com.example.learngerman

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.learngerman.Login.LoginActivity
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.activity_main.*
import java.io.ByteArrayOutputStream

class EditProfileActivity : AppCompatActivity(),PpEkleFragment.onProfilResimListener {
    var galeridenGelenURI: Uri? = null
    var kameradanGelenBitmap: Bitmap? = null
    var izinlerVerildi: Boolean = false

    override fun getResimYolu(resimPath: Uri?) {
        galeridenGelenURI = resimPath
        Picasso.get().load(galeridenGelenURI).resize(200, 200).into(imgProfilePicture)
    }

    override fun getResimBitmap(bitmap: Bitmap) {
        kameradanGelenBitmap = bitmap
        imgProfilePicture.setImageBitmap(bitmap)
    }

    inner class BackgroundResimCompress_ : AsyncTask<Uri, Void, ByteArray?> {
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
                    MediaStore.Images.Media.getBitmap(
                        this@EditProfileActivity.contentResolver,
                        p0[0]
                    )
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
            uploadResimtoFirebase_(result)
        }
    }
    private fun uploadResimtoFirebase_(result: ByteArray?) {

        progressGoster()

        var firebaseurl: String? = null
        var storageReferans = FirebaseStorage.getInstance().getReference()
        var resimEklenecekYer =
            storageReferans.child("images/users" + FirebaseAuth.getInstance().currentUser?.uid + "/profile_resim")
        var uploadGorevi = resimEklenecekYer.putBytes(result!!)

        uploadGorevi.addOnSuccessListener(object : OnSuccessListener<UploadTask.TaskSnapshot> {
            override fun onSuccess(p0: UploadTask.TaskSnapshot?) {


                resimEklenecekYer.downloadUrl.addOnSuccessListener {
                    firebaseurl = it.toString()
                    FirebaseDatabase.getInstance().reference
                        .child("Kullanici")
                        .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                        .child("profilPicture")
                        .setValue(firebaseurl)

//                    Toast.makeText(this@MainActivity, firebaseurl, Toast.LENGTH_SHORT).show()

                }.addOnFailureListener {
                    Toast.makeText(this@EditProfileActivity, "hata :" + it.toString(), Toast.LENGTH_SHORT)
                        .show()
                }
                    progressGizle()

                }
            })
//            Toast.makeText(
//                this@EditProfileActivity,
//                " sdafdasfasfasdf",
//                Toast.LENGTH_SHORT
//            ).show()


        }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        var user = FirebaseAuth.getInstance().currentUser!!
        edittxtProfilName.setText(user?.displayName.toString())

        imgProfilePicture.setOnClickListener {
            if (izinlerVerildi) {
                var dialog = PpEkleFragment()
                dialog.show(supportFragmentManager, "fotosec")
            } else {
                izinleriIste()
            }
            var diaglog = PpEkleFragment()
            diaglog.show(supportFragmentManager, "fotosec")
        }

        kullaniciBilgileriniOku()

//        Chat.setOnClickListener {
//            var intent = Intent(this@EditProfileActivity, MainActivity::class.java)
//            startActivity(intent)
//        }
//        btnNotPaylasActivityEdit.setOnClickListener {
//            var intent = Intent(this@EditProfileActivity, EditProfileActivity::class.java)
//            startActivity(intent)
//        }


        btnPasswordSend.setOnClickListener {
            FirebaseAuth.getInstance()
                .sendPasswordResetEmail(FirebaseAuth.getInstance().currentUser?.email.toString())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            this,
                            "Şifre sıfırlama maili gonderildi",
                            Toast.LENGTH_LONG
                        ).show()

                    } else {
                        Toast.makeText(
                            this,
                            "Hata oluştu" + task.exception,
                            Toast.LENGTH_LONG
                        ).show()


                    }
                }
        }

        btnProfileSave.setOnClickListener {
            kullaniciBilgileriniOku()
            if (edittxtProfilName.text.toString().isNotEmpty()) {
                user = FirebaseAuth.getInstance().currentUser!!
                var bilgilerGuncelle = UserProfileChangeRequest.Builder()
                    .setDisplayName(edittxtProfilName.text.toString())
                    .build()
                user.updateProfile(bilgilerGuncelle)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {

                            FirebaseDatabase.getInstance().reference
                                .child("Kullanici")
                                .child(FirebaseAuth.getInstance().currentUser?.uid!!)
                                .child("isim")
                                .setValue(edittxtProfilName.text.toString())





                        }

                        if (galeridenGelenURI != null) {
                            fotografCompressed(galeridenGelenURI!!)
                        } else if (kameradanGelenBitmap != null) {
                            fotografCompressed(kameradanGelenBitmap!!)
                        }
                    }
            } else {
                Toast.makeText(
                    this@EditProfileActivity,
                    "kullanici adini doldurunuz",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        btnProfileUpdateMailPassword.setOnClickListener {
            if (edittxtProfilPassword.text.isNotEmpty()) {

                var credential = EmailAuthProvider.getCredential(
                    user.email.toString(),
                    edittxtProfilPassword.text.toString()
                )
                user.reauthenticate(credential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            updateLayout.visibility = View.VISIBLE

                            btnProfileMailUpdate.setOnClickListener {
                                mailAdresiniGuncelle()
                            }

                            btnProfilePasswordUpdate.setOnClickListener {
                                sifreBilgisiniGuncelle()
                            }

                        } else {
                            Toast.makeText(
                                this@EditProfileActivity,
                                "Şu anki şifrenizi yanlış girdiniz ",
                                Toast.LENGTH_SHORT
                            ).show()
                            updateLayout.visibility = View.INVISIBLE
                        }
                    }

            } else {
                Toast.makeText(
                    this@EditProfileActivity,
                    "Güncellemeler için geçerli şifrenizi yazınız",
                    Toast.LENGTH_SHORT
                ).show()
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
                cikisYap()
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

    private fun cikisYap() {
        FirebaseAuth.getInstance().signOut()
        var intent = Intent(this@EditProfileActivity, LoginActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }


    fun fotografCompressed(galeridenGelenURI: Uri) {
        var compressed = BackgroundResimCompress_()
        compressed.execute(galeridenGelenURI)
    }

    fun fotografCompressed(kameradanGelenBitmap: Bitmap) {
        var compressed = BackgroundResimCompress_(kameradanGelenBitmap)
        var uri = null
        compressed.execute(uri)
    }

    fun izinleriIste() {
        var izinler = arrayOf(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA
        )
        if (ContextCompat.checkSelfPermission(
                this,
                izinler[0]
            ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this,
                izinler[1]
            ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this,
                izinler[2]
            ) == PackageManager.PERMISSION_GRANTED
        )
            izinlerVerildi = true
        else {
            ActivityCompat.requestPermissions(this, izinler, 150)
        }
    }

    fun kullaniciBilgileriniOku() {
//        Toast.makeText(this@EditProfileActivity,"girdi", Toast.LENGTH_SHORT).show()
        var referans = FirebaseDatabase.getInstance().getReference()
        var kullanici_ = FirebaseAuth.getInstance().currentUser
        txtProfileEmail.text = kullanici_?.email

        var sorgu = referans.child("Kullanici")
            .orderByKey()
            .equalTo(kullanici_?.uid)
        sorgu.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                Toast.makeText(this@EditProfileActivity,"asdfasdf", Toast.LENGTH_SHORT).show()



                for (singleSnapshot in p0!!.children) {
//                    Toast.makeText(this@EditProfileActivity,"girdi", Toast.LENGTH_SHORT).show()

                    var okunanKullanici_ = singleSnapshot.getValue(Kullanici::class.java)

                    edittxtProfilName.setText(okunanKullanici_?.isim)
                    Toast.makeText(this@EditProfileActivity,okunanKullanici_?.isim, Toast.LENGTH_SHORT).show()

                    Toast.makeText(this@EditProfileActivity,okunanKullanici_?.profilPicture, Toast.LENGTH_SHORT).show()
                    Picasso.get().load(okunanKullanici_?.profilPicture).resize(200, 200).into(imgProfilePicture)


                }
            }

        })

    }

    fun loginSayfasinaYonlendir() {
        var intent = Intent(this@EditProfileActivity, LoginActivity::class.java)
        startActivity((intent))
        finish()
    }

    fun sifreBilgisiniGuncelle() {
        var user_ = FirebaseAuth.getInstance().currentUser!!
        if (user_ != null) {
            user_.updatePassword(edittxtNewPassword.text.toString())
                .addOnCompleteListener {
                    FirebaseAuth.getInstance().signOut()
                    loginSayfasinaYonlendir()
                    Toast.makeText(
                        this@EditProfileActivity,
                        "Sifreniz degisti tekrar giris yapın",
                        Toast.LENGTH_SHORT
                    ).show()

                }
        }
    }

    fun mailAdresiniGuncelle() {
        var user_ = FirebaseAuth.getInstance().currentUser!!
        if (user_ != null) {

            FirebaseAuth.getInstance()
                .fetchSignInMethodsForEmail(edittxtNewEmail.text.toString())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        if (task.getResult()?.signInMethods.isNullOrEmpty()) {

                            user_.updatePassword(edittxtNewEmail.text.toString())
                                .addOnCompleteListener {

                                    Toast.makeText(
                                        this@EditProfileActivity,
                                        "maliniz değisti tekrar giris yapın",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    FirebaseAuth.getInstance().signOut()
                                    loginSayfasinaYonlendir()
                                }

                        } else {
                            Toast.makeText(
                                this@EditProfileActivity,
                                "email kullanımda",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@EditProfileActivity,
                            "mail güncellenemedi",
                            Toast.LENGTH_SHORT
                        ).show()
                    }


                }
        }
    }


    fun progressGoster() {
        progressBar2.visibility = View.VISIBLE
    }

    fun progressGizle() {
        progressBar2.visibility = View.INVISIBLE
    }

    override fun onBackPressed() {
//        var intent = Intent(this@EditProfileActivity, MainActivity::class.java)
//        startActivity((intent))
        super.onBackPressed()
    }

}

