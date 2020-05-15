package com.supremesir.gallerydemokt

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.fragment_pager_photo.*
import kotlinx.android.synthetic.main.pager_photo_view.view.*

/**
 * A simple [Fragment] subclass.
 */

const val REQUEST_WRITE_EXTERNAL_STORAGE = 1

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
        PagerPhotoListAdapter().apply {
            viewPager2.adapter = this
            submitList(photoList)
        }

        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                photoTag.text = getString(R.string.photo_tag, position + 1, photoList?.size)
            }
        })

        // 保证 大图加载的是点击的小图，而不是从第一个显示
        viewPager2.setCurrentItem(arguments?.getInt("PHOTO_POSITION") ?: 0, false)
//        arguments?.getInt("PHOTO_POSITION")?.let {
//            // false 关闭平滑滚动的效果
//            viewPager2.setCurrentItem(it, false)
//        }

        saveButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_WRITE_EXTERNAL_STORAGE
                )
            } else {
                savePhoto()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_WRITE_EXTERNAL_STORAGE ->
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    savePhoto()
                } else {
                    Toast.makeText(requireContext(), "请授权存储权限以保存图片", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun savePhoto() {
        val holder =
            (viewPager2[0] as RecyclerView).findViewHolderForAdapterPosition(viewPager2.currentItem) as PagerPhotoViewHolder
        val bitmap = holder.itemView.pagerPhoto.drawable.toBitmap()
//        // API < 29 时，可用
//        if (MediaStore.Images.Media.insertImage(
//                requireActivity().contentResolver,
//                bitmap,
//                "",
//                ""
//            ) == null
//        ) {
//            Toast.makeText(requireContext(), "存储失败", Toast.LENGTH_SHORT).show()
//        } else {
//            Toast.makeText(requireContext(), "存储成功", Toast.LENGTH_SHORT).show()
//        }
        val saveUri = requireContext().contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            ContentValues()
        )?: kotlin.run {
            Toast.makeText(requireContext(), "存储失败", Toast.LENGTH_SHORT).show()
            return
        }

        requireContext().contentResolver.openOutputStream(saveUri).use {
            // 压缩存储大文件耗费很长时间，需要放在工作线程里
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)) {
                Toast.makeText(requireContext(), "存储成功", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "存储失败", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
