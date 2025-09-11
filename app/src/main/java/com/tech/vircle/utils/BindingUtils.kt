package com.tech.vircle.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.ActivityCompat
import androidx.databinding.BindingAdapter
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.google.gson.Gson
import com.tech.vircle.R
import com.tech.vircle.data.api.Constants
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object BindingUtils {

    @BindingAdapter("setImageFromUrl")
    @JvmStatic
    fun setImageFromUrl(image: ShapeableImageView, url: String?) {
        url?.let {
            val cleanUrl = it.removePrefix("/")
            Glide.with(image.context)
                .load(Constants.MEDIA_BASE_URL + cleanUrl)
                .placeholder(R.drawable.profilephoto)
                .error(R.drawable.profilephoto)
                .into(image)

            Log.d("dgfdgggfdg", "setImageFromUrl: ${Constants.MEDIA_BASE_URL + cleanUrl}")
        }
    }


    @JvmStatic
    @BindingAdapter("setTimeSpent")
    fun setTimeSpent(textView: TextView, totalMinutes: Int?) {
        totalMinutes?.let {
            textView.text = if (it < 60) {
                "$it min"
            } else {
                val hours = it / 60
                val minutes = it % 60
                if (minutes == 0) {
                    "$hours hr"
                } else {
                    "$hours hr $minutes min"
                }
            }
        } ?: run {
            textView.text = "0 min"
        }
    }


    inline fun <reified T> parseJson(json: String): T? {
        return try {
            val gson = Gson()
            gson.fromJson(json, T::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    @BindingAdapter("setTime")
    @JvmStatic
    fun setTime(textView: AppCompatTextView, time: String?) {
        if (time != null) {
            val utcTime = time
            val localTime = formatUtcToLocalTime(utcTime)
            textView.text = localTime
        }
    }

    fun formatUtcToLocalTime(utcDate: String?): String {
        // Input format
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")
        // Output format
        val outputFormat = SimpleDateFormat("hh:mm a", Locale.US)
        outputFormat.timeZone = TimeZone.getDefault()
        val date = inputFormat.parse(utcDate)!!
        return outputFormat.format(date)
    }


    val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.READ_MEDIA_AUDIO,
            Manifest.permission.CAMERA
        )
    } else {
        arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        )
    }


    fun hasPermissions(context: Context?, permissions: Array<String>?): Boolean {
        if (context != null && permissions != null) {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(
                        context, permission
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return false
                }
            }
        }
        return true
    }


    @BindingAdapter("setImageFromInt")
    @JvmStatic
    fun setImageFromInt(image: AppCompatImageView, url: Int?) {
        if (url != null) {
            image.setImageResource(url)
        }
    }

    fun navigateWithSlide(navController: NavController, destinationId: Int, bundle: Bundle?) {
        val navOptions = NavOptions.Builder().setEnterAnim(R.anim.slide_in_right)
            .setExitAnim(R.anim.slide_out_left).setPopEnterAnim(R.anim.slide_in_left)
            .setPopExitAnim(R.anim.slide_out_right).build()

        navController.navigate(destinationId, bundle, navOptions)
    }


    fun preventMultipleClick(view: View) {
        view.isEnabled = false
        view.postDelayed({
            try {
                view.isEnabled = true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, 1000)
    }


    /**
     * Updates progress bar and moves text accordingly
     */
    fun updateProgress(
        activity: Activity,
        progressBar: ProgressBar,
        tvProgress: AppCompatTextView,
        progressStatus: Int,
        label: String
    ) {
        progressBar.progress = progressStatus

        progressBar.post {
            val maxSizePoint = Point().also {
                activity.windowManager.defaultDisplay.getSize(it)
            }
            val maxX = maxSizePoint.x
            val offsetPx = 14

            val progressPx = (progressStatus * (progressBar.width - 2)) / progressBar.max
            val textViewX = progressPx - (tvProgress.width / 2) + offsetPx

            val finalX = if (tvProgress.width + textViewX > maxX) {
                maxX - tvProgress.width - 16
            } else {
                textViewX + 16
            }

            tvProgress.x = (if (finalX < 0) 16 else finalX).toFloat()
            tvProgress.text = label
        }
    }

    /*** set date **/
    private fun parseUtcToLocalDate(utcDate: String?): Date? {
        if (utcDate.isNullOrEmpty()) return null
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            val date = sdf.parse(utcDate)

            // Convert to local timezone
            date
        } catch (e: Exception) {
            null
        }
    }

    internal fun getDateLabel(utcDate: String?): String {
        val date = parseUtcToLocalDate(utcDate) ?: return ""

        val calendar = Calendar.getInstance()
        calendar.time = date

        val today = Calendar.getInstance()
        val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }

        return when {
            isSameDay(calendar, today) -> "Today"
            isSameDay(calendar, yesterday) -> "Yesterday"
            else -> {
                val sdf = SimpleDateFormat("d MMM yyyy", Locale.getDefault())
                sdf.format(date)
            }
        }
    }

    private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(
            Calendar.DAY_OF_YEAR
        )
    }


}
