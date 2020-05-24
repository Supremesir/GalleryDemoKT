package com.supremesir.gallerydemokt

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.paging.toLiveData

/**
 * @author HaoFan Fang
 * @date 2020/4/23 16:41
 */

class GalleryViewModel(application: Application) : AndroidViewModel(application) {
    val pagedListLiveData = PixabayDataSourceFactory(application).toLiveData(1)
    fun resetQuery() {
        pagedListLiveData.value?.dataSource?.invalidate()
    }

}