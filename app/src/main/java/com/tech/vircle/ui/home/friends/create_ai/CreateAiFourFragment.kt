package com.tech.vircle.ui.home.friends.create_ai

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
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
import com.tech.vircle.data.model.CreateAiContactResponse
import com.tech.vircle.data.model.CreateAiData
import com.tech.vircle.data.model.GetAvtarResponse
import com.tech.vircle.databinding.AvatarBottomSheetBinding
import com.tech.vircle.databinding.FragmentCreateAiFourBinding
import com.tech.vircle.databinding.RvAvtarItemBinding
import com.tech.vircle.databinding.VideoImagePickerDialogBoxBinding
import com.tech.vircle.ui.home.friends.FriendsFragmentVM
import com.tech.vircle.utils.AppUtils
import com.tech.vircle.utils.BaseCustomBottomSheet
import com.tech.vircle.utils.BaseCustomDialog
import com.tech.vircle.utils.BindingUtils
import com.tech.vircle.utils.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.IOException


@AndroidEntryPoint
class CreateAiFourFragment : BaseFragment<FragmentCreateAiFourBinding>() {
    private val viewModel: FriendsFragmentVM by viewModels()
    private var avatarBottomSheet: BaseCustomBottomSheet<AvatarBottomSheetBinding>? = null
    private lateinit var avatarAdapter: SimpleRecyclerViewAdapter<String, RvAvtarItemBinding>
    private var imageDialog: BaseCustomDialog<VideoImagePickerDialogBoxBinding>? = null
    private var photoFile2: File? = null
    private var photoURI: Uri? = null
    private var multipartPart: MultipartBody.Part? = null
    private var avtarName = ""
    private var userId: String? = null
    private var userChatId: String? = null
    private lateinit var aiContact: CreateAiData
    override fun getLayoutResource(): Int {
        return R.layout.fragment_create_ai_four
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // click
        initOnCLick()
        // observer
        initObserver()
        val userDetails = arguments?.getParcelable<CreateAiData>("userDetails")
        if (userDetails != null) {
            userId = userDetails.contact?._id
            userChatId = userDetails.chatId
            aiContact = userDetails
            Glide.with(requireContext())
                .load(Constants.MEDIA_BASE_URL +  aiContact.contact?.aiAvatar)
                .placeholder(R.drawable.profilephoto)
                .error(R.drawable.profilephoto).into(binding.ivPerson)
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
                                val myDataModel: CreateAiContactResponse? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (myDataModel != null) {
                                    if (myDataModel.data != null) {
                                        aiContact = myDataModel.data
                                        showSuccessToast(myDataModel.message.toString())
                                        Glide.with(requireContext())
                                            .load(Constants.MEDIA_BASE_URL + myDataModel.data.contact?.aiAvatar)
                                            .placeholder(R.drawable.profilephoto)
                                            .error(R.drawable.profilephoto).into(binding.ivPerson)
                                    }
                                }

                            } catch (e: Exception) {
                                Log.e("error", "createAiContactApi: $e")
                            } finally {
                                hideLoading()
                            }
                        }

                        "getAvtarApi" -> {
                            try {
                                val myDataModel: GetAvtarResponse? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (myDataModel != null) {
                                    showSuccessToast(myDataModel.message.toString())
                                    avatarBottomSheet()
                                    if (myDataModel.avatars?.avatars?.size!! > 0) {
                                        avatarAdapter.list = myDataModel.avatars.avatars
                                    }
                                }

                            } catch (e: Exception) {
                                Log.e("error", "updateAiImageApi: $e")
                            } finally {
                                hideLoading()
                            }
                        }

                        "updateAiAvtarApi" -> {
                            try {
                                val myDataModel: CreateAiContactResponse? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (myDataModel != null) {
                                    if (myDataModel.data != null) {
                                        aiContact = myDataModel.data
                                        showSuccessToast(myDataModel.message.toString())
                                        Glide.with(requireContext())
                                            .load(Constants.MEDIA_BASE_URL + myDataModel.data.contact?.aiAvatar)
                                            .placeholder(R.drawable.profilephoto)
                                            .error(R.drawable.profilephoto).into(binding.ivPerson)
                                    }
                                }

                            } catch (e: Exception) {
                                Log.e("error", "createAiContactApi: $e")
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
        viewModel.onClick.observe(viewLifecycleOwner) { it ->
            when (it?.id) {
                // back button click
                R.id.ivBack -> {
                    BindingUtils.navigateWithSlide(
                        findNavController(), R.id.fragmentFriends, null
                    )
                }

                // Start Talking button click
                R.id.btnStartTalking -> {/*     if (multipartPart != null) {
                        aiContactData?.let {
                            val data = HashMap<String, RequestBody>()
                            data["name"] = it.name.toRequestBody()
                            data["age"] = it.age.toRequestBody()
                            data["gender"] = it.gender.toRequestBody()
                            data["expertise"] = it.expertise.toRequestBody()
                            data["relationship"] = it.relationship.toRequestBody()
                            it.canTextEvery?.let { value ->
                                data["canTextEvery"] =
                                    value.toRequestBody("text/plain".toMediaTypeOrNull())
                            }
                            it.at?.let { value ->
                                data["at"] = value.toRequestBody("text/plain".toMediaTypeOrNull())
                            }
                            it.on?.let { value ->
                                data["on"] = value.toRequestBody("text/plain".toMediaTypeOrNull())
                            }
                            data["wantToHear"] = it.wantToHear.toRequestBody()
                            data["type"] = it.type.toRequestBody()
                            if (it.characteristics.isNotEmpty()) {
                                for (i in it.characteristics.indices) {
                                    data["characterstics[$i]"] =
                                        it.characteristics[i].toRequestBody()
                                }
                            }
                            // API call with only + data
                            viewModel.createAiContactApi(Constants.AI_CONTACT_ADD, data,)
                        }
                    } else {
                        showInfoToast("Please add a image")
                    }*/

                    val bundle = Bundle()
                    bundle.putParcelable("userAiContact", aiContact)
                    bundle.putString("chatId", userChatId)
                    BindingUtils.navigateWithSlide(
                        findNavController(), R.id.navigateToChatDetailsFragment, bundle
                    )
                }
                // btn Choose From Library button click
                R.id.btnChooseFromLibrary -> {
                    // api call
                    viewModel.getAvtarApi(Constants.GET_AVTAR)
                }
                // iv edit
                R.id.ivEdit -> {
                    imageDialog()
                }


            }
        }
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

                        viewModel.updateAiImageApi(Constants.AI_CONTACT_UPDATE+"/$userId", multipartPart)
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

                            viewModel.updateAiImageApi(Constants.AI_CONTACT_UPDATE+"/$userId", multipartPart)
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


    /****
     * common bottom sheet item
     ****/
    private fun avatarBottomSheet() {
        avatarBottomSheet = BaseCustomBottomSheet(
            requireContext(), R.layout.avatar_bottom_sheet
        ) {
            when (it?.id) {
                R.id.backButton -> {
                    avatarBottomSheet?.dismiss()
                }

                R.id.btnIAccept -> {
                    avatarBottomSheet?.dismiss()
                }
            }

        }
        avatarBottomSheet?.apply {
            behavior.isDraggable = true
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            create()
            window?.attributes?.windowAnimations = R.style.BottomSheetAnimationSecond
            show()

            binding.tvCommon.text = getString(R.string.avatar)
        }
        initCommonAdapter()

    }


    /** handle adapter **/
    private fun initCommonAdapter() {
        avatarAdapter = SimpleRecyclerViewAdapter(R.layout.rv_avtar_item, BR.bean) { v, m, pos ->
            when (v?.id) {
                R.id.main -> {
                    avtarName = m
                    Glide.with(requireContext()).load(Constants.MEDIA_BASE_URL + m)
                        .placeholder(R.drawable.profilephoto).error(R.drawable.profilephoto)
                        .into(binding.ivPerson)
                    avatarBottomSheet?.dismiss()
                    val data = HashMap<String, RequestBody>()
                    data["definedAvatar"] = avtarName.toRequestBody()
                    if (userId != null) {
                        viewModel.updateAiAvtarApi(Constants.AI_CONTACT_UPDATE + "/$userId", data)
                    }

                }
            }
        }
        avatarBottomSheet?.binding?.rvAvatar?.adapter = avatarAdapter
    }


}