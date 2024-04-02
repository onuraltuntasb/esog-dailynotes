package com.example.learngerman


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.example.learngerman.Login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.fragment_sifremiunuttum_dialog.*


/**
 * A simple [Fragment] subclass.
 */
class SifremiunuttumDialogFragment : DialogFragment() {

    lateinit var emailEdittext:EditText
    lateinit var mContext:FragmentActivity


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view= inflater.inflate(R.layout.fragment_sifremiunuttum_dialog, container, false)

        emailEdittext=view.findViewById(R.id.editTextSendEmail)
        mContext=requireActivity()


        var btnIptal=view.findViewById<Button>(R.id.btnForgetPasswordcancel)
        var btnGonder=view.findViewById<Button>(R.id.btnForgetPasswordSend)

        btnIptal.setOnClickListener(){
            dialog.dismiss()
        }

        btnGonder.setOnClickListener {

            if(editTextSendEmail.text.isNotEmpty())
            {
                FirebaseAuth.getInstance().sendPasswordResetEmail(editTextSendEmail.text.toString())
                    .addOnCompleteListener{
                            task ->
                        if(task.isSuccessful){
                            Toast.makeText(mContext,"Şifre sıfırlama maili gonderildi",Toast.LENGTH_LONG).show()
                            dialog.dismiss()
                        }else{
                            Toast.makeText(mContext,"Hata oluştu"+task.exception,Toast.LENGTH_LONG).show()
                            dialog.dismiss()

                        }
                    }
            }
            else{
                Toast.makeText(mContext,"Boş alanları doldurunuz",Toast.LENGTH_SHORT).show()
            }


        }

        return view
    }


}
