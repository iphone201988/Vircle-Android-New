package com.tech.vircle.ui.auth.forgot

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
import com.tech.vircle.databinding.FragmentSetUpPasswordBinding
import com.tech.vircle.ui.auth.AuthCommonVM
import com.tech.vircle.utils.BindingUtils
import com.tech.vircle.utils.Status
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SetUpPasswordFragment : BaseFragment<FragmentSetUpPasswordBinding>() {
    private val viewModel: AuthCommonVM by viewModels()
    override fun getLayoutResource(): Int {
        return R.layout.fragment_set_up_password
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
                // next start  button click
                R.id.btnChangePassword -> {
                    if (validate()) {
                        val data = HashMap<String, Any>()
                        data["userId"] = arguments?.getString("userId").toString()
                        data["newPassword"] = binding.etPassword.text.toString().trim()
                        viewModel.changePasswordApi(Constants.USER_CHANGE_PASSWORD, data)
                    }
                }
                // show or hide password click
                R.id.ivHidePassword -> {
                    if (binding.etPassword.text.toString().trim().isNotEmpty()) {
                        showOrHidePassword()
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
                                    BindingUtils.navigateWithSlide(
                                        findNavController(), R.id.navigateToAllSetFragment, null
                                    )
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
        val password = binding.etPassword.text.toString().trim()
        val confirmPassword = binding.etConPassword.text.toString().trim()
        if (password.isEmpty()) {
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


}


