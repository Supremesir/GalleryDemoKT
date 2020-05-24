package com.supremesir.gallerydemokt

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.paging.toLiveData

/**
 * @author HaoFan Fang
 * @date 2020/4/23 16:41
 */

const val  DATA_STATUS_CAN_LOAD_MORE = 0
const val DATA_STATUS_NO_MORE = 1
const val DATA_STATUS_NETWORK_ERROR = 2

class GalleryViewModel(application: Application) : AndroidViewModel(application) {
    val pagedListLiveData = PixabayDataSourceFactory(application).toLiveData(1)

}