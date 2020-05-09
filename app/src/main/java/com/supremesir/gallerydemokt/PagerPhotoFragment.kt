package com.supremesir.gallerydemokt

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_pager_photo.*

/**
 * A simple [Fragment] subclass.
 */
class PagerPhotoFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pager_photo, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val photoList = arguments?.getParcelableArrayList<PhotoItem>("PHOTO_LIST")
        val pagerPhotoListAdapter = PagerPhotoListAdapter().apply {
            viewPager2.adapter = this
            submitList(photoList)
        }

    }

}
