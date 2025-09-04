package com.tech.vircle.ui.home.chat

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.tech.vircle.R
import com.tech.vircle.base.BaseFragment
import com.tech.vircle.base.BaseViewModel
import com.tech.vircle.data.api.Constants
import com.tech.vircle.data.model.Chat
import com.tech.vircle.data.model.GetChatResponse
import com.tech.vircle.databinding.FragmentChatBinding
import com.tech.vircle.utils.BindingUtils
import com.tech.vircle.utils.Status
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatFragment : BaseFragment<FragmentChatBinding>(),
    CustomSwipeChatAdapter.OnItemClickListener {
    private val viewModel: ChatFragmentVM by viewModels()
    private lateinit var customChatAdapter: CustomSwipeChatAdapter
    private lateinit var fullChatList: ArrayList<Chat>
    override fun getLayoutResource(): Int {
        return R.layout.fragment_chat
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // click
        initOnCLick()
        // Adapter initialize
        customChatAdapter = CustomSwipeChatAdapter(this)
        binding.rvChat.adapter = customChatAdapter
        // observer
        initObserver()
        // api call
          viewModel.getChatApi(Constants.GET_CHATS)
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
                        "getChatApi" -> {
                            try {
                                val myDataModel: GetChatResponse? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (myDataModel != null) {
                                    if (myDataModel.data != null) {
                                        if (myDataModel.data.chats?.size!! > 0) {
                                            fullChatList = myDataModel.data.chats as ArrayList<Chat>
                                            customChatAdapter.setList(fullChatList)
                                            binding.tvEmpty.visibility = View.GONE
                                        } else {
                                            binding.tvEmpty.visibility = View.VISIBLE
                                        }
                                    } else {
                                        binding.tvEmpty.visibility = View.VISIBLE
                                    }


                                }

                            } catch (e: Exception) {
                                Log.e("error", "getChatApi: $e")
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
                R.id.ivCancel -> {
                    binding.etSearch.clearFocus()
                    hideKeyboard()
                }
            }
        }

        // check focusable
        binding.etSearch.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.ivCancel.visibility = View.VISIBLE
                binding.ivSearch.setImageResource(R.drawable.blue_search_rounded)
            } else {
                binding.ivCancel.visibility = View.GONE
                binding.ivSearch.setImageResource(R.drawable.search_icon)
            }
        }

        // Search filter
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim().lowercase()
                val filteredList = if (query.isEmpty()) {
                    fullChatList
                } else {
                    fullChatList.filter {
                        it.contactId?.name?.lowercase()
                            ?.contains(query) == true || it.lastMessage?.message?.lowercase()
                            ?.contains(query) == true
                    } as ArrayList
                }
                customChatAdapter.setList(filteredList)
                customChatAdapter.notifyDataSetChanged()
            }

            override fun afterTextChanged(s: Editable?) {}
        })


    }

    /***
     * hide keyboard
     */
    private fun hideKeyboard() {
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.etSearch.windowToken, 0)
    }


    override fun onItemClick(
        view: View, item: Chat, position: Int
    ) {
        when (view.id) {
            R.id.constantSwap -> {
                customChatAdapter.removeItemAt(position)
            }

            R.id.favConstantLayout -> {
                val bundle = Bundle()
                bundle.putParcelable("chat", item)
                BindingUtils.navigateWithSlide(
                    findNavController(), R.id.navigateToChatDetailsFragment, bundle
                )
            }
        }
    }
}