package com.tech.vircle.ui.auth.login

import android.app.Activity
import android.os.Build
import android.text.InputType
import android.util.Log
import android.util.Patterns
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import com.tech.vircle.R
import com.tech.vircle.base.BaseFragment
import com.tech.vircle.base.BaseViewModel
import com.tech.vircle.data.api.Constants
import com.tech.vircle.data.model.UserRegistrationResponse
import com.tech.vircle.databinding.FragmentLoginBinding
import com.tech.vircle.ui.auth.AuthCommonVM
import com.tech.vircle.utils.BindingUtils
import com.tech.vircle.utils.Status
import dagger.hilt.android.AndroidEntryPoint
import java.util.TimeZone

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>() {
    private var token = "123456"
    private val viewModel: AuthCommonVM by viewModels()
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    override fun getLayoutResource(): Int {
        return R.layout.fragment_login
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreateView(view: View) {
        // click
        initOnClick()
        // observer
        initObserver()
        // get token firebase
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (!it.isSuccessful) {
                return@addOnCompleteListener
            }
            token = it.result

        }
        // firebase initialize
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()
        mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
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
                        "loginAccount" -> {
                            try {
                                val myDataModel: UserRegistrationResponse? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (myDataModel != null) {
                                    if (myDataModel.data != null) {
                                        sharedPrefManager.setLoginData(myDataModel.data)
                                        sharedPrefManager.setToken(myDataModel.data.token)
                                    }
                                    if (myDataModel.data?.user?.isOnboard == true) {
                                        findNavController().popBackStack(R.id.auth_navigation, true)
                                        BindingUtils.navigateWithSlide(
                                            findNavController(), R.id.navigateToAboutFragment, null
                                        )
                                    } else {
                                        findNavController().popBackStack(R.id.auth_navigation, true)
                                        BindingUtils.navigateWithSlide(
                                            findNavController(), R.id.fragmentFriends, null
                                        )
                                    }

                                }

                            } catch (e: Exception) {
                                Log.e("error", "loginAccount: $e")
                            } finally {
                                hideLoading()
                            }
                        }

                        "socialLogin" -> {
                            try {
                                val myDataModel: UserRegistrationResponse? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (myDataModel != null) {
                                    if (myDataModel.data != null) {
                                        sharedPrefManager.setToken(myDataModel.data.token)
                                        sharedPrefManager.setLoginData(myDataModel.data)
                                    }
                                    if (myDataModel.data?.user?.isOnboard == true) {
                                        findNavController().popBackStack(R.id.auth_navigation, true)
                                        BindingUtils.navigateWithSlide(
                                            findNavController(), R.id.navigateToAboutFragment, null
                                        )
                                    } else {
                                        findNavController().popBackStack(R.id.auth_navigation, true)
                                        BindingUtils.navigateWithSlide(
                                            findNavController(), R.id.fragmentFriends, null
                                        )
                                    }
                                }

                            } catch (e: Exception) {
                                Log.e("error", "socialLogin: $e")
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
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    private fun initOnClick() {
        viewModel.onClick.observe(viewLifecycleOwner) {
            when (it?.id) {
                // sigun screen click
                R.id.clRegEmail -> {
                    BindingUtils.navigateWithSlide(
                        findNavController(), R.id.navigateToSignupFragment, null
                    )
                }
                // forgot password click
                R.id.tvForgotPassword -> {
                    BindingUtils.navigateWithSlide(
                        findNavController(), R.id.navigateToForgotEmailFragment, null
                    )
                }
                // login button click
                R.id.btnNext -> {
                    val email = binding.etEmail.text.toString().trim()
                    val password = binding.etPassword.text.toString().trim()
                    if (validate()) {
                        val data = HashMap<String, Any>()
                        data["email"] = email
                        data["password"] = password
                        data["deviceToken"] = token
                        data["deviceType"] = 2
                        data["timezone"] = getCurrentTimeZoneIdentifier()
                        viewModel.loginAccount(Constants.USER_LOGIN, data)
                    }
                }
                // show or hide password click
                R.id.ivHidePassword -> {
                    if (binding.etPassword.text.toString().trim().isNotEmpty()) {
                        showOrHidePassword()
                    }
                }
                // google button click
                R.id.clGoogle -> {
                    BindingUtils.preventMultipleClick(it)
                    mGoogleSignInClient.signOut()
                    signIn()

                }
            }
        }
    }


    /** google sign in **/
    private fun signIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        resultLauncher.launch(signInIntent)
    }


    /** google launcher **/
    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task: Task<GoogleSignInAccount> =
                    GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.getResult(ApiException::class.java)!!
                    val data = HashMap<String, Any>()
                    data["socialId"] = account.id.toString()
                    data["name"] = account.displayName.toString()
                    data["email"] = account.email.toString()
                    data["avatar"] = account.photoUrl.toString()
                    data["socialType"] = 2
                    data["deviceType"] = 2
                    data["deviceToken"] = token
                    viewModel.socialLogin(Constants.USER_SOCIAL_LOGIN, data)
                } catch (e: ApiException) {
                    showErrorToast("Google sign-in failed")
                }
            }
        }


    /*** show or  hide password **/
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


    /*** add validation ***/
    private fun validate(): Boolean {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (email.isEmpty()) {
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