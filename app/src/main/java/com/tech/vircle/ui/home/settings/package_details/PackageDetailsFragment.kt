package com.tech.vircle.ui.home.settings.package_details

import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.tech.vircle.BR
import com.tech.vircle.R
import com.tech.vircle.base.BaseFragment
import com.tech.vircle.base.BaseViewModel
import com.tech.vircle.base.SimpleRecyclerViewAdapter
import com.tech.vircle.data.model.PackageDetailsClass
import com.tech.vircle.databinding.FragmentPackageDetailsBinding
import com.tech.vircle.databinding.RvPackageDetailsItemBinding
import com.tech.vircle.ui.home.settings.SettingsFragmentVM
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PackageDetailsFragment : BaseFragment<FragmentPackageDetailsBinding>() {
    private val viewModel: SettingsFragmentVM by viewModels()
    private lateinit var packageDetailsAdapter: SimpleRecyclerViewAdapter<PackageDetailsClass, RvPackageDetailsItemBinding>
    override fun getLayoutResource(): Int {
        return R.layout.fragment_package_details
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // adapter
        initPackageDetailsAdapter()
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


    /** handle adapter **/
    private fun initPackageDetailsAdapter() {
        packageDetailsAdapter =
            SimpleRecyclerViewAdapter(R.layout.rv_package_details_item, BR.bean) { v, m, pos ->

            }
        packageDetailsAdapter.list = addProfileList()
        binding.rvPackageDetails.adapter = packageDetailsAdapter
    }

    // add data
    private fun addProfileList(): ArrayList<PackageDetailsClass> {
        return arrayListOf(
            PackageDetailsClass("Current Plan", "Pro Plan - Monthly", R.drawable.details_first),
            PackageDetailsClass(
                "Renewal Date", "Renews on July 15, 2024", R.drawable.details_second
            ),
            PackageDetailsClass("Billing Amount", "Monthly - $7.99", R.drawable.details_third),
        )
    }
}