package com.tech.vircle.base

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.tech.vircle.App
import com.tech.vircle.BR
import com.tech.vircle.R
import com.tech.vircle.base.connectivity.ConnectivityProvider
import com.tech.vircle.base.local.SharedPrefManager
import com.tech.vircle.base.network.ErrorCodes
import com.tech.vircle.base.network.NetworkError
import com.tech.vircle.databinding.ViewProgressSheetBinding
import com.tech.vircle.ui.auth.AuthActivity
import com.tech.vircle.utils.AlertManager
import com.tech.vircle.utils.event.NoInternetSheet
import com.tech.vircle.utils.hideKeyboard
import javax.inject.Inject

abstract class BaseActivity<Binding : ViewDataBinding> : AppCompatActivity(),
    ConnectivityProvider.ConnectivityStateListener {
    lateinit var progressDialogAvl: ProgressDialogAvl
    open val onRetry: (() -> Unit)? = null
    lateinit var binding: Binding
    val app: App
        get() = application as App

    private lateinit var connectivityProvider: ConnectivityProvider
    private var noInternetSheet: NoInternetSheet? = null

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout: Int = getLayoutResource()
        binding = DataBindingUtil.setContentView(this, layout)
        binding.setVariable(BR.vm, getViewModel())
        connectivityProvider = ConnectivityProvider.createProvider(this)
        connectivityProvider.addListener(this)
        progressDialogAvl = ProgressDialogAvl(this)
        setStatusBarColor(R.color.white)
        setStatusBarDarkText()
        onCreateView()

        val vm = getViewModel()
        binding.setVariable(BR.vm, vm)
        vm.onUnAuth.observe(this) {
            showUnauthorised()
        }
    }

    fun showUnauthorised() {
        sharedPrefManager.clear()
        val intent = Intent(this@BaseActivity, AuthActivity::class.java)
        intent.putExtra("baseActivity","Base")
        startActivity(intent)
        finishAffinity()
    }

    private fun setStatusBarColor(colorResId: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(this, colorResId)
        }
    }

    private fun setStatusBarDarkText() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }

    @Inject
    lateinit var sharedPrefManager: SharedPrefManager

    protected abstract fun getLayoutResource(): Int
    protected abstract fun getViewModel(): BaseViewModel
    protected abstract fun onCreateView()

    fun showToast(msg: String? = "Something went wrong !!") {
        Toast.makeText(this, msg ?: "Showed null value !!", Toast.LENGTH_SHORT).show()
    }

    override fun onStop() {
        super.onStop()
        hideKeyboard()
    }

    fun hideLoading() {
        progressDialogAvl.isLoading(false)
    }

    fun showLoading() {
        progressDialogAvl.isLoading(true)
    }


    fun onError(error: Throwable, showErrorView: Boolean) {
        if (error is NetworkError) {

            when (error.errorCode) {
                ErrorCodes.SESSION_EXPIRED -> {
                    showToast(getString(R.string.session_expired))
                    app.onLogout()
                }

                else -> AlertManager.showNegativeAlert(
                    this, error.message, getString(R.string.alert)
                )
            }
        } else {
            AlertManager.showNegativeAlert(
                this, getString(R.string.please_try_again), getString(R.string.alert)
            )
        }
    }

    override fun onDestroy() {
        connectivityProvider.removeListener(this)
        super.onDestroy()
    }

    override fun onStateChange(state: ConnectivityProvider.NetworkState) {
        if (noInternetSheet == null) {
            noInternetSheet = NoInternetSheet()
            noInternetSheet?.isCancelable = false
        }
        if (state.hasInternet()) {
            if (noInternetSheet?.isVisible == true) noInternetSheet?.dismiss()
        } else {
            if (noInternetSheet?.isVisible == false) noInternetSheet?.show(
                supportFragmentManager,
                noInternetSheet?.tag
            )
        }
    }

    private fun ConnectivityProvider.NetworkState.hasInternet(): Boolean {
        return (this as? ConnectivityProvider.NetworkState.ConnectedState)?.hasInternet == true
    }

    fun showToastError(msg: String? = "Something went wrong !!") {
        val inflater = layoutInflater
        val layout = inflater.inflate(R.layout.error_toast_item, null)

        val textView: AppCompatTextView = layout.findViewById(R.id.tvErrorToast)
        textView.text = msg ?: "Showed null value !!"

        val toast = Toast(this)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = layout
        toast.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, 50)
        toast.show()
    }


    fun showToastSuccess(msg: String? = "Something went wrong !!") {
        val inflater = layoutInflater
        val layout = inflater.inflate(R.layout.successful_toast_item, null)
        val textView: AppCompatTextView = layout.findViewById(R.id.tvSuccessToast)
        textView.text = msg ?: "Showed null value !!"

        val toast = Toast(this)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = layout
        toast.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, 50)
        toast.show()
    }

    fun showToastInfo(msg: String? = "Something went wrong !!") {
        val inflater = layoutInflater
        val layout = inflater.inflate(R.layout.info_toast_item, null)
        val textView: TextView = layout.findViewById(R.id.tvInfoToast)
        textView.text = msg ?: "Showed null value !!"

        val toast = Toast(this)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = layout
        toast.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, 50)
        toast.show()
    }

}