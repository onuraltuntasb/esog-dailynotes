package com.example.learngerman

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.learngerman.Login.LoginActivity
import com.example.learngerman.utils.MessagesModel
import com.example.learngerman.utils.MessagesRecylerviewAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_sohbet.*

class SohbetActivity : AppCompatActivity() {

    lateinit var tumMesajlar: ArrayList<MessagesModel>
    var mMesajReferans_=FirebaseDatabase.getInstance().reference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sohbet)

        baslatMesajListener_()


        fabSendMessage.setOnClickListener {


            if(etGonderilenMesaj.text.isNotEmpty()){
                var ref=FirebaseDatabase.getInstance().reference

                FirebaseDatabase.getInstance().reference
                    .child("SohbetMessages").push()
                    .setValue(etGonderilenMesaj.text.toString())
//                rvSohbetOdalari.scrollToPosition(myAdapter!!.itemCount-1)
//                myAdapter.notifyDataSetChanged()
//                paylasilanMesajıOku()

            }
            else {
                Toast.makeText(this@SohbetActivity, "Lütfen Boş Mesaj Atmayiniz", Toast.LENGTH_SHORT).show()

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
                CikisYap()
                return true
            }
            R.id.menuSohbet->{
                var intent= Intent(this,SohbetActivity::class.java)
                startActivity(intent)
                return true
            }

            R.id.menuHesapAyarlari->{
                var intent= Intent(this,EditProfileActivity::class.java)
                startActivity(intent)
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun CikisYap() {
        FirebaseAuth.getInstance().signOut()
        var intent = Intent(this@SohbetActivity, LoginActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }

    private fun setupMessagesRecylerView() {
        var myAdapter=MessagesRecylerviewAdapter(tumMesajlar,this@SohbetActivity)
        var myRecylerView: RecyclerView?=rvSohbetOdalari
        myRecylerView?.layoutManager= GridLayoutManager(this,1)
        myAdapter.notifyDataSetChanged()
        rvSohbetOdalari.scrollToPosition(myAdapter.itemCount-1)
        myRecylerView?.adapter=myAdapter

    }

    var mValueEventListener:ValueEventListener=object:ValueEventListener{
        override fun onCancelled(p0: DatabaseError) {

        }

        override fun onDataChange(p0: DataSnapshot) {
            paylasilanMesajıOku()
        }

    }
    private fun baslatMesajListener_(){
        mMesajReferans_=FirebaseDatabase.getInstance().reference.child("SohbetMessages")
        mMesajReferans_?.addValueEventListener(mValueEventListener)
    }

    private fun paylasilanMesajıOku() {
        var referans = FirebaseDatabase.getInstance().getReference()
        var ref = referans.child("SohbetMessages")
        tumMesajlar = ArrayList<MessagesModel>()
        Toast.makeText(this@SohbetActivity, "Mesajı okuya girdi", Toast.LENGTH_SHORT).show()

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onDataChange(p0: DataSnapshot) {

                for (i in p0!!.children) {

                    var okunanNot = i.value
                    var obj= MessagesModel()
                    obj.message=okunanNot.toString()
//                    Toast.makeText(this@SohbetActivity,  obj.message, Toast.LENGTH_SHORT).show()

                    tumMesajlar.add(obj)
                    setupMessagesRecylerView()
                }

            }
        })

    }

}


