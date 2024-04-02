package com.example.learngerman.Login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.learngerman.Kullanici
import com.example.learngerman.MainActivity
import com.example.learngerman.Notlar
import com.example.learngerman.R
import com.example.learngerman.utils.EventbusDataEvents
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*
import org.greenrobot.eventbus.EventBus

class RegisterActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)


        btnRegisterNext.setOnClickListener {

            if(edittxtEmail.text.isNotEmpty() && edittxtName.text.isNotEmpty() && edittxtPassword.text.isNotEmpty()){

                yeniUyeKayit(edittxtEmail.text.toString(),edittxtPassword.text.toString(),edittxtName.text.toString())
            }else{
                Toast.makeText(this,"Boş alanları doldurunuz",Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun yeniUyeKayit(mail: String, sifre: String,name: String) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(mail, sifre)
            .addOnCompleteListener(object : OnCompleteListener<AuthResult> {
                override fun onComplete(p0: Task<AuthResult>) {
                    if (p0.isSuccessful) {
                        progressBarGizle()
                        onayMailiGonder()

                        var veritabaninaEklenecekKullanici= Kullanici()
                        veritabaninaEklenecekKullanici.kullanici_id=FirebaseAuth.getInstance().currentUser?.uid
                        veritabaninaEklenecekKullanici.isim=name
                        veritabaninaEklenecekKullanici.email=mail
                        veritabaninaEklenecekKullanici.profilPicture="https://firebasestorage.googleapis.com/v0/b/learngerman-7566f.appspot.com/o/images%2Fusers8wbacfMnWCUtoSm5lnqMdKm77EF3%2Fprofile_resim?alt=media&token=7c204563-736c-48ea-8725-827e0e1d2b0a"
//
//                        var veritabaninaEklenecekNotlar= Notlar()
//                        veritabaninaEklenecekNotlar.not=""
//
//
//                        FirebaseDatabase.getInstance().reference
//                            .child("Notlar")
//                            .child("not")
//                            .setValue(veritabaninaEklenecekKullanici).addOnCompleteListener{
//                                if(it.isSuccessful){
//                                    Toast.makeText(this@RegisterActivity, "Dugum olusmalı", Toast.LENGTH_SHORT).show()
//                                }
//                            }

                        FirebaseDatabase.getInstance().reference
                            .child("Kullanici")
                            .child(FirebaseAuth.getInstance().currentUser!!.uid)
                            .setValue(veritabaninaEklenecekKullanici).addOnCompleteListener { task->

                                if(task.isSuccessful){
                                    Toast.makeText(this@RegisterActivity, "Üye kaydedildi:" + FirebaseAuth.getInstance().currentUser?.uid, Toast.LENGTH_SHORT).show()
                                    FirebaseAuth.getInstance().signOut()
                                    var intent=Intent(this@RegisterActivity,LoginActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }

                            }


                    }else{
                        progressBarGizle()
                        Toast.makeText(this@RegisterActivity,"Üye kaydedilemedi :"+p0.exception,Toast.LENGTH_SHORT).show()
                    }
                }
            })
    }

    private fun progressBarGoster(){
        progressBar.visibility=View.VISIBLE
    }
    private fun progressBarGizle(){
        progressBar.visibility=View.INVISIBLE

    }

    private fun onayMailiGonder(){
        var kullanici=FirebaseAuth.getInstance().currentUser
        if(kullanici!=null){
            kullanici.sendEmailVerification()
                .addOnCompleteListener(object:OnCompleteListener<Void>{
                    override fun onComplete(p0: Task<Void>) {
                        if(p0.isSuccessful){
                            Toast.makeText(this@RegisterActivity,"Mail atıldı onaylayın :",Toast.LENGTH_SHORT).show()
                        }else{
                            Toast.makeText(this@RegisterActivity,"Mail atılamadı:",Toast.LENGTH_SHORT).show()
                        }
                    }

                })
        }
    }


}
