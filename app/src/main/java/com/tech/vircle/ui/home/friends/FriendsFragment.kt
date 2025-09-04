package com.tech.vircle.ui.home.friends

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
import com.tech.vircle.data.model.ContactList
import com.tech.vircle.data.model.ContactListResponse
import com.tech.vircle.databinding.FragmentFriendsBinding
import com.tech.vircle.databinding.RvFriendsItemBinding
import com.tech.vircle.utils.BindingUtils
import com.tech.vircle.utils.Status
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FriendsFragment : BaseFragment<FragmentFriendsBinding>() {
    private val viewModel: FriendsFragmentVM by viewModels()
    private lateinit var friendsAdapter: SimpleRecyclerViewAdapter<ContactList, RvFriendsItemBinding>
    private lateinit var fullFriendsList: ArrayList<ContactList>

    override fun getLayoutResource(): Int {
        return R.layout.fragment_friends
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // click
        initOnCLick()
        // Adapter initialize
        initFriendsAdapter()
        // observer
        initObserver()
        // api call
         viewModel.getContactApi(Constants.AI_CONTACT)
        // get id
        if (sharedPrefManager.isDarkModeEnabled()) {
            binding.ivEmpty.setImageResource(R.drawable.create_ai_dark_mode_icon)
        } else {
            binding.ivEmpty.setImageResource(R.drawable.ai_contact_empty)
        }

    }

    /***
     * all click event handel
     */
    private fun initOnCLick() {
        viewModel.onClick.observe(viewLifecycleOwner) {
            when (it?.id) {
                //  add button click
                R.id.ivAddContact, R.id.btnNext -> {
                    BindingUtils.navigateWithSlide(
                        findNavController(), R.id.navigateToCreateAiContactFirstFragment, null
                    )
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
                        "getContactApi" -> {
                            try {
                                val myDataModel: ContactListResponse? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (myDataModel != null) {
                                    if (myDataModel.data != null) {
                                        if (myDataModel.data.contacts?.size!! > 0) {
                                            fullFriendsList =
                                                myDataModel.data.contacts as ArrayList<ContactList>
                                            friendsAdapter.list = fullFriendsList
                                            binding.ivAddContact.visibility = View.VISIBLE
                                            binding.ivAddContact.visibility = View.VISIBLE
                                            binding.clEmpty.visibility = View.GONE
                                            binding.rvContact.visibility = View.VISIBLE
                                        } else {
                                            binding.ivAddContact.visibility = View.GONE
                                            binding.clEmpty.visibility = View.VISIBLE
                                            binding.rvContact.visibility = View.GONE
                                        }
                                    } else {
                                        binding.ivAddContact.visibility = View.GONE
                                        binding.clEmpty.visibility = View.VISIBLE
                                        binding.rvContact.visibility = View.GONE
                                    }


                                }

                            } catch (e: Exception) {
                                Log.e("error", "callSignUpApi: $e")
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

    /** recycler view item click handel **/
    private fun initFriendsAdapter() {
        friendsAdapter = SimpleRecyclerViewAdapter(R.layout.rv_friends_item, BR.bean) { v, m, _ ->
            when (v?.id) {
                R.id.ivPerson ,R.id.favConstantLayout1-> {
                    val bundle = Bundle()
                    bundle.putParcelable("data", m)
                    BindingUtils.navigateWithSlide(findNavController(), R.id.navigateToAiContactPageFragment, bundle)
                }
            }
        }
        binding.rvContact.adapter = friendsAdapter
    }


}

