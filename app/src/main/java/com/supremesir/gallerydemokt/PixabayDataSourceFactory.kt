package com.supremesir.gallerydemokt

import android.content.Context
import androidx.paging.DataSource


/**
 * @author HaoFan Fang
 * @date 2020/5/24 16:48
 */

class PixabayDataSourceFactory(private val context: Context) : DataSource.Factory<Int, PhotoItem>() {
    override fun create(): DataSource<Int, PhotoItem> {
        return PixabayDataSource(context)
    }
}