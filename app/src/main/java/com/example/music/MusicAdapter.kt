package com.example.music

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.music.databinding.MusicViewBinding

class MusicAdapter(private val context: Context, private var musicList: ArrayList<Music>,
                   private var chiTietDSP: Boolean = false, private val selectionActivity: Boolean = false):
    RecyclerView.Adapter<MusicAdapter.MyHolder>() {

    class MyHolder(binding: MusicViewBinding): RecyclerView.ViewHolder(binding.root) {

        val title = binding.NameMV
        val album = binding.AlbumMV
        val image = binding.anhMV
        val thoi_luong = binding.TimeMV
        val root = binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(MusicViewBinding.inflate(LayoutInflater.from(context), parent,false))
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.title.text = musicList[position].tenBH
        holder.album.text = musicList[position].TacGia
        holder.thoi_luong.text = DinhDangTime(musicList[position].ThoiLuongBH)
        Glide.with(context)
             .load(musicList[position].imgBH)
             .apply(RequestOptions().placeholder(R.drawable.nhac).centerCrop())
             .into(holder.image)
        when{
            chiTietDSP->{
                holder.root.setOnClickListener {
                    sendIntent(ref = "chiTietDSPAdapter", pos = position)
                }
            }
            selectionActivity->{
                holder.root.setOnClickListener {
                    if (addBh(musicList[position])){
                        holder.root.setBackgroundColor(ContextCompat.getColor(context,
                            R.color.music_icon_background))
                    }else{
                        holder.root.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
                    }
                }
            }
            else->{
                holder.root.setOnClickListener {
                    when{
                        MainActivity.timkiem -> sendIntent(ref = "MusicAdapterSearch", pos = position)
                        musicList[position].id == PlayerActivity.nowPlayingId -> sendIntent(ref = "NowPlaying", pos = position )
                        else->sendIntent(ref = "MusicAdapter", pos = position)
                    }
                }
            }
        }
    }

    private fun addBh(song: Music): Boolean {

        DanhSachNhacActivity.DanhSachBH.ref[ChiTietDanhSachPhat.currentPlaylistPos].playlist.forEachIndexed {  index, music ->
            if (song.id == music.id) {
                DanhSachNhacActivity.DanhSachBH.ref[ChiTietDanhSachPhat.currentPlaylistPos].playlist.removeAt(index)
                return false
            }
        }
        DanhSachNhacActivity.DanhSachBH.ref[ChiTietDanhSachPhat.currentPlaylistPos].playlist.add(song)
        return true
    }

    override fun getItemCount(): Int {
        return musicList.size
    }
    @SuppressLint("NotifyDataSetChanged")
    fun updateDanhSachNhac(seachList: ArrayList<Music>){
        musicList = ArrayList()
        musicList.addAll(seachList)
        notifyDataSetChanged()
    }
    private fun sendIntent(ref: String, pos: Int){
        val intent = Intent(context, PlayerActivity::class.java)
        intent.putExtra("index",pos)
        intent.putExtra("class", ref)
        ContextCompat.startActivities(context, arrayOf(intent),null)
    }
    @SuppressLint("NotifyDataSetChanged")
    fun LamMoiDanhSach(){
        musicList = ArrayList()
        musicList = DanhSachNhacActivity.DanhSachBH.ref[ChiTietDanhSachPhat.currentPlaylistPos].playlist
        notifyDataSetChanged()
    }
}