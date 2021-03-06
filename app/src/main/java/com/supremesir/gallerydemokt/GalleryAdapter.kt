package com.supremesir.gallerydemokt

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
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

class GalleryAdapter(private val galleryViewModel: GalleryViewModel) :
    PagedListAdapter<PhotoItem, RecyclerView.ViewHolder>(DiffCallback) {

    // 当重新进入 GalleryFragment 后，尝试重新加载
    // 解决，网络错误时，在进入 PagerPhotoFragment 后恢复网络后回到 GalleryFragment 后，不会重新加载的问题
    init {
        galleryViewModel.retry()
    }

    private var networkStatus: NetworkStatus? = null

    // 管理 footer 显示，实现第一次加载时不显示 footer
    private var hasFooter = false

    object DiffCallback : DiffUtil.ItemCallback<PhotoItem>() {
        override fun areItemsTheSame(oldItem: PhotoItem, newItem: PhotoItem): Boolean {
            // === 表示判断是否是同一个对象
            return oldItem.photoId == newItem.photoId
        }

        override fun areContentsTheSame(oldItem: PhotoItem, newItem: PhotoItem): Boolean {
            return oldItem == newItem
        }
    }

    fun updateNetworkStatus(networkStatus: NetworkStatus?) {
        this.networkStatus = networkStatus
        if (networkStatus == NetworkStatus.INITIAL_LOADING) {
            hideFooter()
        } else {
            showFooter()
        }
    }

    private fun hideFooter() {
        if (hasFooter) {
            notifyItemRemoved(itemCount - 1)
        }
        hasFooter = false
    }

    private fun showFooter() {
        // 若此时已经有 Footer 在显示，则刷新 Footer
        if (hasFooter) {
            notifyItemChanged(itemCount - 1)
        } else {
            hasFooter = true
            notifyItemInserted(itemCount - 1)
        }
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + if (hasFooter) 1 else 0
    }

    override fun getItemViewType(position: Int): Int {
        return if (hasFooter && position == itemCount - 1) R.layout.gallery_footer else R.layout.gallery_cell
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.gallery_cell ->
                PhotoViewHolder.newInstance(parent).also { holder ->
                    holder.itemView.setOnClickListener {
                        Bundle().apply {
                            putInt("PHOTO_POSITION", holder.adapterPosition)
                            getItem(holder.adapterPosition).also {
                                putIntArray("PHOTO_SIZE", intArrayOf(it!!.photoHeight, it.photoWidth))
                            }
                            // 此处的 this 代表该 Bundle
                            holder.itemView.findNavController()
                                .navigate(R.id.action_galleryFragment_to_pagerPhotoFragment, this)
                        }
                    }
                }
            else ->
                FooterViewHolder.newInstance(parent).also {
                    it.itemView.setOnClickListener {
                        galleryViewModel.retry()
                    }
                }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        // 若 getItem 返回值为空，直接 return
        when (holder.itemViewType) {
            R.layout.gallery_footer -> (holder as FooterViewHolder).bindWithNetworkStatus(
                networkStatus
            )
            else -> {
                val photoItem = getItem(position) ?: return
                (holder as PhotoViewHolder).bindWithPhotoItem(photoItem)
            }
        }
    }

}

class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    companion object {
        fun newInstance(parent: ViewGroup): PhotoViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.gallery_cell, parent, false)
            return PhotoViewHolder(view)
        }
    }

    fun bindWithPhotoItem(photoItem: PhotoItem) {
        with(itemView) {
            shimmerLayoutCell.apply {
                setShimmerColor(0x55FFFFFF)
                setShimmerAngle(0)
                startShimmerAnimation()
            }
            textViewUser.text = photoItem.photoUser
            textViewLikes.text = photoItem.photoLikes.toString()
            textViewFavorites.text = photoItem.photoFavorites.toString()
        }
        Glide.with(itemView)
            .load(photoItem.previewUrl)
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
                        itemView.shimmerLayoutCell?.stopShimmerAnimation()
                    }
                }

            })
            .into(itemView.imageView)
    }
}

class FooterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    companion object {
        fun newInstance(parent: ViewGroup): FooterViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.gallery_footer, parent, false)
            // 将 footer 占满屏幕宽度
            (view.layoutParams as StaggeredGridLayoutManager.LayoutParams).isFullSpan = true
            return FooterViewHolder(view)
        }
    }

    fun bindWithNetworkStatus(networkStatus: NetworkStatus?) {
        with(itemView) {
            when (networkStatus) {
                NetworkStatus.FAILED -> {
                    textViewLoading.text = resources.getString(R.string.network_error_tag)
                    progressBarLoading.visibility = View.GONE
                    isClickable = true
                }
                NetworkStatus.COMPLETED -> {
                    textViewLoading.text = resources.getString(R.string.completed_tag)
                    progressBarLoading.visibility = View.GONE
                    isClickable = false
                }
                else -> {
                    textViewLoading.text = resources.getString(R.string.loading_tag)
                    progressBarLoading.visibility = View.VISIBLE
                    isClickable = false
                }
            }
        }
    }
}