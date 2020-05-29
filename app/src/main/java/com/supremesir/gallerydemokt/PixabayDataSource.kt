package com.supremesir.gallerydemokt

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson

/**
 * @author HaoFan Fang
 * @date 2020/5/23 16:58
 */

enum class NetworkStatus {
    LOADING,
    FAILED,
    COMPLETED
}

class PixabayDataSource(private val context: Context) : PageKeyedDataSource<Int, PhotoItem>() {
    // 用来保存错误现场
    var retry: (() -> Any)? = null


//    // 在 Java 中，更推荐使用常量而不是枚举，节约对象的资源开销
//    // 而在 Kotlin 中，常量也是对象，因此更加推荐使用枚举
//    companion object {
//        const val LOADING = 0
//        const val FAILED = 1
//        const val COMPLETED = 2
//    }
    private val _networkStatus = MutableLiveData<NetworkStatus>()
    val networkStatus: LiveData<NetworkStatus> = _networkStatus

    private val queryKey = arrayOf("cat", "dog", "car", "bee", "phone", "flower", "animal").random()
    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, PhotoItem>
    ) {
        // 开始请求时就清除保存的函数，避免误操作
        retry = null
        // postValue 是线程安全的，无论主线程还是副线程都可以执行
        _networkStatus.postValue(NetworkStatus.LOADING)
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
                // 大括号表示保存一个 lambada 函数
                retry = { loadInitial(params, callback)}
                _networkStatus.postValue(NetworkStatus.FAILED)
                Log.d("fetch", "paging 初始化加载错误：$it")
            }
        ).also { VolleySingleton.getInstance(context).requestQueue.add(it) }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, PhotoItem>) {
        // 开始请求时就清除保存的函数，避免误操作
        retry = null
        _networkStatus.postValue(NetworkStatus.LOADING)
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
                if (it.toString() == "com.android.volley.ClientError") {
                    _networkStatus.postValue(NetworkStatus.COMPLETED)
                } else {
                    retry = { loadAfter(params, callback)}
                    _networkStatus.postValue(NetworkStatus.FAILED)
                }
                Log.d("fetch", "paging 加载下一页错误：$it")
            }
        ).also { VolleySingleton.getInstance(context).requestQueue.add(it) }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, PhotoItem>) {
    }

}