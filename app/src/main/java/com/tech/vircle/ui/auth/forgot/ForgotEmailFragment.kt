package com.tech.vircle.ui.auth.forgot

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.tech.vircle.R
import com.tech.vircle.base.BaseFragment
import com.tech.vircle.base.BaseViewModel
import com.tech.vircle.data.api.Constants
import com.tech.vircle.data.model.ForgotPasswordResponse
import com.tech.vircle.databinding.FragmentForgotEmailBinding
import com.tech.vircle.ui.auth.AuthCommonVM
import com.tech.vircle.utils.BindingUtils
import com.tech.vircle.utils.Status
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ForgotEmailFragment : BaseFragment<FragmentForgotEmailBinding>() {
    private val viewModel: AuthCommonVM by viewModels()
    override fun getLayoutResource(): Int {
        return R.layout.fragment_forgot_email
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
                R.id.btnNext -> {
                    if (validate()) {
                        val data = HashMap<String, Any>()
                        data["email"] = binding.etEmail.text.toString().trim()
                        viewModel.forgotEmailApi(Constants.USER_FORGOT_PASSWORD, data)
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
                        "forgotEmailApi" -> {
                            try {
                                val myDataModel: ForgotPasswordResponse? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (myDataModel != null) {
                                    showSuccessToast(myDataModel.message.toString())
                                    var bundle = Bundle()
                                    bundle.putString("userId", myDataModel.userId)
                                    bundle.putString(
                                        "userEmail", binding.etEmail.text.toString().trim()
                                    )
                                    BindingUtils.navigateWithSlide(
                                        findNavController(),
                                        R.id.navigateToCodeVerificationFragment,
                                        bundle
                                    )

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
        val email = binding.etEmail.text.toString().trim()

        if (email.isEmpty()) {
            showInfoToast("Please enter email")
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showInfoToast("Please enter a valid email")
            return false
        }
        return true
    }
}