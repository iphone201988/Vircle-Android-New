package com.tech.vircle.ui.auth.forgot

import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.tech.vircle.R
import com.tech.vircle.base.BaseFragment
import com.tech.vircle.base.BaseViewModel
import com.tech.vircle.data.api.Constants
import com.tech.vircle.data.model.ForgotPasswordResponse
import com.tech.vircle.databinding.FragmentCodeVerificationBinding
import com.tech.vircle.ui.auth.AuthCommonVM
import com.tech.vircle.utils.BindingUtils
import com.tech.vircle.utils.Status
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CodeVerificationFragment : BaseFragment<FragmentCodeVerificationBinding>() {
    private val viewModel: AuthCommonVM by viewModels()
    private lateinit var otpETs: Array<AppCompatEditText?>
    private var isOtpComplete = false
    private var userId: String? = null
    private var userEmail: String? = null
    override fun getLayoutResource(): Int {
        return R.layout.fragment_code_verification
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // click
        initOnClick()
        // view
        initView()
        userId = arguments?.getString("userId")
        userEmail = arguments?.getString("userEmail")
        // observer
        initObserver()
    }


    /**
     * Initialize onClick
     */
    private fun initOnClick() {
        viewModel.onClick.observe(viewLifecycleOwner) {
            when (it?.id) {
                // back button click
                R.id.backButton -> {
                    findNavController().popBackStack()
                }
                // lets start  button click
                R.id.btnNext -> {
                    if (validate()) {
                        verifyAccountApi()
                    }
                }
                // resend data
                R.id.ivSend -> {
                    if (userEmail?.isNotEmpty() == true) {
                        val data = HashMap<String, Any>()
                        data["email"] = userEmail.toString()
                        viewModel.forgotEmailApi(Constants.USER_FORGOT_PASSWORD, data)
                    }
                }
            }
        }

        binding.root.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            binding.root.getWindowVisibleDisplayFrame(rect)
            val screenHeight = binding.root.rootView.height
            val keypadHeight = screenHeight - rect.bottom
            if (keypadHeight > screenHeight * 0.15) {
                Log.d("Keyboard", "Visible")
            } else {
                binding.apply {
                    otpET1.clearFocus()
                    otpET2.clearFocus()
                    otpET3.clearFocus()
                    otpET4.clearFocus()
                    otpET5.clearFocus()
                    otpET6.clearFocus()
                }
            }
        }
    }

    /** verifyAccount api call **/
    private fun verifyAccountApi() {
        try {
            val otpData =
                "${binding.otpET1.text}" + "${binding.otpET2.text}" + "${binding.otpET3.text}" + "${binding.otpET4.text}" + "${binding.otpET5.text}" + "${binding.otpET6.text}"
            val data = HashMap<String, Any>()
            if (otpData.isNotEmpty()) {
                data["userId"] = userId.toString()
                data["otp"] = otpData.toInt()
                viewModel.codeVerificationApi(Constants.USER_VERIFY_OTP, data)
            }

        } catch (e: Exception) {
            Log.e("error", "verifyAccount: $e")
        }
    }

    /*** view ***/
    private fun initView() {
        otpETs = arrayOf(
            binding.otpET1,
            binding.otpET2,
            binding.otpET3,
            binding.otpET4,
            binding.otpET5,
            binding.otpET6
        )
        otpETs.forEachIndexed { index, editText ->
            editText?.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int,
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    if (!s.isNullOrEmpty() && index != otpETs.size - 1) {
                        otpETs[index + 1]?.requestFocus()
                    }

                    // Check if all OTP fields are filled
                    isOtpComplete = otpETs.all { it!!.text?.isNotEmpty() == true }

                }
            })

            editText?.setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                    if (editText.text?.isEmpty() == true && index != 0) {
                        otpETs[index - 1]?.apply {
                            text?.clear()
                            requestFocus()
                        }
                    }
                }
                // Check if all OTP fields are filled
                isOtpComplete = otpETs.all { it!!.text?.isNotEmpty() == true }

                false
            }
        }
    }

    /** api response observer ***/
    private fun initObserver() {
        viewModel.observeCommon.observe(viewLifecycleOwner) {
            when (it?.status) {
                Status.LOADING -> {
                    showLoading()
                }

                Status.SUCCESS -> {
                    when (it.message) {
                        "codeVerificationApi" -> {
                            try {
                                val myDataModel: ForgotPasswordResponse? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (myDataModel != null) {
                                    showSuccessToast(myDataModel.message.toString())
                                    var bundle = Bundle()
                                    bundle.putString("userId", myDataModel.userId)
                                    BindingUtils.navigateWithSlide(
                                        findNavController(),
                                        R.id.navigateToSetUpPasswordFragment,
                                        bundle
                                    )
                                }
                            } catch (e: Exception) {
                                Log.e("error", "codeVerificationApi: $e")
                            } finally {
                                hideLoading()
                            }
                        }

                        "forgotEmailApi" -> {
                            try {
                                val myDataModel: ForgotPasswordResponse? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (myDataModel != null) {
                                    showSuccessToast(myDataModel.message.toString())
                                }

                            } catch (e: Exception) {
                                Log.e("error", "forgotEmailApi: $e")
                            } finally {
                                hideLoading()
                            }
                        }
                    }
                }

                Status.ERROR -> {
                    hideLoading()
                    showErrorToast(it.message.toString())
                }

                else -> {
                }
            }
        }
    }

    /*** add validation ***/
    private fun validate(): Boolean {
        val first = binding.otpET1.text.toString().trim()
        val second = binding.otpET2.text.toString().trim()
        val third = binding.otpET3.text.toString().trim()
        val four = binding.otpET4.text.toString().trim()
        val five = binding.otpET5.text.toString().trim()
        val six = binding.otpET6.text.toString().trim()
        if (first.isEmpty()) {
            showInfoToast("Please enter valid otp")
            return false
        } else if (second.isEmpty()) {
            showInfoToast("Please enter valid otp")
            return false
        } else if (third.isEmpty()) {
            showInfoToast("Please enter valid otp")
            return false
        } else if (four.isEmpty()) {
            showInfoToast("Please enter valid otp")
            return false
        } else if (five.isEmpty()) {
            showInfoToast("Please enter valid otp")
            return false
        } else if (six.isEmpty()) {
            showInfoToast("Please enter valid otp")
            return false
        }
        return true
    }
}