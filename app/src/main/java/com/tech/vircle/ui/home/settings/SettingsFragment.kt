package com.tech.vircle.ui.home.settings

import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.tech.vircle.App
import com.tech.vircle.R
import com.tech.vircle.base.BaseFragment
import com.tech.vircle.base.BaseViewModel
import com.tech.vircle.data.api.Constants
import com.tech.vircle.data.model.ForgotPasswordResponse
import com.tech.vircle.data.model.GetUserprofileResponse
import com.tech.vircle.data.model.ProfileModelClass
import com.tech.vircle.databinding.DialogDeleteLogoutBinding
import com.tech.vircle.databinding.FragmentSettingsBinding
import com.tech.vircle.ui.home.settings.profile.SettingsAdapter
import com.tech.vircle.utils.BaseCustomDialog
import com.tech.vircle.utils.BindingUtils
import com.tech.vircle.utils.Status
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : BaseFragment<FragmentSettingsBinding>() {
    private val viewModel: SettingsFragmentVM by viewModels()
    private lateinit var profileAdapter: SettingsAdapter
    private var logoutDialog: BaseCustomDialog<DialogDeleteLogoutBinding>? = null
    override fun getLayoutResource(): Int {
        return R.layout.fragment_settings
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // click
        initOnClick()
        // adapter
        initProfileAdapter()
        // observer
        initObserver()
        // api call
        val profile = sharedPrefManager.getProfileData()
        if (profile?.name.isNullOrEmpty()) {
            viewModel.getProfileApi(Constants.USER_GET_PROFILE)
        } else {
            binding.bean = profile
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
                        "logoutApi" -> {
                            try {
                                val myDataModel: ForgotPasswordResponse? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (myDataModel != null) {
                                    showSuccessToast(myDataModel.message.toString())
                                    sharedPrefManager.clear()
                                    findNavController().popBackStack(R.id.auth_navigation, true)
                                    BindingUtils.navigateWithSlide(
                                        findNavController(), R.id.navigateToLoginFragment, null
                                    )
                                }

                            } catch (e: Exception) {
                                Log.e("error", "logoutApi: $e")
                            } finally {
                                hideLoading()
                            }
                        }

                        "getProfileApi" -> {
                            try {
                                val myDataModel: GetUserprofileResponse? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (myDataModel != null) {
                                    if (myDataModel.data != null) {
                                        sharedPrefManager.setProfileData(myDataModel.data.user)
                                        binding.bean = myDataModel.data.user
                                    }
                                }

                            } catch (e: Exception) {
                                Log.e("error", "logoutApi: $e")
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
                // upgrade button click
                R.id.btnUpgrade -> {
                    BindingUtils.navigateWithSlide(
                        findNavController(), R.id.navigateToPackageDetailsFragment, null
                    )
                }
                // profile icon click
                R.id.ivPerson -> {
                    BindingUtils.navigateWithSlide(
                        findNavController(), R.id.navigateToProfileFragment, null
                    )
                }

                R.id.btnLogout -> {
                    initLogoutDialog()
                }
            }
        }
    }


    /** dialog **/
    private fun initLogoutDialog() {
        logoutDialog = BaseCustomDialog(requireActivity(), R.layout.dialog_delete_logout) {
            when (it?.id) {
                R.id.tvCancel -> {
                    logoutDialog?.dismiss()
                }

                R.id.tvLogout -> {
                    viewModel.logoutApi(Constants.USER_LOGOUT)
                    logoutDialog?.dismiss()
                }
            }

        }
        logoutDialog?.setCancelable(false)
        logoutDialog?.create()
        logoutDialog?.show()

        logoutDialog?.binding?.apply {
            tvTitle.text = getString(R.string.logout)
            tvSubHeading.text = getString(R.string.are_you_sure_to_logout)
            tvLogout.text = getString(R.string.logout)
        }

    }

    /** handle adapter **/
    private fun initProfileAdapter() {
        profileAdapter =
            SettingsAdapter(addProfileList(), object : SettingsAdapter.OnItemClickListener {
                override fun onItemClicked(position: Int, item: ProfileModelClass) {
                    when (position) {
                        2 -> {
                            BindingUtils.navigateWithSlide(
                                findNavController(), R.id.navigateToChangePasswordFragment, null
                            )
                        }

                        3 -> {

                        }

                        4 -> {

                        }

                        5 -> {

                        }

                        6 -> {
                            BindingUtils.navigateWithSlide(
                                findNavController(), R.id.navigateToPlansFragment, null
                            )
                        }
                    }
                }

                override fun onSwitchChanged(
                    position: Int, item: ProfileModelClass, isChecked: Boolean
                ) {
                    // Handle switch toggle
                    when (item.name) {
                        "Dark Mode" -> {
                            sharedPrefManager.setDarkMode(isChecked)
                            (requireActivity().application as App).setDarkMode(isChecked)
                        }

                        "Notifications" -> {
                            showInfoToast("Notifications ${if (isChecked) "enabled" else "disabled"}")
                        }
                    }

                }
            })
        binding.rvProfile.layoutManager = LinearLayoutManager(requireContext())
        binding.rvProfile.adapter = profileAdapter
    }

    // add data
    private fun addProfileList(): ArrayList<ProfileModelClass> {
        var darkMode = sharedPrefManager.isDarkModeEnabled()
        return arrayListOf(
            ProfileModelClass(
                "Dark Mode", 2, darkMode
            ),
            ProfileModelClass(
                "Notifications", 2
            ),
            ProfileModelClass(
                "Change Password", 1
            ),

            ProfileModelClass(
                "Privacy Policy", 1
            ),
            ProfileModelClass(
                "Terms of Service", 1
            ),
            ProfileModelClass(
                "AI Use Disclaimer", 1
            ),
            ProfileModelClass(
                "Contact Us / Give Feedback", 1
            ),

            )
    }
}