package com.example.learngerman.Login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.learngerman.MainActivity
import com.example.learngerman.R
import com.example.learngerman.SharedNotesActivity
import com.example.learngerman.SifremiunuttumDialogFragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.fragment_sifremiunuttum_dialog.*

class LoginActivity : AppCompatActivity() {

   lateinit var mAuthStateListener:FirebaseAuth.AuthStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

       initMyAuthStateListener()

        textViewLoginRegister.setOnClickListener {
            var intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        textViewForgetmyPassword.setOnClickListener {
            var dialogSifreyiTekrarGonder=SifremiunuttumDialogFragment()
            dialogSifreyiTekrarGonder.show(supportFragmentManager,"gosterdialogsifre")
        }

        btnLogin.setOnClickListener {

            if (editTextLoginEmail.text.isNotEmpty() && editTextLoginPassword.text.isNotEmpty()) {
                progressBarGoster()

                FirebaseAuth.getInstance().signInWithEmailAndPassword(editTextLoginEmail.text.toString(), editTextLoginPassword.text.toString())
                    .addOnCompleteListener(object:OnCompleteListener<AuthResult>{
                        override fun onComplete(p0: Task<AuthResult>) {
                            Toast.makeText(this@LoginActivity, p0.isSuccessful.toString(), Toast.LENGTH_SHORT).show()

                            if(p0.isSuccessful){
                                progressBarGizle()
                                Toast.makeText(this@LoginActivity, "Giriş başarılı "+FirebaseAuth.getInstance().currentUser?.email, Toast.LENGTH_SHORT).show()

                            }
                            else{
                                progressBarGizle()
                                Toast.makeText(this@LoginActivity, "hatalı giriş", Toast.LENGTH_SHORT)

                            }
                        }

                    })

            }
        }

    }


    private fun progressBarGoster() {
        progressBarLogin.visibility = View.VISIBLE
    }

    private fun progressBarGizle() {
        progressBarLogin.visibility = View.INVISIBLE

    }

    override fun onStart() {
        super.onStart()
        FirebaseAuth.getInstance().addAuthStateListener (mAuthStateListener)
    }

    override fun onStop() {
        super.onStop()
        FirebaseAuth.getInstance().removeAuthStateListener (mAuthStateListener)
    }

    private fun initMyAuthStateListener(){
        mAuthStateListener=object:FirebaseAuth.AuthStateListener{
            override fun onAuthStateChanged(p0: FirebaseAuth) {
              var kullanici=p0.currentUser
                if(kullanici!=null){
                    if(kullanici.isEmailVerified){
                        progressBarGizle()
                        Toast.makeText(this@LoginActivity, "Mail onaylanmış giriş yapılabilir", Toast.LENGTH_SHORT)
                        var intent=Intent(this@LoginActivity,MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    else{
                        Toast.makeText(this@LoginActivity, "mail adresinizi onaylayın lütfen", Toast.LENGTH_SHORT)
                        FirebaseAuth.getInstance().signOut()
                    }
                }
            }

        }
    }
}
