package com.supremesir.gallerydemokt

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Transformations
import androidx.paging.toLiveData

/**
 * @author HaoFan Fang
 * @date 2020/4/23 16:41
 */

class GalleryViewModel(application: Application) : AndroidViewModel(application) {
    private val factory = PixabayDataSourceFactory(application)
    val pagedListLiveData = factory.toLiveData(1)
    val networkStatus = Transformations.switchMap(factory.pixabayDataSource) {it.networkStatus}!!
    fun resetQuery() {
        pagedListLiveData.value?.dataSource?.invalidate()
    }
    fun retry() {
        // invoke() 表明执行
        factory.pixabayDataSource.value?.retry?.invoke()
    }

}