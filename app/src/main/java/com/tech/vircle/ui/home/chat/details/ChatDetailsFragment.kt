package com.tech.vircle.ui.home.chat.details

import android.content.Context
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tech.vircle.R
import com.tech.vircle.base.BaseFragment
import com.tech.vircle.base.BaseViewModel
import com.tech.vircle.data.api.Constants
import com.tech.vircle.data.model.AiContact
import com.tech.vircle.data.model.Chat
import com.tech.vircle.data.model.CreateAiData
import com.tech.vircle.data.model.GetUserMessageData
import com.tech.vircle.data.model.Message
import com.tech.vircle.databinding.FragmentChatDetailsBinding
import com.tech.vircle.ui.home.chat.ChatFragmentVM
import com.tech.vircle.ui.home.chat.adpter.ChatAdapter
import com.tech.vircle.utils.BindingUtils
import com.tech.vircle.utils.Status
import dagger.hilt.android.AndroidEntryPoint
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.UUID

@AndroidEntryPoint
class ChatDetailsFragment : BaseFragment<FragmentChatDetailsBinding>() {
    private val viewModel: ChatFragmentVM by viewModels()

    //    private lateinit var chatAdapter: ChatDetailsAdapter
    // private lateinit var chatAdapter: MessageAdapter
    private lateinit var chatAdapter: ChatAdapter

    // private lateinit var chatAdapter: SimpleRecyclerViewAdapter<Message, RvChatItemSimpleBinding>
    private var displayChatList = ArrayList<Message>()
    private lateinit var mSocket: Socket
    private var chatMessageId = ""
    private var currentPage = 1
    private var scroll: Int = 1
    private var isLoading = false

    override fun getLayoutResource(): Int {
        return R.layout.fragment_chat_details
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // click
        initOnCLick()
        // intent data
        val chat: Chat? = arguments?.getParcelable("chat")
        chat?.let {
            Glide.with(requireContext()).load(Constants.MEDIA_BASE_URL + it.contactId?.aiAvatar)
                .placeholder(R.drawable.profilephoto).error(R.drawable.profilephoto)
                .into(binding.ivPerson)
            binding.tvName.text= it.contactId?.name
            chatMessageId = it._id.toString()
            // api call
            val data = HashMap<String, Any>()
            data["page"] = currentPage
            viewModel.getChatDetailsApi(Constants.GET_CHATS_MESSAGES + "/${it._id}", data)
        }
        val userDetails = arguments?.getParcelable<CreateAiData>("userAiContact")
        val chatId = arguments?.getString("chatId")
        userDetails?.let {
            Glide.with(requireContext()).load(Constants.MEDIA_BASE_URL + userDetails.contact?.aiAvatar)
                .placeholder(R.drawable.profilephoto).error(R.drawable.profilephoto)
                .into(binding.ivPerson)
            binding.tvName.text= userDetails.contact?.name
        }
        chatId?.let {
            chatMessageId = it.toString()
            val data = HashMap<String, Any>()
            data["page"] = currentPage
            viewModel.getChatDetailsApi(Constants.GET_CHATS_MESSAGES + "/${it}", data)
        }


        // adapter
//        chatAdapter = MessageAdapter()
//        binding.rvChatDetails.adapter = chatAdapter
        chatAdapter = ChatAdapter()
        binding.rvChatDetails.adapter = chatAdapter
        initChatAdapter()
        // observer
        initObserver()
        // connect socket
        connectSocket()
        /** Refresh **/
        binding.ssPullRefresh.setColorSchemeResources(
            ContextCompat.getColor(requireContext(), R.color.primaryColor)
        )
        binding.ssPullRefresh.setOnRefreshListener {
            Handler().postDelayed({
                binding.ssPullRefresh.isRefreshing = false
                isLoading = true
                if (scroll == 1) {
                    currentPage++
                    // api call
                    val data = HashMap<String, Any>()
                    data["page"] = currentPage
                    viewModel.getChatDetailsApi(
                        Constants.GET_CHATS_MESSAGES + "/${chatId}", data
                    )
                    scroll = 0
                }
            }, 2000)
        }

    }

    /** api response observer ***/
    private fun initObserver() {
        viewModel.observeCommon.observe(viewLifecycleOwner) {
            when (it?.status) {
                Status.LOADING -> {
                    if (isLoading==false){
                        showLoading()
                    }

                }

                Status.SUCCESS -> {
                    when (it.message) {
                        "getChatDetailsApi" -> {
                            try {
                                val myDataModel: GetUserMessageData? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (myDataModel != null) {
                                    if (myDataModel.data != null) {
                                        val messages = myDataModel.data.messages ?: emptyList()
                                        if (currentPage == 1) {
                                            chatAdapter.setList(messages.reversed())
                                            binding.rvChatDetails.scrollToPosition(chatAdapter.itemCount - 1)
                                        } else {
                                            chatAdapter.addToListMessage(messages)
                                        }
                                        scroll =
                                            if (currentPage == myDataModel.data.pagination?.totalPages) {
                                                0
                                            } else {
                                                1
                                            }
                                        if (chatAdapter.getList().isNotEmpty()) {
                                            binding.tvEmpty.visibility = View.GONE
                                        } else {
                                            binding.tvEmpty.visibility = View.VISIBLE
                                        }
                                    } else {
                                        binding.tvEmpty.visibility = View.VISIBLE
                                    }
                                }

                            } catch (e: Exception) {
                                Log.e("error", "getChatDetailsApi: $e")
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
                    binding.clSearch.visibility = View.GONE
                    binding.clUser.visibility = View.VISIBLE
                    binding.etSearch.clearFocus()
                    hideKeyboard()

                }
                // back button click
                R.id.ivBack -> {
                    findNavController().popBackStack()
                }
                // search button click
                R.id.ivSearch -> {
                    binding.ivCancel.visibility = View.VISIBLE
                    binding.clSearch.visibility = View.VISIBLE
                    binding.clUser.visibility = View.GONE

                    // Focus the search EditText
                    binding.etSearch.requestFocus()

                    // Show the keyboard
                    val imm =
                        requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showSoftInput(binding.etSearch, InputMethodManager.SHOW_IMPLICIT)
                }

                // Send button click
                R.id.ivSend -> {
                    val messageText = binding.etMessage.text.toString().trim()
                    if (messageText.isNotEmpty() && chatMessageId.isNotEmpty()) {
                        sendMessage(messageText, chatMessageId)

                    } else {
                        showInfoToast("Please enter message")
                    }

                    /*  if (messageText.isNotEmpty()) {
                            val currentTime =
                                SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
                            val todayDate =
                                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

                            val newMessage = MessageModelClass(
                                message = messageText, time = currentTime, messageType = false
                            )
                            val todayChatGroup = displayChatList.find { it.date == todayDate }
                            if (todayChatGroup != null) {
                                todayChatGroup.chatData.add(newMessage)
                                chatAdapter.notifyItemChanged(displayChatList.indexOf(todayChatGroup))

                            } else {
                                val newDateGroup =
                                    ChatDetailsClass(todayDate, arrayListOf(newMessage))
                                displayChatList.add(newDateGroup)
                                chatAdapter.notifyItemInserted(displayChatList.size - 1)
                            }
                            binding.etMessage.text?.clear()
                            binding.rvChatDetails.post {
                                binding.rvChatDetails.scrollToPosition(displayChatList.size - 1)
                            }

                            CoroutineScope(Dispatchers.Main).launch {
                                binding.imgTyping.visibility= View.VISIBLE
                                delay(5000)
                                binding.imgTyping.visibility= View.GONE
                            }

                        } else {
                            showInfoToast("Please enter message")
                        }*/
                }

            }
        }

        // check focusable
        binding.etSearch.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.clSearch.visibility = View.VISIBLE
                binding.clUser.visibility = View.GONE
            } else {
                binding.ivCancel.visibility = View.GONE
                binding.clSearch.visibility = View.GONE
                binding.clUser.visibility = View.VISIBLE
            }
        }

        // Search filter
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim().lowercase()
                val filteredList = if (query.isEmpty()) {
                    displayChatList
                } else {
                    displayChatList.filter {
                        it.message?.lowercase()?.contains(query) == true
                    } as ArrayList
                }
                chatAdapter.setList(filteredList)
                chatAdapter.notifyDataSetChanged()
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

    /**
     * change status bar color
     */
    override fun onResume() {
        super.onResume()
        val window = requireActivity().window
        window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.color_ECECEC)
    }


    /** socket handel **/
    fun connectSocket() {
        try {
            val options = IO.Options().apply {
                extraHeaders = mapOf("token" to listOf(sharedPrefManager.getToken()))
                reconnection = true
                reconnectionAttempts = 5
                reconnectionDelay = 2000
            }

            mSocket = IO.socket(Constants.SOCKET_URL, options)
            mSocket.connect()

            mSocket.on(Socket.EVENT_CONNECT) {
                Log.d("SOCKET", "Connected to server")
            }

            mSocket.on(Socket.EVENT_DISCONNECT) {
                Log.d("SOCKET", "Disconnected from server")
            }

            mSocket.on(Socket.EVENT_CONNECT_ERROR) {
                Log.e("SOCKET", "Connection error: ${it.firstOrNull()}")
            }

            // Listen for messages from the server
            mSocket.on("receiveMessage") { args ->
                val data = args[0] as JSONObject
               var chatMessage =  Message(
                    _id = data.optString("_id"),
                    aiContactId = data.optString("aiContactId"),
                    chatId = data.optString("chatId"),
                    createdAt = data.optString("createdAt"),
                    isRead = data.optBoolean("isRead"),
                    message = data.optString("message"),
                    type = data.optString("type"),
                    updatedAt = data.optString("updatedAt"),
                    userId = data.optString("userId")

                )

                // Run on UI thread
                requireActivity().runOnUiThread {
                     chatAdapter.addToListSendMessage(listOf(chatMessage))

                    if (chatMessage.chatId?.isNotEmpty() == true) {
                        checkIsRead(chatMessage.chatId!!)
                    }
                    binding.rvChatDetails.scrollToPosition(chatAdapter.itemCount - 1)
                    binding.imgTyping.visibility = View.GONE
                }
            }


        } catch (e: Exception) {
            Log.e("dgggdfd", "Connection error: ${e.localizedMessage}")
        }
    }


    private fun checkIsRead(chatId: String) {
        val msgJson = JSONObject().apply {
            put("chatId", chatId)

        }
        mSocket.emit("readMessage", msgJson)
    }


    fun sendMessage(message: String, chatId: String) {
        val msgJson = JSONObject().apply {
            put("message", message)
            put("chatId", chatId)
        }
        mSocket.emit("send_message", msgJson)
        binding.etMessage.setText("")
        binding.etMessage.clearFocus()
       var sentMessage = Message(
            _id = UUID.randomUUID().toString(),
            aiContactId = "",
            chatId = chatId,
            createdAt = getUtcDateTime(),
            isRead = false,
            message = message,
            type = "user",
            updatedAt = "",
            userId = ""
        )
        requireActivity().runOnUiThread {
               chatAdapter.addToListSendMessage(listOf(sentMessage))
            binding.rvChatDetails.scrollToPosition(chatAdapter.itemCount - 1)
            binding.imgTyping.visibility = View.VISIBLE
        }
    }


    /**
     * get current date
     */
    private fun getUtcDateTime(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(Date())
    }


    override fun onDestroyView() {
        if (::mSocket.isInitialized) {
            mSocket.disconnect()
            Log.d("SOCKET", "disconnectSocket from server")
        }
        super.onDestroyView()
    }


    /** recycler view item click handel **/

    private fun initChatAdapter() {
//        chatAdapter = SimpleRecyclerViewAdapter(R.layout.rv_chat_item_simple, BR.bean) { v, m, _ ->
//            when (v?.id) {
//
//            }
//        }
//        binding.rvChatDetails.adapter = chatAdapter
    }


}