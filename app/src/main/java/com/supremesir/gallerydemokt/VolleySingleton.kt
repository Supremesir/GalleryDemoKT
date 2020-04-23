package com.supremesir.gallerydemokt

import android.content.Context
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

/**
 * @author HaoFan Fang
 * @date 2020/4/23 17:02
 */

class VolleySingleton private constructor(context: Context){
    // companion object 相当于 Java 中的 static
    companion object{
        private var INSTANCE: VolleySingleton? = null
        fun getInstance(context: Context) =
            INSTANCE ?: synchronized(this) {
                VolleySingleton(context).also { INSTANCE = it }
            }
    }

    val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(context.applicationContext)
    }

}