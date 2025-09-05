package com.tech.vircle.ui.home.chat

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.tech.vircle.BR
import com.tech.vircle.R
import com.tech.vircle.base.BaseFragment
import com.tech.vircle.base.BaseViewModel
import com.tech.vircle.base.SimpleRecyclerViewAdapter
import com.tech.vircle.data.api.Constants
import com.tech.vircle.data.model.Chat
import com.tech.vircle.data.model.ChatSearchResponse
import com.tech.vircle.data.model.ContactList
import com.tech.vircle.data.model.GetChatResponse
import com.tech.vircle.data.model.SearchMessage
import com.tech.vircle.databinding.FragmentChatBinding
import com.tech.vircle.databinding.RvChatSearchItemBinding
import com.tech.vircle.databinding.RvFriendsItemBinding
import com.tech.vircle.utils.BindingUtils
import com.tech.vircle.utils.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChatFragment : BaseFragment<FragmentChatBinding>(),
    CustomSwipeChatAdapter.OnItemClickListener {
    private val viewModel: ChatFragmentVM by viewModels()
    private lateinit var customChatAdapter: CustomSwipeChatAdapter
    private lateinit var messageSearchAdapter: SimpleRecyclerViewAdapter<SearchMessage, RvChatSearchItemBinding>
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
        // adapter
        initChatSearchAdapter()
    }

    /** api response observer ***/
    private var searchJob: Job? = null
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

                        "searchChatApi" -> {
                            try {
                                val myDataModel: ChatSearchResponse? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (myDataModel != null) {
                                    if (myDataModel.data != null) {
                                        if (!myDataModel.data.messages.isNullOrEmpty()) {
                                            binding.rvChatSearch.visibility=View.VISIBLE
                                            binding.rvChat.visibility=View.GONE
                                            messageSearchAdapter.setList(myDataModel.data.messages)
                                            binding.tvEmpty.visibility = View.GONE
                                        } else if (!myDataModel.data.AiContacts.isNullOrEmpty()) {
                                            binding.rvChatSearch.visibility=View.GONE
                                            binding.rvChat.visibility=View.VISIBLE
                                            fullChatList = myDataModel.data.AiContacts as ArrayList<Chat>
                                            customChatAdapter.setList(fullChatList)
                                            binding.tvEmpty.visibility = View.GONE
                                        } else {
                                            binding.rvChatSearch.visibility=View.GONE
                                            binding.rvChat.visibility=View.VISIBLE
                                            fullChatList.clear()
                                            customChatAdapter.setList(fullChatList)
                                            binding.tvEmpty.visibility = View.VISIBLE
                                        }
                                    } else {
                                        binding.rvChatSearch.visibility=View.GONE
                                        binding.rvChat.visibility=View.VISIBLE
                                        fullChatList.clear()
                                        customChatAdapter.setList(fullChatList)
                                        binding.tvEmpty.visibility = View.VISIBLE
                                    }
                                } else {
                                    binding.rvChatSearch.visibility=View.GONE
                                    binding.rvChat.visibility=View.VISIBLE
                                    fullChatList.clear()
                                    customChatAdapter.setList(fullChatList)
                                    binding.tvEmpty.visibility = View.VISIBLE
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
                    binding.rvChatSearch.visibility=View.GONE
                    binding.rvChat.visibility=View.VISIBLE
                    binding.etSearch.setText("")
                    binding.etSearch.clearFocus()
                    hideKeyboard()
                    viewModel.getChatApi(Constants.GET_CHATS)
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



        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim().lowercase()

                searchJob?.cancel() // cancel previous typing
                searchJob = lifecycleScope.launch {
                    delay(1500) // 500ms debounce
                    if (query.isNotEmpty()) {
                        val data = HashMap<String, Any>()
                        data["search"] = query
                        viewModel.searchChatApi(Constants.CHAT_SEARCH, data)
                    }
                }
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

    /** recycler view item click handel **/
    private fun initChatSearchAdapter() {
        messageSearchAdapter= SimpleRecyclerViewAdapter(R.layout.rv_chat_search_item, BR.bean) { v, m, _ ->
            when (v?.id) {

            }
        }
        binding.rvChatSearch.adapter = messageSearchAdapter
    }

}