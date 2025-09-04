package com.tech.vircle.ui.auth.forgot

import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.tech.vircle.R
import com.tech.vircle.base.BaseFragment
import com.tech.vircle.base.BaseViewModel
import com.tech.vircle.databinding.FragmentAllSetBinding
import com.tech.vircle.ui.auth.AuthCommonVM
import com.tech.vircle.utils.BindingUtils
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AllSetFragment : BaseFragment<FragmentAllSetBinding>() {
    private val viewModel: AuthCommonVM by viewModels()
    override fun getLayoutResource(): Int {
        return R.layout.fragment_all_set
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // click
        initOnClick()
    }


    /**
     * Initialize onClick
     */
    private fun initOnClick() {
        viewModel.onClick.observe(viewLifecycleOwner) {
            when (it?.id) {
                // next start  button click
                R.id.btnLogin -> {
                    findNavController().popBackStack(R.id.auth_navigation, true)
                    BindingUtils.navigateWithSlide(
                        findNavController(),
                        R.id.navigateToLoginFragment,
                        null
                    )

                }


            }
        }
    }
}