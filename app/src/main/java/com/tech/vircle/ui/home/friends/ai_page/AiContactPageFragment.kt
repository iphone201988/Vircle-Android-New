package com.tech.vircle.ui.home.friends.ai_page

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.util.FileUtil
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.tech.vircle.BR
import com.tech.vircle.R
import com.tech.vircle.base.BaseFragment
import com.tech.vircle.base.BaseViewModel
import com.tech.vircle.base.SimpleRecyclerViewAdapter
import com.tech.vircle.data.api.Constants
import com.tech.vircle.data.model.CommonModelClass
import com.tech.vircle.data.model.ContactList
import com.tech.vircle.databinding.CharacteristicsRvItemBinding
import com.tech.vircle.databinding.CommonBottomSheetItemBinding
import com.tech.vircle.databinding.DialogDeleteLogoutBinding
import com.tech.vircle.databinding.FragmentAiContactPageBinding
import com.tech.vircle.databinding.RvCommonItemBinding
import com.tech.vircle.databinding.VideoImagePickerDialogBoxBinding
import com.tech.vircle.ui.home.friends.FriendsFragmentVM
import com.tech.vircle.utils.AppUtils
import com.tech.vircle.utils.BaseCustomBottomSheet
import com.tech.vircle.utils.BaseCustomDialog
import com.tech.vircle.utils.BindingUtils
import com.tech.vircle.utils.Status
import com.tech.vircle.utils.event.DummyList
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException


@AndroidEntryPoint
class AiContactPageFragment : BaseFragment<FragmentAiContactPageBinding>() {
    private val viewModel: FriendsFragmentVM by viewModels()
    private var commonBottomSheet: BaseCustomBottomSheet<CommonBottomSheetItemBinding>? = null
    private lateinit var commonAdapter: SimpleRecyclerViewAdapter<CommonModelClass, RvCommonItemBinding>
    private lateinit var characteristicsAdapter: SimpleRecyclerViewAdapter<String, CharacteristicsRvItemBinding>
    private var selectedCharacteristics = mutableListOf<String>()
    private var imageDialog: BaseCustomDialog<VideoImagePickerDialogBoxBinding>? = null
    private var commonDialog: BaseCustomDialog<DialogDeleteLogoutBinding>? = null
    private var contactList: ContactList? = null
    private var photoFile2: File? = null
    private var photoURI: Uri? = null
    private var multipartPart: MultipartBody.Part? = null
    private var userName = ""
    override fun getLayoutResource(): Int {
        return R.layout.fragment_ai_contact_page
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // adapter
        initCharacteristicsAdapter()
        // get intent data
        contactList = arguments?.getParcelable<ContactList>("data")
        if (contactList != null) {
            binding.bean = contactList
            userName = contactList?.name.toString()
            val reverseTypeMap = mapOf(
                "new_expert" to "Expert",
                "new_companion" to "Companion",
                "new_assistant" to "Assistant",
                "new_characters" to "Fictional"
            )
            val type = reverseTypeMap[contactList?.type] ?: "Unknown"
            binding.tvCategory.text = type
            selectedCharacteristics = contactList?.characterstics as MutableList<String>
            characteristicsAdapter.list = selectedCharacteristics
        }

        // click
        initOnCLick()

        // observer
        initObserver()
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

                // etRelationship  click
                R.id.etRelationship -> {
                    BindingUtils.preventMultipleClick(it)
                    commonBottomSheet(1)
                }
                // edit contact
                R.id.clEdit -> {
                    val bundle = Bundle()
                    bundle.putParcelable("contactData", contactList)
                    BindingUtils.navigateWithSlide(
                        findNavController(),
                        R.id.navigateToCreateAiContactThreeFragment,
                        bundle
                    )
                }

                // etCharacteristics edit text click
                R.id.etCharacteristics -> {
                    BindingUtils.preventMultipleClick(it)
                    commonBottomSheet(4)
                }

                // iv edit
                R.id.ivEdit -> {
                    imageDialog()
                }
                // delete contact ai
                R.id.clRemove -> {
                    initLogoutDialog(1)
                }
                // clear chat ai
                R.id.btnCleanTheChat -> {
                    initLogoutDialog(2)
                }

            }
        }
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
                        "updateAiImageApi" -> {
                            try {
                                showSuccessToast("Profile updated")
                            } catch (e: Exception) {
                                Log.e("error", "createAiImageApi: $e")
                            } finally {
                                hideLoading()
                            }
                        }

                        "deleteAiContactApi" -> {
                            try {
                                showSuccessToast("AI contact deleted successfully")
                                findNavController().popBackStack()
                            } catch (e: Exception) {
                                Log.e("error", "deleteAiContactApi: $e")
                            } finally {
                                hideLoading()
                            }
                        }

                        "clearChatAi" -> {
                            try {
                                showSuccessToast("Messages deleted successfully")
                            } catch (e: Exception) {
                                Log.e("error", "deleteAiContactApi: $e")
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

            if (type == 4) {
                binding.tvCommon.text = getString(R.string.characteristics)
            } else if (type == 1) {
                binding.tvCommon.text = getString(R.string.relationship)
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
                            1 -> binding.etRelationship
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
                            1 -> binding.etRelationship.setText(m.category)
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
            1 -> DummyList.addRelationshipList()
            4 -> DummyList.addCharacteristicsList()
            else -> DummyList.addRelationshipList()
        }.apply {
                when (type) {
                    1 -> {
                        val selectedRelationShip = binding.etRelationship.text.toString().trim()
                        Log.d("dgdfg", "initCommonAdapter: $selectedRelationShip")
                        forEach { item ->
                            item.isStatus =
                                selectedRelationShip.isNotEmpty() && item.category.equals(
                                    selectedRelationShip,
                                    ignoreCase = true
                                )
                        }
                    }

                    4 -> {
                        forEach { item ->
                            item.isStatus = selectedCharacteristics.contains(item.category)
                        }
                    }
                }

            }
        commonBottomSheet?.binding?.rvCommon?.adapter = commonAdapter
    }


    /** handle adapter **/
    private fun initCharacteristicsAdapter() {
        characteristicsAdapter =
            SimpleRecyclerViewAdapter(R.layout.characteristics_rv_item, BR.bean) { v, m, _ ->
                when (v?.id) {
                    R.id.main -> {
                        //   commonBottomSheet(4)
                    }
                }
            }
        binding.rvCharacteristics.adapter = characteristicsAdapter
    }


    /**** Edit date and time dialog  handel ***/
    private fun imageDialog() {
        imageDialog = BaseCustomDialog(requireActivity(), R.layout.video_image_picker_dialog_box) {
            when (it.id) {
                R.id.tvCamera, R.id.imageCamera -> {
                    if (!BindingUtils.hasPermissions(
                            requireActivity(), BindingUtils.permissions
                        )
                    ) {
                        permissionResultLauncher1.launch(BindingUtils.permissions)
                    } else {
                        // camera
                        openCameraIntent()
                    }
                    imageDialog!!.dismiss()
                }

                R.id.imageGallery, R.id.tvGallery -> {
                    if (!BindingUtils.hasPermissions(
                            requireActivity(), BindingUtils.permissions
                        )
                    ) {
                        permissionResultLauncher.launch(BindingUtils.permissions)

                    } else {
                        galleryImagePicker()

                    }
                    imageDialog!!.dismiss()
                }

            }
        }
        imageDialog!!.create()
        imageDialog!!.show()

    }

    /**** Gallery permission  ***/
    private var allGranted = false
    private val permissionResultLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            for (it in permissions.entries) {
                it.key
                val isGranted = it.value
                allGranted = isGranted
            }
            when {
                allGranted -> {
                    galleryImagePicker()
                }

                else -> {
                    showInfoToast("Permission Denied")
                }
            }
        }

    /*** open gallery ***/
    private fun galleryImagePicker() {
        val pictureActionIntent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI).apply {
                type = "image/*"  // Restrict to images only
            }
        resultLauncherGallery.launch(pictureActionIntent)
    }


    /*** gallery launcher ***/
    private var resultLauncherGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                val imageUri = data?.data

                imageUri?.let { uri ->
                    try {
                        Glide.with(requireActivity()).load(imageUri).centerCrop()
                            .into(binding.ivPerson)
                        // binding.ivPerson.setImageURI(imageUri)
                        multipartPart = convertMultipartPartGal(uri)
                        viewModel.updateAiImageApi(
                            Constants.AI_CONTACT_UPDATE + "/${contactList?._id}",
                            multipartPart
                        )

                    } catch (e: Exception) {
                        e.printStackTrace()
                        showErrorToast("Image compression failed")
                    }

                }
            }
        }


    private val permissionResultLauncher1: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val allGranted = permissions.all { it.value }
            if (allGranted) {
                openCameraIntent()
            } else {
                showInfoToast("Permission Denied")
            }
        }

    /**** open camera ***/
    private fun openCameraIntent() {
        val pictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (pictureIntent.resolveActivity(requireActivity().packageManager) != null) {
            try {
                photoFile2 = AppUtils.createImageFile1(requireActivity())
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
            if (photoFile2 != null) {
                photoURI = FileProvider.getUriForFile(
                    requireActivity(), "com.tech.vircle.fileProvider", photoFile2!!
                )
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                resultLauncherCamera.launch(pictureIntent)
            } else {
                Log.d("TAG", "openCameraIntent: ")
            }
        } else {
            Log.d("TAG", "openCameraIntent: ")
        }
    }

    /*** camera launcher ***/
    private val resultLauncherCamera =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                if (photoFile2?.exists() == true) {
                    val imagePath = photoFile2?.absolutePath.toString()
                    val imageUri = imagePath.toUri()

                    lifecycleScope.launch {
                        try {
                            Glide.with(requireActivity()).load(imageUri).centerCrop()
                                .into(binding.ivPerson)
                            //binding.ivPerson.setImageURI(imageUri)
                            multipartPart = convertMultipartPart(imageUri)
                            viewModel.updateAiImageApi(
                                Constants.AI_CONTACT_UPDATE + "/${contactList?._id}",
                                multipartPart
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                            showErrorToast("Compression fail")
                        }
                    }
                }
            }
        }

    /*** convert image in multipart ***/
    private fun convertMultipartPart(imageUri: Uri): MultipartBody.Part? {
        val filePath = imageUri.path ?: return null
        val file = File(filePath)
        if (!file.exists()) {
            return null
        }
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("aiAvatar", file.name, requestFile)
    }

    private fun convertMultipartPartGal(imageUri: Uri): MultipartBody.Part {
        val file = FileUtil.getTempFile(requireActivity(), imageUri)
        val fileName =
            "${file!!.nameWithoutExtension}_${System.currentTimeMillis()}.${file.extension}"
        val newFile = File(file.parent, fileName)
        file.renameTo(newFile)
        return MultipartBody.Part.createFormData(
            "aiAvatar", newFile.name, newFile.asRequestBody("image/*".toMediaTypeOrNull())
        )
    }


    /** dialog **/
    private fun initLogoutDialog(type: Int) {
        commonDialog = BaseCustomDialog(requireActivity(), R.layout.dialog_delete_logout) {
            when (it?.id) {
                R.id.tvCancel -> {
                    commonDialog?.dismiss()
                }

                R.id.tvLogout -> {
                    if (type == 1) {
                        viewModel.deleteAiContactApi(Constants.AI_CONTACT_DELETE + "/" + "${contactList?._id}")
                    } else {
                        viewModel.clearChatAi(Constants.CLEAR_CHAT + "/" + "${contactList?.chatIds}")
                    }
                    commonDialog?.dismiss()
                }
            }

        }
        commonDialog?.setCancelable(false)
        commonDialog?.create()
        commonDialog?.show()

        commonDialog?.binding?.apply {
            if (type == 1) {
                tvTitle.text = getString(R.string.delete_ai_contact)
                tvSubHeading.text = getString(R.string.are_you_sure_to_delete_contact)
                tvLogout.text = getString(R.string.delete)
            } else {
                tvTitle.text = getString(R.string.clear_chat)
                tvSubHeading.text = getString(R.string.are_you_sure_to_delete_chat)
                tvLogout.text = getString(R.string.clear)
            }

        }

    }


}
