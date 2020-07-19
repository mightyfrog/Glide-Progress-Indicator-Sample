package org.mightyfrog.android.glideprogressindicatorsample

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        private const val URL = // test image
            "https://upload.wikimedia.org/wikipedia/commons/2/23/Mountaintop_of_Seehorn_%28Davos%29.jpg"

        private val options: RequestOptions = RequestOptions() // no image caching
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            load()
        }
    }

    private fun load() {
        onStartLoading()

        ProgressAppGlideModule.expect(URL, object : ProgressAppGlideModule.UIonProgressListener {
            override fun onProgress(bytesRead: Long, expectedLength: Long) {
                val percent = (100 * bytesRead / expectedLength).toInt()
                textView.text = percent.toString()
            }

            override val granularityPercentage: Float get() = 1.0f
        })

        Glide.with(imageView.context)
            .load(URL)
            .apply(options)
            .listener(object : RequestListener<Drawable?> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any,
                    target: Target<Drawable?>,
                    isFirstResource: Boolean
                ): Boolean {
                    ProgressAppGlideModule.forget(URL)
                    onFinishLoading()
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any,
                    target: Target<Drawable?>,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    ProgressAppGlideModule.forget(URL)
                    onFinishLoading()
                    return false
                }
            })
            .into(imageView)
    }

    private fun onStartLoading() {
        textView.visibility = View.VISIBLE
        progressBar.visibility = View.VISIBLE
    }

    private fun onFinishLoading() {
        textView.text = null
        textView.visibility = View.GONE
        progressBar.visibility = View.GONE
    }
}