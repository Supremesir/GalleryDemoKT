package com.supremesir.gallerydemokt

import android.content.Context
import android.util.Log
import androidx.paging.PageKeyedDataSource
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson

/**
 * @author HaoFan Fang
 * @date 2020/5/23 16:58
 */

class PixabayDataSource(private val context: Context) : PageKeyedDataSource<Int, PhotoItem>() {
    private val queryKey = arrayOf("cat", "dog", "car", "bee", "phone", "flower", "animal").random()
    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, PhotoItem>
    ) {
        val url =
            "https://pixabay.com/api/?key=16144591-adae3cf7f07751722a20825cf&q=${queryKey}&per_page=50&page=1"
        StringRequest(
            Request.Method.GET,
            url,
            Response.Listener {
                val dataList = Gson().fromJson(it, Pixabay::class.java).hits.toList()
                callback.onResult(dataList, null, 2)
            },
            // 错误处理，在分页加载中非常重要
            Response.ErrorListener {
                Log.d("fetch","paging 初始化加载错误")
            }
        ).also { VolleySingleton.getInstance(context).requestQueue.add(it) }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, PhotoItem>) {
        val url =
            "https://pixabay.com/api/?key=16144591-adae3cf7f07751722a20825cf&q=${queryKey}&per_page=50&page=${params.key}"
        StringRequest(
            Request.Method.GET,
            url,
            Response.Listener {
                val dataList = Gson().fromJson(it, Pixabay::class.java).hits.toList()
                callback.onResult(dataList, params.key + 1)
            },
            Response.ErrorListener {
                Log.d("fetch","paging 下一页加载错误")
            }
        ).also { VolleySingleton.getInstance(context).requestQueue.add(it) }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, PhotoItem>) {
    }

}