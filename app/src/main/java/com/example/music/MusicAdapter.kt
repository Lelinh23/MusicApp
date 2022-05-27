package com.example.music

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.music.databinding.MusicViewBinding

class MusicAdapter(private val context: Context, private val musicList: ArrayList<Music>):
    RecyclerView.Adapter<MusicAdapter.MyHolder>() {

    class MyHolder(binding: MusicViewBinding): RecyclerView.ViewHolder(binding.root) {

        val title = binding.NameMV
        val album = binding.AlbumMV
        val image = binding.anhMV
        val thoi_luong = binding.thoiLuongBh

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicAdapter.MyHolder {
        return MyHolder(MusicViewBinding.inflate(LayoutInflater.from(context), parent,false))
    }

    override fun onBindViewHolder(holder: MusicAdapter.MyHolder, position: Int) {
//        holder.title.text = musicList[position].tenBH
        holder.title.text = musicList[position].tenBH
//        holder.album.text = musicList[position].album
//        holder.thoi_luong.text = musicList[position].ThoiLuongBH.toString()
    }

    override fun getItemCount(): Int {
        return musicList.size
    }
}