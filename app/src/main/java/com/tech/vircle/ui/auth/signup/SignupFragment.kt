package com.tech.vircle.ui.auth.signup

import android.text.InputType
import android.util.Log
import android.util.Patterns
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.tech.vircle.R
import com.tech.vircle.base.BaseFragment
import com.tech.vircle.base.BaseViewModel
import com.tech.vircle.data.api.Constants
import com.tech.vircle.data.model.UserRegistrationResponse
import com.tech.vircle.databinding.FragmentSignupBinding
import com.tech.vircle.ui.auth.AuthCommonVM
import com.tech.vircle.utils.BindingUtils
import com.tech.vircle.utils.Status
import dagger.hilt.android.AndroidEntryPoint
import java.util.TimeZone


@AndroidEntryPoint
class SignupFragment : BaseFragment<FragmentSignupBinding>() {
    private val viewModel: AuthCommonVM by viewModels()
    override fun getLayoutResource(): Int {
        return R.layout.fragment_signup
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // click
        initOnClick()
        // observer
        initObserver()
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
                        "createAccount" -> {
                            try {
                                val myDataModel: UserRegistrationResponse? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (myDataModel != null) {
                                    if (myDataModel.data != null) {
                                        myDataModel.data.user?.let { it1 ->
                                            sharedPrefManager.setLoginData(
                                                it1
                                            )
                                        }
                                    }
                                    BindingUtils.navigateWithSlide(
                                        findNavController(), R.id.navigateToAboutFragment, null
                                    )
                                }

                            } catch (e: Exception) {
                                Log.e("error", "createAccount: $e")
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
                // create account button click
                R.id.btnCreateAccount -> {
                    val name = binding.etYourName.text.toString().trim()
                    val subName = binding.etYourSubName.text.toString().trim()
                    val email = binding.etEmail.text.toString().trim()
                    val password = binding.etPassword.text.toString().trim()
                    if (validate()) {
                        val data = HashMap<String, Any>()
                        data["name"] = name
                        data["surname"] = subName
                        data["email"] = email
                        data["password"] = password
                        data["deviceToken"] = "123456789"
                        data["deviceType"] = "2"
                        data["timezone"] = getCurrentTimeZoneIdentifier()
                        viewModel.createAccount(Constants.USER_REGISTER, data)
                    }
                }
                // show or hide password click
                R.id.ivHidePassword -> {
                    showOrHidePassword()
                }
                // show or hide confirm password click
                R.id.ivConHidePassword -> {
                    showOrHideConfirmPassword()
                }
            }
        }
    }


    /*** show or confirm hide password **/
    private fun showOrHidePassword() {
        if (binding.etPassword.text.toString().trim().isNotEmpty()) {
            if (binding.etPassword.inputType == InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD) {
                binding.ivHidePassword.setImageResource(R.drawable.bxs_show)
                binding.etPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                binding.ivHidePassword.setImageResource(R.drawable.hide_password)
                binding.etPassword.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            binding.etPassword.setSelection(binding.etPassword.length())
        }
    }

    /*** show or hide confirm password **/
    private fun showOrHideConfirmPassword() {
        if (binding.etConPassword.text.toString().trim().isNotEmpty()) {
            if (binding.etConPassword.inputType == InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD) {
                binding.ivConHidePassword.setImageResource(R.drawable.bxs_show)
                binding.etConPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                binding.ivConHidePassword.setImageResource(R.drawable.hide_password)
                binding.etConPassword.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            binding.etConPassword.setSelection(binding.etConPassword.length())
        }
    }


    /*** add validation ***/
    private fun validate(): Boolean {
        val name = binding.etYourName.text.toString().trim()
        val subName = binding.etYourSubName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val confirmPassword = binding.etConPassword.text.toString().trim()

        if (name.isEmpty()) {
            showInfoToast("Please enter name")
            return false
        } else if (subName.isEmpty()) {
            showInfoToast("Please enter sur name")
            return false
        } else if (email.isEmpty()) {
            showInfoToast("Please enter email")
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showInfoToast("Please enter a valid email")
            return false
        } else if (password.isEmpty()) {
            showInfoToast("Please enter password")
            return false
        } else if (password.length < 6) {
            showInfoToast("Password must be at least 6 characters")
            return false
        } else if (!password.any { it.isUpperCase() }) {
            showInfoToast("Password must contain at least one uppercase letter")
            return false
        } else if (confirmPassword.isEmpty()) {
            showInfoToast("Please enter confirm password")
            return false
        } else if (password != confirmPassword) {
            showInfoToast("Password and Confirm password do not match")
            return false
        }

        return true
    }

    /** get current time zone ***/
    private fun getCurrentTimeZoneIdentifier(): String {
        val timeZone = TimeZone.getDefault()
        val timeZoneId = timeZone.id

        return if (timeZoneId == "Asia/Calcutta") {
            "Asia/Kolkata"
        } else {
            timeZoneId
        }
    }
}