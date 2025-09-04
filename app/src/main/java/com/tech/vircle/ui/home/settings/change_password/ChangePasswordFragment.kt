package com.tech.vircle.ui.home.settings.change_password

import android.text.InputType
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.tech.vircle.R
import com.tech.vircle.base.BaseFragment
import com.tech.vircle.base.BaseViewModel
import com.tech.vircle.data.api.Constants
import com.tech.vircle.data.model.ForgotPasswordResponse
import com.tech.vircle.databinding.FragmentChangePasswordBinding
import com.tech.vircle.ui.home.settings.SettingsFragmentVM
import com.tech.vircle.utils.BindingUtils
import com.tech.vircle.utils.Status
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangePasswordFragment : BaseFragment<FragmentChangePasswordBinding>() {
    private val viewModel: SettingsFragmentVM by viewModels()
    override fun getLayoutResource(): Int {
        return R.layout.fragment_change_password
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
                        "changePasswordApi" -> {
                            try {
                                val myDataModel: ForgotPasswordResponse? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (myDataModel != null) {
                                    showSuccessToast(myDataModel.message.toString())
                                }

                            } catch (e: Exception) {
                                Log.e("error", "changePasswordApi: $e")
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

    /***
     * click handel event
     */
    private fun initOnClick() {
        viewModel.onClick.observe(viewLifecycleOwner) {
            when (it?.id) {
                // back button click
                R.id.ivBack -> {
                    findNavController().popBackStack()
                }
                // next button click
                R.id.btnNext -> {
                    if (validate()) {
                        val oldPassword = binding.etYourPassword.text.toString().trim()
                        val newPassword = binding.etNewPassword.text.toString().trim()
                        val data = HashMap<String, Any>()
                        data["oldPassword"] = oldPassword
                        data["newPassword"] = newPassword
                        viewModel.changePasswordApi(Constants.USER_RESET_PASSWORD, data)
                    }

                }

                // show or hide old password click
                R.id.ivHidePassword -> {
                    if (binding.etYourPassword.text.toString().trim().isNotEmpty()) {
                        showOrHideOldPassword()
                    }
                }
                // show or hide new password click
                R.id.ivNewHidePassword -> {
                    if (binding.etNewPassword.text.toString().trim().isNotEmpty()) {
                        showOrHideNewPassword()
                    }

                }

                // show or hide confirm password click
                R.id.ivConHidePassword -> {
                    if (binding.etConPassword.text.toString().trim().isNotEmpty()) {
                        showOrHideConfirmPassword()
                    }

                }
            }
        }
    }

    /*** add validation ***/
    private fun validate(): Boolean {
        val oldPassword = binding.etYourPassword.text.toString().trim()
        val newPassword = binding.etNewPassword.text.toString().trim()
        val confirmPassword = binding.etConPassword.text.toString().trim()
        if (oldPassword.isEmpty()) {
            showInfoToast("Please enter old password")
            return false
        } else if (oldPassword.length < 6) {
            showInfoToast("Password must be at least 6 characters")
            return false
        } else if (!oldPassword.any { it.isUpperCase() }) {
            showInfoToast("Password must contain at least one uppercase letter")
            return false
        } else if (newPassword.isEmpty()) {
            showInfoToast("Please enter new password")
            return false
        } else if (newPassword.length < 6) {
            showInfoToast("Password must be at least 6 characters")
        } else if (!newPassword.any { it.isUpperCase() }) {
            showInfoToast("Password must contain at least one uppercase letter")
            return false
        } else if (confirmPassword.isEmpty()) {
            showInfoToast("Please enter confirm password")
            return false
        } else if (newPassword != confirmPassword) {
            showInfoToast("Password and Confirm password do not match")
            return false
        }

        return true
    }


    /*** show or old hide password **/
    private fun showOrHideOldPassword() {
        if (binding.etYourPassword.text.toString().trim().isNotEmpty()) {
            if (binding.etYourPassword.inputType == InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD) {
                binding.ivHidePassword.setImageResource(R.drawable.bxs_show)
                binding.etYourPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                binding.ivHidePassword.setImageResource(R.drawable.hide_password)
                binding.etYourPassword.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            binding.etYourPassword.setSelection(binding.etYourPassword.length())
        }
    }

    /*** show or hide new password **/
    private fun showOrHideNewPassword() {
        if (binding.etNewPassword.text.toString().trim().isNotEmpty()) {
            if (binding.etNewPassword.inputType == InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD) {
                binding.ivNewHidePassword.setImageResource(R.drawable.bxs_show)
                binding.etNewPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                binding.ivNewHidePassword.setImageResource(R.drawable.hide_password)
                binding.etNewPassword.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            binding.etNewPassword.setSelection(binding.etNewPassword.length())
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


}