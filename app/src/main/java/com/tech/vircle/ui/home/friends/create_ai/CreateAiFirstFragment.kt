package com.tech.vircle.ui.home.friends.create_ai

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.tech.vircle.BR
import com.tech.vircle.R
import com.tech.vircle.base.BaseFragment
import com.tech.vircle.base.BaseViewModel
import com.tech.vircle.base.SimpleRecyclerViewAdapter
import com.tech.vircle.data.model.CreateAiModelClass
import com.tech.vircle.databinding.FragmentCreateAiFirstBinding
import com.tech.vircle.databinding.RvCreateAccountItemBinding
import com.tech.vircle.ui.home.friends.FriendsFragmentVM
import com.tech.vircle.utils.BindingUtils
import com.tech.vircle.utils.BindingUtils.updateProgress
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CreateAiFirstFragment : BaseFragment<FragmentCreateAiFirstBinding>() {
    private val viewModel: FriendsFragmentVM by viewModels()
    private lateinit var createAiAdapter: SimpleRecyclerViewAdapter<CreateAiModelClass, RvCreateAccountItemBinding>

    private var typeList = ""

    override fun getLayoutResource(): Int {
        return R.layout.fragment_create_ai_first
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // click
        initOnCLick()
        // progress bar
        updateProgress(
            requireActivity(), binding.progressBar, binding.tvProgress, 35, "1/3"
        )
        // adapter
        initCreateAiAdapter()

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
                // next button click
                R.id.btnNext -> {
                    if (typeList.isNotEmpty()) {
                        val bundle = Bundle().apply {
                            putString("selectedType", typeList)
                        }
                        BindingUtils.navigateWithSlide(
                            findNavController(),
                            R.id.navigateToCreateAiContactSecondFragment,
                            bundle
                        )
                    } else {
                        showInfoToast("Please select at least one type")
                    }

                }


            }
        }
    }

    /** handle adapter **/
    private fun initCreateAiAdapter() {
        createAiAdapter =
            SimpleRecyclerViewAdapter(R.layout.rv_create_account_item, BR.bean) { v, m, pos ->
                when (v?.id) {
                    R.id.clCreate -> {
                        for (i in createAiAdapter.list) {
                            i.check = i.category == m.category
                        }
                        typeList = m.category
                        createAiAdapter.notifyDataSetChanged()
                    }
                }
            }
        createAiAdapter.list = addCreateAiList()
        binding.rvCreateAi.adapter = createAiAdapter
    }

    // add data
    private fun addCreateAiList(): ArrayList<CreateAiModelClass> {
        return arrayListOf(
            CreateAiModelClass(
                "Expert",
                R.drawable.expert,
                R.drawable.un_selected_radiobutton,
            ),
            CreateAiModelClass(
                "Companion",
                R.drawable.companion,
                R.drawable.un_selected_radiobutton,
            ),
            CreateAiModelClass(
                "Assistant",
                R.drawable.assistant,
                R.drawable.un_selected_radiobutton,
            ),
            CreateAiModelClass(
                "Fictional",
                R.drawable.fictional,
                R.drawable.un_selected_radiobutton,
            ),


            )
    }


}