package com.example.wallipop

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import carbon.widget.ImageView
import com.bumptech.glide.Glide

class FavoriteAdapter(private val context: Context,private val photlst : List<UserDefined>) :
    RecyclerView.Adapter<FavoriteAdapter.ViewHolder>(){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.favourite_recucle,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val photo = photlst[position]

        Glide.with(context).load(photo.imgurl).into(holder.imageview)
        holder.imageview.setOnClickListener{
            val int = Intent(context, favoriteshow::class.java)
            int.putExtra("power",photo.imgurl)
            context.startActivity(int)
        }
    }


    override fun getItemCount(): Int {
        return photlst.size
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
            val imageview : ImageView=itemView.findViewById(R.id.image_add_fav)
        }
}