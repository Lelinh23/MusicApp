package com.example.music

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.music.databinding.FragmentNowPlayingBinding

class NowPlaying : Fragment() {

    companion object{
        @SuppressLint("StaticFieldLeak")
        lateinit var binding: FragmentNowPlayingBinding

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        requireContext().theme.applyStyle(MainActivity.currenTheme[MainActivity.themeIndex], true)
        val view = inflater.inflate(R.layout.fragment_now_playing, container, false)
        binding = FragmentNowPlayingBinding.bind(view)
        binding.root.visibility = View.INVISIBLE
        binding.btnPlay.setOnClickListener{
            if (PlayerActivity.isPlaying) dungBH()
            else phatBH()
        }
        binding.btnNext.setOnClickListener{
            setViTriBH(increment = true)
            PlayerActivity.musicService!!.createPhatNhac()
            Glide.with(this)
                .load(PlayerActivity.musicListPN[PlayerActivity.vitriBH].imgBH)
                .apply(RequestOptions().placeholder(R.drawable.nhac).centerCrop())
                .into(binding.anhSong)
            binding.songName.text = PlayerActivity.musicListPN[PlayerActivity.vitriBH].tenBH
            phatBH()
        }
        binding.root.setOnClickListener {
            val intent = Intent(requireContext(), PlayerActivity::class.java)
            intent.putExtra("index", PlayerActivity.vitriBH)
            intent.putExtra("class", "NowPlaying")
            ContextCompat.startActivity(requireContext(), intent, null)
        }
        return view
    }

    override fun onResume() {
        super.onResume()
        if (PlayerActivity.musicService != null){
            binding.root.visibility = View.VISIBLE
            binding.songName.isSelected = true
            Glide.with(this)
                .load(PlayerActivity.musicListPN[PlayerActivity.vitriBH].imgBH)
                .apply(RequestOptions().placeholder(R.drawable.nhac).centerCrop())
                .into(binding.anhSong)
            binding.songName.text = PlayerActivity.musicListPN[PlayerActivity.vitriBH].tenBH
            if (PlayerActivity.isPlaying){
                binding.btnPlay.setIconResource(R.drawable.icon_phat_bai_hat)
            }
            else binding.btnPlay.setIconResource(R.drawable.icon_dung_bai_hat)
        }
    }

    private fun phatBH(){
        PlayerActivity.isPlaying = true
        PlayerActivity.musicService!!.phat_nhac!!.start()
        binding.btnPlay.setIconResource(R.drawable.icon_phat_bai_hat)
    }
    private fun dungBH(){
        PlayerActivity.isPlaying = false
        PlayerActivity.musicService!!.phat_nhac!!.pause()
        binding.btnPlay.setIconResource(R.drawable.icon_dung_bai_hat)
    }
}