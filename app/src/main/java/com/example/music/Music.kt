package com.example.music

import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

data class Music(val id: String, val tenBH:String, val album:String, val TacGia:String, 
                 val ThoiLuongBH:Long = 0, val link:String, val imgBH:String)

class DanhSachPhat{
    lateinit var name: String
    lateinit var playlist: ArrayList<Music>
    lateinit var createdBy: String
    lateinit var createOn: String
}
class DanhSachBH{
    var  ref: ArrayList<DanhSachPhat> = ArrayList()
}

fun DinhDangTime(ThoiLuongBH: Long):String{
    val phut = TimeUnit.MINUTES.convert(ThoiLuongBH, TimeUnit.MILLISECONDS)
    val giay = (TimeUnit.SECONDS.convert(ThoiLuongBH, TimeUnit.MILLISECONDS))-phut*TimeUnit.SECONDS.convert(1,TimeUnit.MINUTES)
    return String.format("%02d:%02d", phut, giay)
}
fun setViTriBH(increment: Boolean){
    if(!PlayerActivity.laplai){
        if(increment)
        {
            if(PlayerActivity.musicListPN.size - 1 == PlayerActivity.vitriBH)
                PlayerActivity.vitriBH = 0
            else ++PlayerActivity.vitriBH
        }else{
            if(0 == PlayerActivity.vitriBH)
                PlayerActivity.vitriBH = PlayerActivity.musicListPN.size-1
            else --PlayerActivity.vitriBH
        }
    }
}
fun exitApplication(){
    if (PlayerActivity.musicService != null) {
        PlayerActivity.musicService!!.audioManager.abandonAudioFocus(PlayerActivity.musicService)
        PlayerActivity.musicService!!.stopForeground(true)
        PlayerActivity.musicService!!.phat_nhac!!.release()
        PlayerActivity.musicService = null
    }
    exitProcess(1)
}
fun CheckerThich(id: String): Int {
    PlayerActivity.isFavourite = false
    YeuThichActivity.bh_yeu_thich.forEachIndexed { index, music ->
        if (id == music.id){
            PlayerActivity.isFavourite = true
            return index
        }
    }
    return -1
}
fun checkDataDSN(DSN: ArrayList<Music>): ArrayList<Music>{
    DSN.forEachIndexed { index, music ->
        val file = File(music.link)
        if (!file.exists()){
            DSN.removeAt(index)
        }
    }
    return DSN
}