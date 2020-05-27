package com.supremesir.gallerydemokt

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource


/**
 * @author HaoFan Fang
 * @date 2020/5/24 16:48
 */

class PixabayDataSourceFactory(private val context: Context) :
    DataSource.Factory<Int, PhotoItem>() {

    // DataSource 中的 networkStatus 的值需要传递到 ViewModel 中，才能在 Fragment 中观察
    // 一种方式是，通过构造函数的层层传递
    // 更好的方式是在 DataSourceFactory 中，将创建好的 DataSource 实例储存为MutableLiveData，在 ViewModel 中访问
    private var _pixabayDataSource=  MutableLiveData<PixabayDataSource>()
    val pixabayDataSource: LiveData<PixabayDataSource> = _pixabayDataSource

    override fun create(): DataSource<Int, PhotoItem> {
        return PixabayDataSource(context).also { _pixabayDataSource.postValue(it) }
    }
}