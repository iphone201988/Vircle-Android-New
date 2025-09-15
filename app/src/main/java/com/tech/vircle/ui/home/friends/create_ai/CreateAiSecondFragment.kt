package com.tech.vircle.ui.home.friends.create_ai

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.tech.vircle.BR
import com.tech.vircle.R
import com.tech.vircle.base.BaseFragment
import com.tech.vircle.base.BaseViewModel
import com.tech.vircle.base.SimpleRecyclerViewAdapter
import com.tech.vircle.data.api.Constants
import com.tech.vircle.data.model.AiContactList
import com.tech.vircle.data.model.ContactCreateListResponse
import com.tech.vircle.databinding.FragmentCreateAiSecondBinding
import com.tech.vircle.databinding.RvCreateSrecondItemBinding
import com.tech.vircle.ui.home.friends.FriendsFragmentVM
import com.tech.vircle.utils.BindingUtils
import com.tech.vircle.utils.BindingUtils.updateProgress
import com.tech.vircle.utils.Status
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CreateAiSecondFragment : BaseFragment<FragmentCreateAiSecondBinding>() {
    private val viewModel: FriendsFragmentVM by viewModels()
    private lateinit var createAiSecondAdapter: SimpleRecyclerViewAdapter<AiContactList, RvCreateSrecondItemBinding>
    var aiContactList: AiContactList? = null
    var selectedType: String? = null
    override fun getLayoutResource(): Int {
        return R.layout.fragment_create_ai_second
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // click
        initOnCLick()
        // progress bar
        updateProgress(requireActivity(), binding.progressBar, binding.tvProgress, 35, "1/3")
        // adapter
        initCreateAiAdapter()
        selectedType = arguments?.getString("selectedType")
        val type = when (selectedType) {
            "Expert" -> "new_expert"
            "Companion" -> "new_companion"
            "Assistant" -> "new_assistant"
            "Fictional" -> "new_characters"
            else -> {}
        }
        // api call
        val data = HashMap<String, Any>()
        data["type"] = type
        viewModel.getContactTypesApi(Constants.AI_CONTACT_ADMIN, data)
        // observer
        initObserver()
    }

    /**
     * api response observer
     **/
    private fun initObserver() {
        viewModel.observeCommon.observe(viewLifecycleOwner) {
            when (it?.status) {
                Status.LOADING -> {
                    showLoading()
                }

                Status.SUCCESS -> {
                    when (it.message) {
                        "getContactTypesApi" -> {
                            try {
                                val myDataModel: ContactCreateListResponse? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (myDataModel != null) {
                                    if (myDataModel.data != null) {
                                        if (myDataModel.data.contact?.size!! > 0) {
                                            createAiSecondAdapter.list =
                                                myDataModel.data.contact as List<AiContactList?>?
                                        }
                                    }


                                }

                            } catch (e: Exception) {
                                Log.e("error", "getContactTypesApi: $e")
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
     * all click event handel
     */
    private fun initOnCLick() {
        viewModel.onClick.observe(viewLifecycleOwner) {
            when (it?.id) {
                // back button click
                R.id.ivBack -> {
                    findNavController().popBackStack()
                }
                // Next button click
                R.id.btnNext -> {

                    if (aiContactList?.type?.isNotEmpty() == true) {
                        val bundle = Bundle().apply {
                            putParcelable("SecondTypeData", aiContactList)
                            putString("selectedType", selectedType)
                        }
                        BindingUtils.navigateWithSlide(
                            findNavController(), R.id.navigateToCreateAiContactThreeFragment, bundle
                        )
                    } else {
                        showInfoToast("Please select at least one type")
                    }
                }

                R.id.btnWillCreate -> {
                    BindingUtils.navigateWithSlide(
                        findNavController(),
                        R.id.navigateToCreateAiContactThreeFragment,
                        null
                    )
                }
            }
        }
    }

    /** handle adapter **/
    private fun initCreateAiAdapter() {
        createAiSecondAdapter =
            SimpleRecyclerViewAdapter(R.layout.rv_create_srecond_item, BR.bean) { v, m, pos ->
                when (v?.id) {
                    R.id.main -> {
                        aiContactList = m
                        for (i in createAiSecondAdapter.list) {
                            i.check = i._id == m._id
                        }
                        createAiSecondAdapter.notifyDataSetChanged()
                    }
                }
            }
        binding.rvCreateAiSecond.adapter = createAiSecondAdapter
    }
}