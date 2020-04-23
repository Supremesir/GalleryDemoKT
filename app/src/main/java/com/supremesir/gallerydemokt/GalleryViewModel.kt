package com.supremesir.gallerydemokt

import android.app.Application
import android.app.DownloadManager
import android.content.ClipData
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson

/**
 * @author HaoFan Fang
 * @date 2020/4/23 16:41
 */

class GalleryViewModel(application: Application) : AndroidViewModel(application) {

    // 通过不可改变的 LiveData 获取 MutableLiveData，实现数据封装
    private val _photoListLive = MutableLiveData<List<PhotoItem>>()
    val photoListLive: LiveData<List<PhotoItem>>
        get() = _photoListLive

    fun fetchData() {
        val stringRequest = StringRequest(
            Request.Method.GET,
            getUrl(),
            Response.Listener {
                _photoListLive.value = Gson().fromJson(it, Pixabay::class.java).hits.toList()
            },
            Response.ErrorListener {
                Log.d("error", it.toString())
            }
        ).also {
            VolleySingleton.getInstance(getApplication()).requestQueue.add(it)
        }
    }

    private fun getUrl(): String {
        return "https://pixabay.com/api/?key=16144591-adae3cf7f07751722a20825cf&q=${keyWords.random()}"
    }

    private val keyWords = arrayOf("cat", "dog", "car", "bee", "phone", "flower", "animal")

}