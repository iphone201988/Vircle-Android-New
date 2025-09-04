package com.tech.vircle.ui.home.settings.profile

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatEditText
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
import com.tech.vircle.data.model.ForgotPasswordResponse
import com.tech.vircle.data.model.GetUserprofileResponse
import com.tech.vircle.databinding.CharacteristicsRvItemBinding
import com.tech.vircle.databinding.CommonBottomSheetItemBinding
import com.tech.vircle.databinding.DialogDeleteLogoutBinding
import com.tech.vircle.databinding.FragmentProfileBinding
import com.tech.vircle.databinding.RvCommonItemBinding
import com.tech.vircle.databinding.VideoImagePickerDialogBoxBinding
import com.tech.vircle.ui.home.settings.SettingsFragmentVM
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
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.IOException
import java.util.Locale

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>() {
    private val viewModel: SettingsFragmentVM by viewModels()
    private var commonBottomSheet: BaseCustomBottomSheet<CommonBottomSheetItemBinding>? = null
    private lateinit var commonAdapter: SimpleRecyclerViewAdapter<CommonModelClass, RvCommonItemBinding>
    private lateinit var characteristicsAdapter: SimpleRecyclerViewAdapter<String, CharacteristicsRvItemBinding>
    private var deleteDialog: BaseCustomDialog<DialogDeleteLogoutBinding>? = null
    private var imageDialog: BaseCustomDialog<VideoImagePickerDialogBoxBinding>? = null
    private var photoFile2: File? = null
    private var photoURI: Uri? = null
    private var multipartPart: MultipartBody.Part? = null
    private var selectedCharacteristics = mutableListOf<String>()
    override fun getLayoutResource(): Int {
        return R.layout.fragment_profile
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // click
        initOnClick()
        // adapter
        initObserver()
        initCharacteristicsAdapter()
        // api call
        viewModel.getProfileApi(Constants.USER_GET_PROFILE)
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
                        "deleteApi" -> {
                            try {
                                val myDataModel: ForgotPasswordResponse? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (myDataModel != null) {
                                    showSuccessToast(it.message.toString())
                                    sharedPrefManager.clear()
                                    findNavController().popBackStack(R.id.auth_navigation, true)
                                    BindingUtils.navigateWithSlide(
                                        findNavController(),
                                        R.id.navigateToLoginFragment,
                                        null
                                    )
                                }
                            } catch (e: Exception) {
                                Log.e("error", "deleteApi: $e")
                            } finally {
                                hideLoading()
                            }
                        }

                        "getProfileApi" -> {
                            try {
                                val myDataModel: GetUserprofileResponse? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (myDataModel != null) {
                                    if (myDataModel.data != null) {
                                        sharedPrefManager.setProfileData(myDataModel.data.user)
                                        binding.bean = myDataModel.data.user
                                        selectedCharacteristics = myDataModel.data.user?.characterstics as MutableList<String>
                                        characteristicsAdapter.list = selectedCharacteristics
                                        if (characteristicsAdapter.list.isNotEmpty()) {
                                            binding.rvCharacteristics.visibility = View.VISIBLE
                                            binding.etCharacteristics.visibility = View.INVISIBLE
                                        } else {
                                            binding.etCharacteristics.visibility = View.VISIBLE
                                            binding.rvCharacteristics.visibility = View.GONE
                                        }
                                    }
                                }
                            } catch (e: Exception) {
                                Log.e("error", "getProfileApi: $e")
                            } finally {
                                hideLoading()
                            }
                        }

                        "updateProfileApi"->{
                            try {
                                val myDataModel: GetUserprofileResponse? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (myDataModel != null) {
                                    showSuccessToast(myDataModel.message.toString())
                                    if (myDataModel.data != null) {
                                        sharedPrefManager.setProfileData(myDataModel.data.user)

                                    }
                                }
                            } catch (e: Exception) {
                                Log.e("error", "getProfileApi: $e")
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
     * click handel event
     */
    private fun initOnClick() {
        viewModel.onClick.observe(viewLifecycleOwner) {
            when (it?.id) {
                // back button click
                R.id.ivBack -> {
                    findNavController().popBackStack()
                }
                // delete button click
                R.id.btnDeleteMyAccount -> {
                    initDeleteDialog()
                }
                // save changes button click
                R.id.btnSaveChanges -> {
                    val name = binding.etName.text.toString().trim()
                    val surname = binding.etSurname.text.toString().trim()
                    val gender = binding.etGender.text.toString().trim()
                    val age = binding.etAge.text.toString().trim()
                    val personalDetails = binding.etPersonalDetails.text.toString().trim()
                    val data = HashMap<String, RequestBody>()
                    data["age"] = age.toRequestBody()
                    data["gender"] = gender.lowercase(Locale.US).toRequestBody()
                    data["name"] = name.toRequestBody()
                    if (selectedCharacteristics.isNotEmpty()) {
                        for (i in selectedCharacteristics.indices) {
                            data["characterstics[$i]"] = selectedCharacteristics[i].toRequestBody()
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
                    if (personalDetails.isNotEmpty()) {
                        data["personal_details"] = personalDetails.toRequestBody()
                    }
                    data["surname"] = surname.toRequestBody()
                    if (multipartPart != null) {
                        viewModel.updateProfileApi(
                            Constants.USER_UPDATE_PROFILE, data, multipartPart
                        )
                    } else {
                        viewModel.updateProfileApi(
                            Constants.USER_UPDATE_PROFILE, data, null
                        )
                    }

                }
                // gender button click
                R.id.etGender -> {
                    BindingUtils.preventMultipleClick(it)
                    commonBottomSheet(1)
                }

                // age button click
                R.id.etAge -> {
                    BindingUtils.preventMultipleClick(it)
                    commonBottomSheet(2)
                }
                // Characteristics button click
                R.id.etCharacteristics -> {
                    BindingUtils.preventMultipleClick(it)
                    commonBottomSheet(3)
                }

                // edit button click
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
                type = "image/*"
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
                        Glide.with(requireActivity())
                            .load(imageUri)
                            .centerCrop()
                            .into(binding.ivPerson)
                    //    binding.ivPerson.setImageURI(imageUri)
                        multipartPart = convertMultipartPartGal(uri)

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
                            Glide.with(requireActivity())
                                .load(imageUri)
                                .centerCrop()
                                .into(binding.ivPerson)
                       //     binding.ivPerson.setImageURI(imageUri)
                            multipartPart = convertMultipartPart(imageUri)
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
        return MultipartBody.Part.createFormData("avatar", file.name, requestFile)
    }

    private fun convertMultipartPartGal(imageUri: Uri): MultipartBody.Part {
        val file = FileUtil.getTempFile(requireActivity(), imageUri)
        val fileName =
            "${file!!.nameWithoutExtension}_${System.currentTimeMillis()}.${file.extension}"
        val newFile = File(file.parent, fileName)
        file.renameTo(newFile)
        return MultipartBody.Part.createFormData(
            "avatar", newFile.name, newFile.asRequestBody("image/*".toMediaTypeOrNull())
        )
    }

    /** dialog **/
    private fun initDeleteDialog() {
        deleteDialog = BaseCustomDialog(requireActivity(), R.layout.dialog_delete_logout) {
            when (it?.id) {
                R.id.tvCancel -> {
                    deleteDialog?.dismiss()
                }

                R.id.tvLogout -> {
                    viewModel.deleteApi(Constants.USER_DELETE_USER)
                    deleteDialog?.dismiss()
                }
            }

        }
        deleteDialog?.setCancelable(false)
        deleteDialog?.create()
        deleteDialog?.show()

        deleteDialog?.binding?.apply {
            tvTitle.text = getString(R.string.delete_my_account)
            tvSubHeading.text = getString(R.string.are_you_sure_to_delete_account)
            tvLogout.text = getString(R.string.delete)
        }

    }

    /** handle adapter **/
    private fun initCharacteristicsAdapter() {
        characteristicsAdapter =
            SimpleRecyclerViewAdapter(R.layout.characteristics_rv_item, BR.bean) { v, m, _ ->
                when (v?.id) {
                    R.id.main -> {
                        commonBottomSheet(3)
                    }
                }
            }


        binding.rvCharacteristics.adapter = characteristicsAdapter
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

                R.id.okButton -> {
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

            if (type == 1) {
                binding.tvCommon.text = getString(R.string.gender)
            } else if (type == 2) {
                binding.tvCommon.text = getString(R.string.age)
            } else {
                binding.tvCommon.text = getString(R.string.characteristics)
            }
        }
        initCommonAdapter(type)
        commonBottomSheet?.setOnDismissListener {
            if (type == 3) {
                if (binding.etCharacteristics.text.toString().trim() == "Other") {
                    binding.rvCharacteristics.visibility = View.GONE
                    binding.etCharacteristics.visibility = View.VISIBLE
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
        commonAdapter =
            SimpleRecyclerViewAdapter(R.layout.rv_common_item, BR.bean) { v, m, position ->
                if (v?.id == R.id.main) {
                    when (type) {
                        1 -> {
                            commonAdapter.list.forEach { it.isStatus = it.category == m.category }
                            binding.etGender.setText(m.category)
                            commonAdapter.notifyDataSetChanged()
                        }

                        2 -> {
                            commonAdapter.list.forEach { it.isStatus = it.category == m.category }
                            handleOtherIfNeeded(m, binding.etAge)
                            commonAdapter.notifyDataSetChanged()
                        }

                        3 -> {
                            if (m.category == "Other") {
                                binding.etCharacteristics.setText(m.category)
                                commonBottomSheet?.dismiss()
                                selectedCharacteristics.clear()
                            } else {
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
                                commonAdapter.notifyItemChanged(position)
                            }
                        }
                    }
                }
            }
        // Provide list based on type
        commonAdapter.list = when (type) {
            1 -> DummyList.addGenderSignupList()
            2 -> DummyList.addAgeList()
            else -> DummyList.addCharacteristicsList()
        }.apply {
            when (type) {
                1 -> {
                    val selectedGender = binding.etGender.text.toString().trim()
                    forEach { item ->
                        item.isStatus = selectedGender.isNotEmpty() && item.category.equals(
                            selectedGender,
                            ignoreCase = true
                        )
                    }
                }

                2 -> {
                    val selectedAge = binding.etAge.text.toString().trim()
                    forEach { item ->
                        item.isStatus = selectedAge.isNotEmpty() && item.category.equals(
                            selectedAge,
                            ignoreCase = true
                        )
                    }
                }

                3 -> {
                    forEach { item ->
                        item.isStatus = selectedCharacteristics.contains(item.category)
                    }
                }
            }
        }
        commonBottomSheet?.binding?.rvCommon?.adapter = commonAdapter
    }

    private fun handleOtherIfNeeded(m: CommonModelClass, targetEt: AppCompatEditText) {
        if (m.category == "Other") {
            commonBottomSheet?.dismiss()
            targetEt.setText("")
            targetEt.apply {
                isFocusableInTouchMode = true
                requestFocus()
                postDelayed({
                    val imm =
                        requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showSoftInput(this, InputMethodManager.SHOW_FORCED)
                }, 500)
            }
        } else {
            targetEt.setText(m.category)
        }
    }


}