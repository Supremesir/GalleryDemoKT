package com.supremesir.gallerydemokt

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.gallery_cell.view.*
import kotlinx.android.synthetic.main.gallery_footer.view.*

/**
 * @author HaoFan Fang
 * @date 2020/4/23 21:54
 */

class GalleryAdapter : PagedListAdapter<PhotoItem, MyViewHolder>(DiffCallback) {

    object DiffCallback : DiffUtil.ItemCallback<PhotoItem>() {
        override fun areItemsTheSame(oldItem: PhotoItem, newItem: PhotoItem): Boolean {
            // === 表示判断是否是同一个对象
            return oldItem.photoId == newItem.photoId
        }

        override fun areContentsTheSame(oldItem: PhotoItem, newItem: PhotoItem): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val holder = MyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.gallery_cell, parent, false)
        )
        holder.itemView.setOnClickListener {
            Bundle().apply {
                // 因为在 list 的点击事件里，currentList 不可能为空
                putParcelableArrayList("PHOTO_LIST", ArrayList(currentList!!))
                putInt("PHOTO_POSITION", holder.adapterPosition)
                // 此处的 this 代表该 Bundle
                holder.itemView.findNavController().navigate(R.id.action_galleryFragment_to_pagerPhotoFragment, this)
            }
        }
        return holder
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // 若 getItem 返回值为空，直接 return
        val photoItem = getItem(position)?:return
        with(holder.itemView) {
            shimmerLayoutCell.apply {
                setShimmerColor(0x55FFFFFF)
                setShimmerAngle(0)
                startShimmerAnimation()
            }
            textViewUser.text = photoItem.photoUser
            textViewLikes.text = photoItem.photoLikes.toString()
            textViewFavorites.text = photoItem.photoFavorites.toString()
        }
        Glide.with(holder.itemView)
            .load(getItem(position)?.previewUrl)
            .placeholder(R.drawable.photo_placeholder)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    // 必须返回false，否则无法显示图片
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false.also {
                        // 加入 ?. 判空，
                        holder.itemView.shimmerLayoutCell?.stopShimmerAnimation()
                    }
                }

            })
            .into(holder.itemView.imageView)

    }

}

class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)