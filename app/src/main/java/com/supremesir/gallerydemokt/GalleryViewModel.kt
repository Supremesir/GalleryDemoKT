package com.supremesir.gallerydemokt

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import kotlin.math.ceil

/**
 * @author HaoFan Fang
 * @date 2020/4/23 16:41
 */

const val  DATA_STATUS_CAN_LOAD_MORE = 0
const val DATA_STATUS_NO_MORE = 1
const val DATA_STATUS_NETWORK_ERROR = 2

class GalleryViewModel(application: Application) : AndroidViewModel(application) {

    private val _dataStatusLive = MutableLiveData<Int>()
    val dataStatusLive: LiveData<Int>
        get() = _dataStatusLive

    // 通过不可改变的 LiveData 获取 MutableLiveData，实现数据封装
    private val _photoListLive = MutableLiveData<List<PhotoItem>>()
    val photoListLive: LiveData<List<PhotoItem>>
        get() = _photoListLive

    private val keyWords = arrayOf("cat", "dog", "car", "bee", "phone", "flower", "animal")
    private val perPage = 50

    private var currentPage = 1
    private var totalPage = 1
    private var currentKey = "cat"
    private var isNewQuery = true
    private var isLoading = false

    init {
        resetQuery()
    }

    // TODO: 下拉到底，继续从网站请求数据
    fun resetQuery() {
        currentPage = 1
        totalPage = 1
        currentKey = keyWords.random()
        isNewQuery = true
        fetchData()
    }

    // TODO: 根据搜索框接受关键词进行搜索
    fun fetchData() {
        if (isLoading) return
        isLoading = true
        // 所有的内容已经全部加载, 没有新内容可以加载
        if (currentPage > totalPage) {
            _dataStatusLive.value = DATA_STATUS_NO_MORE
            return
        }
        val stringRequest = StringRequest(
            Request.Method.GET,
            getUrl(),
            Response.Listener {
                with(Gson().fromJson(it, Pixabay::class.java)){
                    // celi(1.1) = 2
                    totalPage = ceil(totalHits.toDouble() / perPage).toInt()
                    if (isNewQuery) {
                        _photoListLive.value = this.hits.toList()
                        Log.d("fetch", "重新请求成功")
                    } else {
                        // flatten() 将两个 list 扁平化形成新的一维 list
                        _photoListLive.value = arrayListOf(_photoListLive.value!!, this.hits.toList()).flatten()
                        Log.d("fetch", "追加请求成功")
                    }
                }
                _dataStatusLive.value = DATA_STATUS_CAN_LOAD_MORE
                isLoading = false
                isNewQuery = false
                currentPage++
            },
            Response.ErrorListener {
                isLoading = true
                _dataStatusLive.value = DATA_STATUS_NETWORK_ERROR
                Log.d("fetch", "请求失败，$it")
            }
        ).also {
            VolleySingleton.getInstance(getApplication()).requestQueue.add(it)
        }
//        VolleySingleton.getInstance(getApplication()).requestQueue.add(stringRequest)
    }

    private fun getUrl(): String {
        val url =
            "https://pixabay.com/api/?key=16144591-adae3cf7f07751722a20825cf&q=${currentKey}&per_page=${perPage}&page=${currentPage}"
        Log.d("fetch", url)
        return url
    }

}