package com.example.mobilliumcase.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.example.mobilliumcase.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.target.Target
import java.text.SimpleDateFormat
import java.util.*

const val DEFAULT_JUMP_THRESHOLD = 20
const val DEFAULT_SPEED_FACTOR = 1f

fun printLog(message: String, tag: String = "TestLog") = Log.d(tag,message)

fun showToast(context: Context?, message: String) = Toast.makeText(context,message,Toast.LENGTH_SHORT).show()

fun String.convertToFormattedDate(): String? {
    val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(this)
    return if(date != null) SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(date) else this
}

fun View.setGone(){
    this.visibility = View.GONE
}

fun View.setVisible(){
    this.visibility = View.VISIBLE
}

fun ImageView.loadWithGlide(imageUrl:String, progressBar: ProgressBar) =
    Glide.with(context).load(imageUrl).addListener(object: RequestListener<Drawable> {
        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
            this@loadWithGlide.setImageResource(R.drawable.ic_error_48)
            progressBar.setGone()
            return false
        }

        override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
            progressBar.setGone()
            return false
        }
    }).transform(RoundedCorners(12)).into(this)

fun<T> requestFlow(call: suspend () -> Response<T>): Flow<NetworkResponse<T>> = flow {
    emit(NetworkResponse.Loading)

    try {
        val response = call()

        if (response.isSuccessful) {
            response.body()?.let { data ->
                emit(NetworkResponse.Success(data))
            }
        } else {
            emit(NetworkResponse.Failure(errorMessage = response.message()))
        }
    } catch (e: Exception) {
        emit(NetworkResponse.Failure(e.message ?: e.toString()))
    }
}.flowOn(Dispatchers.IO)

// Credits: https://medium.com/flat-pack-tech/quickly-scroll-to-the-top-of-a-recyclerview-da15b717f3c4
suspend fun RecyclerView.quickScrollToTop(
    jumpThreshold: Int = DEFAULT_JUMP_THRESHOLD,
    speedFactor: Float = DEFAULT_SPEED_FACTOR
) {
    val layoutManager = layoutManager as LinearLayoutManager

    val smoothScroller = object : LinearSmoothScroller(context) {
        init {
            targetPosition = 0
        }

        override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics?) =
            super.calculateSpeedPerPixel(displayMetrics) / speedFactor
    }

    val jumpBeforeScroll = layoutManager.findFirstVisibleItemPosition() > jumpThreshold
    if (jumpBeforeScroll) {
        layoutManager.scrollToPositionWithOffset(jumpThreshold, 0)
        awaitFrame()
    }

    layoutManager.startSmoothScroll(smoothScroller)
}
