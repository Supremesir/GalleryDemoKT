package com.supremesir.gallerydemokt

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.fragment_gallery.*

/**
 * A simple [Fragment] subclass.
 */
class GalleryFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_gallery, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val galleryAdapter = GalleryAdapter()
        recycleView.apply {
            adapter = galleryAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
        }

        val galleryViewModel =
            ViewModelProvider(requireActivity()).get(GalleryViewModel::class.java)
        galleryViewModel.photoListLive.observe(requireActivity(), Observer {
            Log.d("fetch","LiveData 更新成功")
            swipeRefreshLayoutGallery.isRefreshing = false
            galleryAdapter.submitList(it)
        })
        galleryViewModel.photoListLive.value ?: galleryViewModel.fetchData()

        swipeRefreshLayoutGallery.setOnRefreshListener {
            Log.d("fetch","下拉刷新，重新请求数据")
            galleryViewModel.fetchData()
        }
    }

}
