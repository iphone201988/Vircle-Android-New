package com.tech.vircle.ui.home.friends.create_ai

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import com.tech.vircle.BR
import com.tech.vircle.R
import com.tech.vircle.base.BaseFragment
import com.tech.vircle.base.BaseViewModel
import com.tech.vircle.base.SimpleRecyclerViewAdapter
import com.tech.vircle.data.api.Constants
import com.tech.vircle.data.model.AiContactList
import com.tech.vircle.data.model.CommonModelClass
import com.tech.vircle.data.model.ContactList
import com.tech.vircle.data.model.CreateAiContactResponse
import com.tech.vircle.databinding.CharacteristicsRvItemBinding
import com.tech.vircle.databinding.CommonBottomSheetItemBinding
import com.tech.vircle.databinding.FragmentCreateAiThreeBinding
import com.tech.vircle.databinding.RvCommonItemBinding
import com.tech.vircle.ui.home.friends.FriendsFragmentVM
import com.tech.vircle.utils.BaseCustomBottomSheet
import com.tech.vircle.utils.BindingUtils
import com.tech.vircle.utils.BindingUtils.updateProgress
import com.tech.vircle.utils.Status
import com.tech.vircle.utils.event.DummyList
import com.tech.vircle.utils.event.DummyList.toOrdinalKey
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody


@AndroidEntryPoint
class CreateAiThreeFragment : BaseFragment<FragmentCreateAiThreeBinding>() {
    private val viewModel: FriendsFragmentVM by viewModels()
    private var commonBottomSheet: BaseCustomBottomSheet<CommonBottomSheetItemBinding>? = null
    private lateinit var commonAdapter: SimpleRecyclerViewAdapter<CommonModelClass, RvCommonItemBinding>
    private lateinit var characteristicsAdapter: SimpleRecyclerViewAdapter<String, CharacteristicsRvItemBinding>
    private var weekListType = 0
    private var type: String? = null
    private var contactList: ContactList? = null
    private var apiCallType = 0
    private var selectedCharacteristics = mutableListOf<String>()
    override fun getLayoutResource(): Int {
        return R.layout.fragment_create_ai_three
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(view: View) {
        // click
        initOnCLick()
        // progress bar
        updateProgress(
            requireActivity(), binding.progressBar, binding.tvProgress, 70, "2/3"
        )

        val selectedType = arguments?.getString("selectedType")
        type = when (selectedType) {
            "Expert" -> "new_expert"
            "Companion" -> "new_companion"
            "Assistant" -> "new_assistant"
            "Fictional" -> "new_characters"
            else -> "new_companion"
        }
        if (selectedType.equals("Expert")) {
            binding.apply {
                tvExpert.visibility = View.VISIBLE
                etExpert.visibility = View.VISIBLE
                views.visibility = View.VISIBLE
                ivFilter.visibility = View.VISIBLE
            }
        } else {
            binding.apply {
                tvExpert.visibility = View.GONE
                etExpert.visibility = View.GONE
                views.visibility = View.GONE
                ivFilter.visibility = View.GONE
            }
        }

        // adapter
        initCharacteristicsAdapter()

        // get intent data
        contactList = arguments?.getParcelable<ContactList>("contactData")
        if (contactList != null) {
            apiCallType = 2
            binding.apply {
                binding.tvAiContact.text = getString(R.string.edit_information)
                binding.btnCreateAiContact.text = getString(R.string.save_changes)
                etName.setText(contactList?.name.orEmpty())
                etOtherDetails.setText(contactList?.description.orEmpty())
                etAge.setText(contactList?.age?.toString().orEmpty())
                etGender.setText(contactList?.gender.orEmpty())
                etExpert.setText(contactList?.expertise.orEmpty())
                selectedCharacteristics = contactList?.characterstics as MutableList<String>
                characteristicsAdapter.list = selectedCharacteristics
                etRelationship.setText(contactList?.relationship.orEmpty())
                etCanText.setText(contactList?.canTextEvery.orEmpty())
                etOn.setText(contactList?.on.orEmpty())
                etAt.setText(contactList?.at.orEmpty())
                etWhatDo.setText(contactList?.wantToHear.orEmpty())
                binding.tvAdd.visibility = View.GONE
                binding.tvProgress.visibility = View.GONE
                binding.progressBar.visibility = View.GONE

            }

        } else {
            apiCallType = 1
            binding.tvAiContact.text = getString(R.string.create_ai_contact)
            binding.btnCreateAiContact.text = getString(R.string.create_ai_contact)
            binding.tvAdd.visibility = View.VISIBLE
            binding.tvProgress.visibility = View.VISIBLE
            binding.progressBar.visibility = View.VISIBLE
        }


        val contactData = arguments?.getParcelable<AiContactList>("SecondTypeData")
        contactData?.let { itData ->
            binding.apply {
                etName.setText(itData.name.orEmpty())
                etAge.setText(itData.age?.toString().orEmpty())
                etGender.setText(itData.gender.orEmpty())
                etExpert.setText(itData.expertise.orEmpty())
                selectedCharacteristics = itData.characterstics as MutableList<String>
                characteristicsAdapter.list = selectedCharacteristics
                etRelationship.setText(itData.relationship.orEmpty())
                etCanText.setText(itData.canTextEvery.orEmpty())
                etOtherDetails.setText(itData.description.orEmpty())
                etOn.setText(itData.on.orEmpty())
                etAt.setText(itData.at.orEmpty())
                etWhatDo.setText(itData.wantToHear.orEmpty())
            }
        }
        // initObserver
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
                        "createAiContactApi" -> {
                            try {
                                val myDataModel: CreateAiContactResponse? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (myDataModel != null) {
                                    if (myDataModel.data != null) {
                                        showSuccessToast(myDataModel.message.toString())
                                        val bundle = Bundle()
                                        bundle.putParcelable("userDetails",myDataModel.data)
                                        BindingUtils.navigateWithSlide(
                                            findNavController(),
                                            R.id.navigateToCreateAiContactFourFragment,
                                            bundle
                                        )
                                    }

                                }

                            } catch (e: Exception) {
                                Log.e("error", "createAiContactApi: $e")
                            } finally {
                                hideLoading()
                            }
                        }

                        "updateAiContactApi" -> {
                            try {
                                val myDataModel: CreateAiContactResponse? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (myDataModel != null) {
                                    if (myDataModel.data != null) {
                                        showSuccessToast(myDataModel.message.toString())
                                        BindingUtils.navigateWithSlide(
                                            findNavController(), R.id.fragmentFriends, null
                                        )
                                    }
                                }
                            } catch (e: Exception) {
                                Log.e("error", "updateAiContactApi: $e")
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

    /** handle adapter **/
    private fun initCharacteristicsAdapter() {
        characteristicsAdapter =
            SimpleRecyclerViewAdapter(R.layout.characteristics_rv_item, BR.bean) { v, m, _ ->
                when (v?.id) {
                    R.id.main -> {
                        commonBottomSheet(4)
                    }
                }
            }
        binding.rvCharacteristics.adapter = characteristicsAdapter
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
                // Start Talking button click
                /*   R.id.btnCreateAiContact -> {
                       if (validate()) {
                           val canYou = binding.etCanText.text.toString().trim()
                           val canYouData = when (canYou) {
                               "Day" -> "Daily"
                               "Week" -> "Weekly"
                               "Month" -> "Monthly"
                               "No Schedule" -> "Never"
                               else -> null
                           }
                           val chars = if (selectedCharacteristics.isNotEmpty()) {
                               selectedCharacteristics
                           } else if (binding.etCharacteristics.text.toString().trim().isNotEmpty()) {
                               listOf(binding.etCharacteristics.text.toString().trim())
                           } else {
                               emptyList()
                           }
                           var onValue: String? = null
                           var atValue: String? = null
                           if (binding.etAt.text.toString().trim().isNotEmpty()) {
                               onValue = binding.etOn.text.toString().trim()
                               val time = binding.etAt.text.toString().trim()
                               val hour = time.substringBefore(":").toIntOrNull() ?: 0
                               val suffix = when (hour) {
                                   0 -> "AM"
                                   in 1..11 -> "AM"
                                   else -> "PM"
                               }
                               atValue = "$time$suffix"
                           }
                           val aiContactData = AiContactDataCreate(
                               name = binding.etName.text.toString().trim(),
                               age = binding.etAge.text.toString().trim(),
                               gender = binding.etGender.text.toString().trim(),
                               expertise = binding.etExpert.text.toString().trim(),
                               relationship = binding.etRelationship.text.toString().trim(),
                               canTextEvery = canYouData,
                               description = binding.etOtherDetails.text.toString().trim(),
                               characteristics = chars,
                               on = onValue,
                               at = atValue,
                               wantToHear = binding.etWhatDo.text.toString().trim(),
                               type = type.toString()
                           )
                           // Navigation with bundle
                           val bundle = Bundle().apply {
                               putParcelable("ai_contact_data", aiContactData)
                           }
                           BindingUtils.navigateWithSlide(
                               findNavController(), R.id.navigateToCreateAiContactFourFragment, bundle
                           )
                       }
                   }*/

                R.id.btnCreateAiContact -> {
                    if (validate()) {
                        val data = HashMap<String, RequestBody>()
                        data["name"] = binding.etName.text.toString().trim().toRequestBody()
                        data["age"] = binding.etAge.text.toString().trim().toRequestBody()
                        data["gender"] = binding.etGender.text.toString().trim().toRequestBody()
                        data["expertise"] = binding.etExpert.text.toString().trim().toRequestBody()
                        data["relationship"] =
                            binding.etRelationship.text.toString().trim().toRequestBody()
                        data["description"] =
                            binding.etOtherDetails.text.toString().trim().toRequestBody()

                        val canYou = binding.etCanText.text.toString().trim()
                        if (canYou.isNotEmpty()) {
                            val canYouData = when (canYou) {
                                "Day" -> "Daily"
                                "Week" -> "Weekly"
                                "Month" -> "Monthly"
                                "No Schedule" -> "Never"
                                else -> "Daily"
                            }
                            data["canTextEvery"] = canYouData.toRequestBody()
                        }

                        val ordinals = listOf(
                            "1st",
                            "2nd",
                            "3rd",
                            "4th",
                            "5th",
                            "6th",
                            "7th",
                            "8th",
                            "9th",
                            "10th",
                            "11th",
                            "12th",
                            "13th",
                            "14th",
                            "15th",
                            "16th",
                            "17th",
                            "18th",
                            "19th",
                            "20th",
                            "21st",
                            "22nd",
                            "23rd",
                            "24th",
                            "25th",
                            "26th",
                            "27th",
                            "28th"
                        )

                        val weekLIst = listOf(
                            "Monday",
                            "Tuesday",
                            "Wednesday",
                            "Thursday",
                            "Friday",
                            "Saturday"
                        )

                        if (binding.etOn.text.toString().trim().isNotEmpty()) {
                            val onData = binding.etOn.text.toString().trim()
                            if (onData.contains("Random")) {
                                if (weekListType == 2) {
                                    val randomDay = weekLIst.random()
                                    data["at"] = randomDay.toRequestBody()

                                } else if (weekListType == 3) {
                                    val randomOrdinal = ordinals.random()
                                    data["at"] = randomOrdinal.toRequestBody()

                                }

                            } else {
                                val key = onData.toOrdinalKey()
                                data["at"] = key.toRequestBody()
                                when (onData) {
                                /*   "1st" -> data["at"] = "1st".toRequestBody()
                                    "2st" -> data["at"] = "2nd".toRequestBody()
                                    "3st" -> data["at"] = "3rd".toRequestBody()
                                    "4st" -> data["at"] = "4th".toRequestBody()
                                    "5st" -> data["at"] = "5th".toRequestBody()
                                    "6st" -> data["at"] = "6th".toRequestBody()
                                    "7st" -> data["at"] = "7th".toRequestBody()
                                    "8st" -> data["at"] = "8th".toRequestBody()
                                    "9st" -> data["at"] = "9th".toRequestBody()
                                    "10st" -> data["at"] = "10th".toRequestBody()
                                    "11st" -> data["at"] = "11th".toRequestBody()
                                    "12st" -> data["at"] = "12th".toRequestBody()
                                    "13st" -> data["at"] = "13th".toRequestBody()
                                    "14st" -> data["at"] = "14th".toRequestBody()
                                    "15st" -> data["at"] = "15th".toRequestBody()
                                    "16st" -> data["at"] = "16th".toRequestBody()
                                    "17st" -> data["at"] = "17th".toRequestBody()
                                    "18st" -> data["at"] = "18th".toRequestBody()
                                    "19st" -> data["at"] = "19th".toRequestBody()
                                    "20st" -> data["at"] = "20th".toRequestBody()
                                    "21st" -> data["at"] = "21st".toRequestBody()
                                    "22st" -> data["at"] = "22nd".toRequestBody()
                                    "23st" -> data["at"] = "23rd".toRequestBody()
                                    "24st" -> data["at"] = "24th".toRequestBody()
                                    "25st" -> data["at"] = "25th".toRequestBody()
                                    "26st" -> data["at"] = "26th".toRequestBody()
                                    "27st" -> data["at"] = "27th".toRequestBody()
                                    "28st" -> data["at"] = "28th".toRequestBody()
                                    "Last day of month" -> data["at"] = "Last day of month".toRequestBody()*/
                                }
                            }
                        }
                        if (selectedCharacteristics.isNotEmpty()) {
                            for (i in selectedCharacteristics.indices) {
                                data["characterstics[$i]"] =
                                    selectedCharacteristics[i].toRequestBody()
                            }
                        } else {
                            if (binding.etCharacteristics.text.toString().trim().isNotEmpty()) {
                                selectedCharacteristics.clear()
                                selectedCharacteristics.add(
                                    binding.etCharacteristics.text.toString().trim()
                                )
                                for (i in selectedCharacteristics.indices) {
                                    data["characterstics[$i]"] =
                                        selectedCharacteristics[i].toRequestBody()
                                }
                            }
                        }
                        if (binding.etAt.text.toString().trim().isNotEmpty()) {
                            val time = binding.etAt.text.toString().trim()
                            val hasAmPm = time.endsWith("AM", ignoreCase = true) || time.endsWith(
                                "PM",
                                ignoreCase = true
                            )
                            val finalTime = if (hasAmPm) {
                                time
                            } else {
                                val hour = time.substringBefore(":").toIntOrNull() ?: 0
                                val suffix = when (hour) {
                                    0 -> "AM"
                                    in 1..11 -> "AM"
                                    else -> "PM"
                                }
                                "$time$suffix"
                            }
                            if (time.contains("Random")){
                                val randomList = DummyList.addTimeList().filter { it.category != "Random" }
                                val randomItem = randomList.random()
                                val randomTime = randomItem.category
                                val hour = time.substringBefore(":").toIntOrNull() ?: 0
                                val suffix = when (hour) {
                                    0 -> "AM"
                                    in 1..11 -> "AM"
                                    else -> "PM"
                                }
                                val finalRandomTime = randomTime + suffix
                                data["at"] = finalRandomTime.toRequestBody()
                            }else{
                                data["at"] = finalTime.toRequestBody()
                            }

                        }

                        if(binding.etOn.text.toString().trim().isNotEmpty()) {
                            data["on"] = binding.etOn.text.toString().trim().toRequestBody()
                        }
                        data["wantToHear"] = binding.etWhatDo.text.toString().trim().toRequestBody()

                        Log.i("qwf", Gson().toJson(data))
                        if (apiCallType == 1) {
                            data["type"] = type.toString().toRequestBody()
                            viewModel.createAiContactApi(Constants.AI_CONTACT_ADD, data)
                        } else {
                            viewModel.updateAiContactApi(
                                Constants.AI_CONTACT_UPDATE + "/${contactList?._id}",
                                data
                            )
                        }

                    }
                }

                // etAge edit text click
                R.id.etAge -> {
                    BindingUtils.preventMultipleClick(it)
                    commonBottomSheet(1)
                }

                // etGender edit text click
                R.id.etGender -> {
                    BindingUtils.preventMultipleClick(it)
                    commonBottomSheet(2)
                }
                // etExpert edit text click
                R.id.etExpert -> {
                    BindingUtils.preventMultipleClick(it)
                    commonBottomSheet(3)
                }

                // etCharacteristics edit text click
                R.id.etCharacteristics -> {
                    BindingUtils.preventMultipleClick(it)
                    commonBottomSheet(4)
                }
                // etRelationship edit text click
                R.id.etRelationship -> {
                    BindingUtils.preventMultipleClick(it)
                    commonBottomSheet(5)
                }

                // etCanText edit text click
                R.id.etCanText -> {
                    BindingUtils.preventMultipleClick(it)
                    commonBottomSheet(6)
                }

                // etOn edit text click
                R.id.etOn -> {
                    BindingUtils.preventMultipleClick(it)
                    commonBottomSheet(7)
                }

                // etAt edit text click
                R.id.etAt -> {
                    BindingUtils.preventMultipleClick(it)
                    commonBottomSheet(8)
                }
                // etWhatDo edit text click
                R.id.etWhatDo -> {
                    BindingUtils.preventMultipleClick(it)
                    commonBottomSheet(9)
                }


            }
        }
        // et can text
        binding.etCanText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val text = s?.toString()?.trim() ?: return
                when {
                    text.contains("Week") -> {
                        weekListType = 2
                        setEtOnEnabled(true)
                        binding.etOn.setText("")
                    }

                    text.contains("Month") -> {
                        weekListType = 3
                        setEtOnEnabled(true)
                        binding.etOn.setText("")
                    }

                    else -> {
                        binding.etOn.setText("")
                        setEtOnEnabled(false)
                    }
                }
            }

        })
        // text watcher
        setupTextWatchers()
    }


    // Call this inside onCreateView or onViewCreated
    private fun setupTextWatchers() {
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                checkFields()
            }
        }

        binding.etName.addTextChangedListener(watcher)
        binding.etAge.addTextChangedListener(watcher)
        binding.etGender.addTextChangedListener(watcher)

        // initial state check
        checkFields()
    }

    private fun checkFields() {
        val isNameFilled = binding.etName.text.toString().trim().isNotEmpty()
        val isAgeFilled = binding.etAge.text.toString().trim().isNotEmpty()
        val isGenderFilled = binding.etGender.text.toString().trim().isNotEmpty()

        val allFilled = isNameFilled && isAgeFilled && isGenderFilled

        binding.btnCreateAiContact.isEnabled = allFilled
        binding.btnCreateAiContact.alpha =
            if (allFilled) 1f else 0.5f // optional: dim when disabled
    }


    /**
     * on button  click handel
     */
    private fun setEtOnEnabled(isEnabled: Boolean) {
        binding.etOn.apply {
            this.isEnabled = isEnabled
            this.isClickable = isEnabled
        }
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

            when (type) {
                1 -> binding.tvCommon.text = getString(R.string.age)
                2 -> binding.tvCommon.text = getString(R.string.gender)
                3 -> binding.tvCommon.text = getString(R.string.expertise)
                4 -> binding.tvCommon.text = getString(R.string.characteristics)
                5 -> binding.tvCommon.text = getString(R.string.relationship)
                6 -> binding.tvCommon.text = getString(R.string.can_text_you_every)
                7 -> binding.tvCommon.text = getString(R.string.on)
                8 -> binding.tvCommon.text = getString(R.string.at)
                9 -> binding.tvCommon.text = getString(R.string.what_do_you_want_to_hear)
            }
        }
        initCommonAdapter(type)

        commonBottomSheet?.setOnDismissListener {
            if (type == 4) {
                if (binding.etCharacteristics.text.toString().trim() == "Other") {
                    binding.rvCharacteristics.visibility = View.GONE
                    binding.etCharacteristics.visibility = View.VISIBLE
                    selectedCharacteristics.clear()
                    binding.etCharacteristics.apply {
                        isFocusableInTouchMode = true
                        setText("")
                        requestFocus()
                        postDelayed({
                            val imm =
                                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                            imm.showSoftInput(this, InputMethodManager.SHOW_FORCED)
                        }, 500)
                    }
                } else {
                    binding.etCharacteristics.setText("")
                    binding.rvCharacteristics.visibility = View.VISIBLE
                    characteristicsAdapter.list = selectedCharacteristics
                }
            }
        }

    }


    /** handle adapter **/
    private fun initCommonAdapter(type: Int) {
        commonAdapter = SimpleRecyclerViewAdapter(R.layout.rv_common_item, BR.bean) { v, m, pos ->
            when (v?.id) {
                R.id.main -> {
                    if (type == 4) {
                    } else {
                        for (i in commonAdapter.list) {
                            i.isStatus = i.category == m.category
                        }
                    }

                    commonAdapter.notifyDataSetChanged()
                    if (m.category == "Other") {
                        commonBottomSheet?.dismiss()
                        if (type == 4) {
                            binding.etCharacteristics.setText(m.category)
                        }
                        val targetEt = when (type) {
                            1 -> binding.etAge
                            2 -> binding.etGender
                            3 -> binding.etExpert
                            5 -> binding.etRelationship
                            else -> null
                        }

                        targetEt?.setText("")
                        targetEt?.apply {
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
                        when (type) {
                            1 -> binding.etAge.setText(m.category)
                            2 -> binding.etGender.setText(m.category)
                            3 -> binding.etExpert.setText(m.category)
                            5 -> binding.etRelationship.setText(m.category)
                            6 -> binding.etCanText.setText(m.category)
                            7 -> binding.etOn.setText(m.category)
                            8 -> binding.etAt.setText(m.category)
                            9 -> binding.etWhatDo.setText(m.category)
                            4 -> {
                                if (!m.isStatus) {
                                    if (selectedCharacteristics.size >= 10) {
                                        showInfoToast("You can select up to 10 items only")
                                        return@SimpleRecyclerViewAdapter
                                    }
                                    m.isStatus = true
                                    selectedCharacteristics.add(m.category)
                                } else {
                                    m.isStatus = false
                                    selectedCharacteristics.remove(m.category)
                                }
                                commonAdapter.notifyItemChanged(pos)
                            }
                        }
                    }

                }
            }
        }

        commonAdapter.list = when (type) {
            1 -> DummyList.addAgeList()
            2 -> DummyList.addGenderList()
            3 -> DummyList.addExpertiseList()
            4 -> DummyList.addCharacteristicsList()
            5 -> DummyList.addRelationshipList()
            6 -> DummyList.addCanTextEveryList()
            7 -> {
                when (weekListType) {
                    2 -> DummyList.addWeekList()
                    3 -> DummyList.addMonthList()
                    else -> DummyList.addWeekList()
                }
            }

            8 -> DummyList.addTimeList()
            9 -> DummyList.addWantToHearList()
            else -> DummyList.addGenderList()
        }.apply {
                when (type) {
                    1 -> {
                        val selectedAge = binding.etAge.text.toString().trim()
                        forEach { item ->
                            item.isStatus = selectedAge.isNotEmpty() && item.category.equals(
                                selectedAge, ignoreCase = true
                            )
                        }
                    }

                    2 -> {
                        val selectedGender = binding.etGender.text.toString().trim()
                        forEach { item ->
                            item.isStatus = selectedGender.isNotEmpty() && item.category.equals(
                                selectedGender, ignoreCase = true
                            )
                        }
                    }

                    3 -> {
                        val selectedExpert = binding.etExpert.text.toString().trim()
                        forEach { item ->
                            item.isStatus = selectedExpert.isNotEmpty() && item.category.equals(
                                selectedExpert, ignoreCase = true
                            )
                        }
                    }

                    4 -> {
                        forEach { item ->
                            item.isStatus = selectedCharacteristics.contains(item.category)
                        }
                    }

                    5 -> {
                        val selectRelationship = binding.etRelationship.text.toString().trim()
                        forEach { item ->
                            item.isStatus = selectRelationship.isNotEmpty() && item.category.equals(
                                selectRelationship, ignoreCase = true
                            )
                        }
                    }

                    6 -> {
                        val selectCanText = binding.etCanText.text.toString().trim()
                        val canYouData = when (selectCanText) {
                            "Daily", "Day" -> "Day"
                            "Weekly", "Week" -> "Week"
                            "Monthly", "Month" -> "Month"
                            "Never", "No Schedule" -> "No Schedule"
                            else -> "Day"
                        }


                        forEach { item ->
                            item.isStatus = canYouData.isNotEmpty() && item.category.contains(
                                canYouData,
                                ignoreCase = true
                            )
                        }
                    }

                    7 -> {
                        val selectOn = binding.etOn.text.toString().trim()
                        forEach { item ->
                            item.isStatus = selectOn.isNotEmpty() && item.category.equals(
                                selectOn, ignoreCase = true
                            )
                        }
                    }

                    8 -> {
                        val selectAt = binding.etAt.text.toString().trim()
                        forEach { item ->
                            item.isStatus = selectAt.isNotEmpty() && item.category.equals(
                                selectAt, ignoreCase = true
                            )
                        }
                    }

                    9 -> {
                        val selectWhatDo = binding.etWhatDo.text.toString().trim()
                        forEach { item ->
                            item.isStatus = selectWhatDo.isNotEmpty() && item.category.equals(
                                selectWhatDo, ignoreCase = true
                            )
                        }
                    }

                }

            }
        commonBottomSheet?.binding?.rvCommon?.adapter = commonAdapter
    }


    /*** add validation ***/
    private fun validate(): Boolean {
        val name = binding.etName.text.toString().trim()
        val gender = binding.etGender.text.toString().trim()
        val age = binding.etAge.text.toString().trim()
        if (name.isEmpty()) {
            showInfoToast("Please enter name")
            return false
        } else if (gender.isEmpty()) {
            showInfoToast("Please enter email")
            return false
        } else if (age.isEmpty()) {
            showInfoToast("Please enter age")
            return false
        }

        return true
    }


}