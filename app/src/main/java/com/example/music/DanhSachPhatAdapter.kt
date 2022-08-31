package com.example.music

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.music.databinding.PlayListViewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class DanhSachPhatAdapter(private val context: Context, private var DanhSachPhat: ArrayList<DanhSachPhat>):
    RecyclerView.Adapter<DanhSachPhatAdapter.MyHolder>() {

    class MyHolder(binding: PlayListViewBinding): RecyclerView.ViewHolder(binding.root) {
        val anh  = binding.anhPlayList
        val name = binding.tenPlayList
        val root = binding.root
        val xoa = binding.btnXoaPlayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(PlayListViewBinding.inflate(LayoutInflater.from(context), parent,false))
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.name.text = DanhSachPhat[position].name
        holder.name.isSelected = true
        holder.xoa.setOnClickListener {
            val builder = MaterialAlertDialogBuilder(context)
            builder.setTitle(DanhSachPhat[position].name)
                .setMessage("Ban muon xóa danh sách phát này?")
                .setPositiveButton("Yes"){ dialog, _ ->
                    DanhSachNhacActivity.DanhSachBH.ref.removeAt(position)
                    LamMoiPlayList()
                    dialog.dismiss()
                }
                .setNegativeButton("No"){ dialog, _ ->
                    dialog.dismiss()
                }
            val customDialog = builder.create()
            customDialog.show()
            customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
            customDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED)
        }
        holder.root.setOnClickListener {
            val intent = Intent(context, ChiTietDanhSachPhat::class.java)
            intent.putExtra("index", position)
            ContextCompat.startActivities(context, arrayOf(intent), null)
        }
        if (DanhSachNhacActivity.DanhSachBH.ref[position].playlist.size > 0){
            Glide.with(context)
                .load(DanhSachNhacActivity.DanhSachBH.ref[position].playlist[0].imgBH)
                .apply(RequestOptions().placeholder(R.drawable.nhac).centerCrop())
                .into(holder.anh)
        }
    }

    override fun getItemCount(): Int {
        return DanhSachPhat.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun  LamMoiPlayList(){
        DanhSachPhat = ArrayList()
        DanhSachPhat.addAll(DanhSachNhacActivity.DanhSachBH.ref)
        notifyDataSetChanged()
    }

}