package com.tech.vircle.ui.home.settings.plans

import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.tech.vircle.R
import com.tech.vircle.base.BaseFragment
import com.tech.vircle.base.BaseViewModel
import com.tech.vircle.databinding.FragmentPlanBinding
import com.tech.vircle.ui.home.settings.SettingsFragmentVM
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlanFragment : BaseFragment<FragmentPlanBinding>() {
    private val viewModel: SettingsFragmentVM by viewModels()
    override fun getLayoutResource(): Int {
        return R.layout.fragment_plan
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // click
        initOnClick()
    }

    /***
     * click handel event
     */
    private fun initOnClick() {
        viewModel.onClick.observe(viewLifecycleOwner) {
            when (it?.id) {
                R.id.ivBack -> {
                    findNavController().popBackStack()
                }
            }
        }
    }


}