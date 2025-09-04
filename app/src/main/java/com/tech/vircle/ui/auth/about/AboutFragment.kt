package com.tech.vircle.ui.auth.about

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableString
import android.text.style.TypefaceSpan
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.tech.vircle.BR
import com.tech.vircle.R
import com.tech.vircle.base.BaseFragment
import com.tech.vircle.base.BaseViewModel
import com.tech.vircle.base.SimpleRecyclerViewAdapter
import com.tech.vircle.data.model.CommonModelClass
import com.tech.vircle.databinding.CommonBottomSheetItemBinding
import com.tech.vircle.databinding.FragmentAboutBinding
import com.tech.vircle.databinding.PrivacyPolicyBottomSheetItemBinding
import com.tech.vircle.databinding.RvCommonItemBinding
import com.tech.vircle.ui.auth.AuthCommonVM
import com.tech.vircle.utils.BaseCustomBottomSheet
import com.tech.vircle.utils.BindingUtils
import com.tech.vircle.utils.event.DummyList
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AboutFragment : BaseFragment<FragmentAboutBinding>() {
    private val viewModel: AuthCommonVM by viewModels()
    private var privacyPolicyBottomSheet: BaseCustomBottomSheet<PrivacyPolicyBottomSheetItemBinding>? =
        null
    private var commonBottomSheet: BaseCustomBottomSheet<CommonBottomSheetItemBinding>? = null
    private lateinit var commonAdapter: SimpleRecyclerViewAdapter<CommonModelClass, RvCommonItemBinding>
    private var isPrivacyPolicy = false
    override fun getLayoutResource(): Int {
        return R.layout.fragment_about
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreateView(view: View) {
        // click
        initOnClick()
        val part1 = getString(R.string.i_accept_the)
        val part2 = getString(R.string.terms_conditions_and_privacy_policy)
        val spannable = SpannableString(part1 + part2)
        spannable.setSpan(
            TypefaceSpan(ResourcesCompat.getFont(requireContext(), R.font.manrope_medium)!!),
            0,
            part1.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannable.setSpan(
            TypefaceSpan(ResourcesCompat.getFont(requireContext(), R.font.manrope_semi_bold)!!),
            part1.length,
            (part1 + part2).length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.RememberMe.text = spannable

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
                // next button click
                R.id.btnNext -> {
                    if (validate()) {
                        if (isPrivacyPolicy) {
                            var bundle = Bundle()
                            bundle.putString("gender", binding.etGender.text.toString())
                            bundle.putString("age", binding.etAge.text.toString())
                            bundle.putBoolean("isPrivacyPolicy", isPrivacyPolicy)

                            BindingUtils.navigateWithSlide(
                                findNavController(), R.id.navigateToUploadPhotoFragment, bundle
                            )
                        } else {
                            showInfoToast("Please select term & condition")
                        }
                    }

                }
                // privacy policy button button click
                R.id.checkbox -> {
                    if (isPrivacyPolicy) {
                        binding.checkbox.setImageResource(R.drawable.un_selected_radiobutton)
                        isPrivacyPolicy = false
                    } else {
                        binding.checkbox.setImageResource(R.drawable.radiobutton)
                        isPrivacyPolicy = true
                        privacyPolicyBottomSheet()
                    }
                }

                // gender button click
                R.id.ivDropDownGender, R.id.etGender -> {
                    BindingUtils.preventMultipleClick(it)
                    commonBottomSheet(1)
                }

                // age button click
                R.id.ivDropDownGenderAge, R.id.etAge -> {
                    BindingUtils.preventMultipleClick(it)
                    commonBottomSheet(2)
                }
            }
        }
    }


    /****
     * privacy policy bottom sheet item
     ****/
    private fun privacyPolicyBottomSheet() {
        privacyPolicyBottomSheet = BaseCustomBottomSheet(
            requireContext(), R.layout.privacy_policy_bottom_sheet_item
        ) {
            when (it?.id) {
                R.id.backButton -> {
                    privacyPolicyBottomSheet?.dismiss()
                }

                R.id.btnIAccept -> {
                    privacyPolicyBottomSheet?.dismiss()
                }
            }
        }
        privacyPolicyBottomSheet?.apply {
            behavior.isDraggable = true
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            create()
            window?.attributes?.windowAnimations = R.style.BottomSheetAnimation
            show()
        }
    }


    /*** add validation ***/
    private fun validate(): Boolean {
        val gender = binding.etGender.text.toString().trim()
        val age = binding.etAge.text.toString().trim()

        if (gender.isEmpty()) {
            showInfoToast("Please enter gender")
            return false
        } else if (age.isEmpty()) {
            showInfoToast("Please enter age")
            return false
        }

        return true
    }


    /****
     * common bottom sheet item
     ****/
    private fun commonBottomSheet(type: Int) {
        commonBottomSheet = BaseCustomBottomSheet(
            requireContext(), R.layout.common_bottom_sheet_item
        ) {
            when (it?.id) {
                R.id.backButton -> {
                    commonBottomSheet?.dismiss()
                }

                R.id.btnIAccept -> {
                    commonBottomSheet?.dismiss()
                }
            }

        }
        commonBottomSheet?.apply {
            behavior.isDraggable = true
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            create()
            window?.attributes?.windowAnimations = R.style.BottomSheetAnimationSecond
            show()

            if (type == 1) {
                binding.tvCommon.text = getString(R.string.gender)
            } else {
                binding.tvCommon.text = getString(R.string.age)
            }
        }
        initCommonAdapter(type)

    }


    /** handle adapter **/
    private fun initCommonAdapter(type: Int) {
        commonAdapter = SimpleRecyclerViewAdapter(R.layout.rv_common_item, BR.bean) { v, m, pos ->
            when (v?.id) {
                R.id.main -> {
                    for (i in commonAdapter.list) {
                        i.isStatus = i.category == m.category
                    }
                    commonAdapter.notifyDataSetChanged()
                    Handler(Looper.getMainLooper()).postDelayed({
                        if (type == 2) {
                            if (m.category == "Other") {
                                commonBottomSheet?.dismiss()
                                binding.etAge.setText("")
                                binding.etAge.apply {
                                    isFocusableInTouchMode = true
                                    requestFocus()
                                    // Post to ensure focus is applied after BottomSheet is dismissed
                                    postDelayed({
                                        val imm =
                                            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                                        imm.showSoftInput(this, InputMethodManager.SHOW_FORCED)
                                    }, 500)
                                }
                            } else {
                                binding.etAge.setText(m.category)
                                commonBottomSheet?.dismiss()
                            }
                        } else {
                            binding.etGender.setText(m.category)
                            commonBottomSheet?.dismiss()
                        }
                    }, 300)
                }
            }
        }

        // Provide list based on type (1 = gender, 2 = age)
        commonAdapter.list =
            if (type == 1) DummyList.addGenderSignupList() else DummyList.addAgeList()
        commonBottomSheet?.binding?.rvCommon?.adapter = commonAdapter
    }


}