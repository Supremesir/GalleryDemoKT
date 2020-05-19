package com.supremesir.gallerydemokt

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import kotlinx.android.synthetic.main.fragment_gallery.*

/**
 * A simple [Fragment] subclass.
 */
class GalleryFragment : Fragment() {

    private lateinit var galleryViewModel: GalleryViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_gallery, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
        val galleryAdapter = GalleryAdapter()
        recycleView.apply {
            adapter = galleryAdapter
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }

        galleryViewModel =
            ViewModelProvider(requireActivity()).get(GalleryViewModel::class.java)
        galleryViewModel.photoListLive.observe(requireActivity(), Observer {
            Log.d("fetch","LiveData 更新成功")
            swipeRefreshLayoutGallery.isRefreshing = false
            galleryAdapter.submitList(it)
        })

        galleryViewModel.dataStatusLive.observe(requireActivity(), Observer {
            // 将其值观察到的变化传递给 GalleryAdapter，在 Adapter 中进行试图变化操作
            galleryAdapter.footerViewStatus = it
            // 若网络原因，则停止刷新
            if (it == DATA_STATUS_NETWORK_ERROR) {
                swipeRefreshLayoutGallery.isRefreshing = false
            }
        })

        swipeRefreshLayoutGallery.setOnRefreshListener {
            Log.d("fetch","下拉刷新，重新请求数据")
            galleryViewModel.resetQuery()
        }

        recycleView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy < 0) return
                val intArray = IntArray(2)
                val layoutManager = recyclerView.layoutManager as StaggeredGridLayoutManager
                layoutManager.findLastVisibleItemPositions(intArray)
                if (intArray[0] == galleryAdapter.itemCount - 1) {
                    galleryViewModel.fetchData()
                }

            }
        })
    }

    // 加载 Menu 资源
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu, menu)
        val searchView = menu.findItem(R.id.app_bar_search).actionView as SearchView
        searchView.maxWidth = 500
        searchView.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {
                    TODO("Not yet implemented")
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    TODO("Not yet implemented")
                }

            }
        )

    }

    // 实现 Menu Item 点击事件
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.refresh -> {
                swipeRefreshLayoutGallery.isRefreshing = true
                // 为请求数据延时1s，保证转动效果的出现
                Handler().postDelayed(Runnable { galleryViewModel.resetQuery() }, 1000)

            }
        }
        return super.onOptionsItemSelected(item)
    }
}
