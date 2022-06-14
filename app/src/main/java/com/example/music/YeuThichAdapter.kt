package com.example.music

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.music.databinding.YeuthichViewBinding

class YeuThichAdapter(private val context: Context, private var musicList: ArrayList<Music>):
    RecyclerView.Adapter<YeuThichAdapter.MyHolder>() {

    class MyHolder(binding: YeuthichViewBinding): RecyclerView.ViewHolder(binding.root) {
        val anh  = binding.anhBHYT
        val name = binding.tenBHYT
        val tenCS= binding.tenCSYT
        val root = binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(YeuthichViewBinding.inflate(LayoutInflater.from(context), parent,false))
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.name.text = musicList[position].tenBH
        holder.tenCS.text = musicList[position].TacGia
        Glide.with(context)
            .load(musicList[position].imgBH)
            .apply(RequestOptions().placeholder(R.drawable.nhac).centerCrop())
            .into(holder.anh)
        holder.root.setOnClickListener {
            val intent = Intent(context, PlayerActivity::class.java)
            intent.putExtra("index",position)
            intent.putExtra("class", "YeuThichAdapter")
            ContextCompat.startActivities(context, arrayOf(intent),null)
        }
    }

    override fun getItemCount(): Int {
        return musicList.size
    }

}