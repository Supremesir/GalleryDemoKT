package com.supremesir.gallerydemokt

import android.app.Application
import android.app.DownloadManager
import android.content.ClipData
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest

/**
 * @author HaoFan Fang
 * @date 2020/4/23 16:41
 */

class GalleryViewModel(application: Application) : AndroidViewModel(application) {

    // 通过不可改变的 LiveData 获取 MutableLiveData，实现数据封装
    private val _photoListLive = MutableLiveData<List<PhotoItem>>()
    val photoListLive: LiveData<List<PhotoItem>>
    get() = _photoListLive


}