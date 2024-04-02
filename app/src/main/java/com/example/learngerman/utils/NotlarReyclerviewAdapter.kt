package com.example.learngerman.utils

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.renderscript.ScriptGroup
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.example.learngerman.NotEkleFragment
import com.example.learngerman.Notlar
import com.example.learngerman.R
import com.squareup.picasso.Picasso
import com.tonyodev.fetch2.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.singleline_not_item.view.*
import org.greenrobot.eventbus.EventBus
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.net.URI
import java.net.URL
import java.util.jar.Manifest
import java.io.FileOutputStream


class NotlarReyclerviewAdapter(var tumNotlar: ArrayList<NotlarModel>, var myContext: Context) :
    RecyclerView.Adapter<NotlarReyclerviewAdapter.MyNotlarViewHolder>() {

        private var fetch: Fetch? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyNotlarViewHolder {
        var view =
            LayoutInflater.from(myContext).inflate(R.layout.singleline_not_item, parent, false)
        return MyNotlarViewHolder(view)
    }

    override fun getItemCount(): Int {
        return tumNotlar.size
    }

    override fun onBindViewHolder(holder: MyNotlarViewHolder, position: Int) {
        holder.setData(tumNotlar.get(position))
        holder.itemView.setOnLongClickListener() {

            Toast.makeText(myContext, "uzun tıklandı"+tumNotlar.get(position).not, Toast.LENGTH_SHORT).show()

            EventBus.getDefault().postSticky(EventBusData.imageurlGonder(tumNotlar.get(position).not!!))



            true
        }
    }


    class MyNotlarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tumLayout = itemView as ConstraintLayout
        var Notimage = tumLayout.imgLayout


        fun setData(oankiNotlar: NotlarModel) {
            Picasso.get().load(oankiNotlar.not).resize(100, 100).into(itemView.imgLayout)
//            Picasso.get().load(oankiNotlar.not).into(itemView.imageView2)
        }
    }


}