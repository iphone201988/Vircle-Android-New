package com.tech.vircle.ui.auth.about

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Handler
import android.os.Looper
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
import com.tech.vircle.data.model.UploadAvtarClass
import com.tech.vircle.data.model.UserRegistrationResponse
import com.tech.vircle.databinding.AvatarBottomSheetBinding
import com.tech.vircle.databinding.FragmentUploadPhotoBinding
import com.tech.vircle.databinding.RvAvtarItemBinding
import com.tech.vircle.databinding.VideoImagePickerDialogBoxBinding
import com.tech.vircle.ui.auth.AuthCommonVM
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
import java.io.FileOutputStream
import java.io.IOException
import java.util.Locale


@AndroidEntryPoint
class UploadPhotoFragment : BaseFragment<FragmentUploadPhotoBinding>() {
    private val viewModel: AuthCommonVM by viewModels()
    private var avatarBottomSheet: BaseCustomBottomSheet<AvatarBottomSheetBinding>? = null
    private lateinit var avatarAdapter: SimpleRecyclerViewAdapter<UploadAvtarClass, RvAvtarItemBinding>
    private var imageDialog: BaseCustomDialog<VideoImagePickerDialogBoxBinding>? = null
    private var photoFile2: File? = null
    private var photoURI: Uri? = null
    private var multipartPart: MultipartBody.Part? = null
    private var age: String? = null
    private var gender: String? = null
    private var isPrivacyPolicy: Boolean = false

    override fun getLayoutResource(): Int {
        return R.layout.fragment_upload_photo
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // click
        initOnClick()
        // Receive the bundle
        arguments?.let { bundle ->
            gender = bundle.getString("gender").orEmpty()
            age = bundle.getString("age").orEmpty()
            isPrivacyPolicy = bundle.getBoolean("isPrivacyPolicy", false)
        }
        // adapter
        initObserver()
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
                        "completeRegistration" -> {
                            try {
                                val myDataModel: UserRegistrationResponse? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (myDataModel != null) {
                                    showSuccessToast(it.message.toString())
                                    if (myDataModel.data != null) {
                                        sharedPrefManager.setLoginData(myDataModel.data)
                                        sharedPrefManager.setToken(myDataModel.data.token)
                                    }
                                    BindingUtils.navigateWithSlide(
                                        findNavController(), R.id.fragmentFriends, null
                                    )
                                }

                            } catch (e: Exception) {
                                Log.e("error", "completeRegistration: $e")
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

    /**
     * Initialize onClick
     */
    private fun initOnClick() {
        viewModel.onClick.observe(viewLifecycleOwner) {
            when (it?.id) {
                // back button click
                R.id.backButton -> {
                    findNavController().popBackStack()
                }
                // lets start  button click
                R.id.btnLetStart -> {
                    if (multipartPart != null) {
                        val data = HashMap<String, RequestBody>()
                        if (gender?.isNotEmpty() == true) {
                            data["gender"] = gender!!.lowercase(Locale.US).toRequestBody()
                        }
                        if (age?.isNotEmpty() == true) {
                            data["age"] = age!!.toRequestBody()
                        }
                        if (age?.isNotEmpty() == true) {
                            data["age"] = age!!.toRequestBody()
                        }
                        data["termsAccepted"] = isPrivacyPolicy.toString().toRequestBody()

                        viewModel.completeRegistration(
                            Constants.USER_COMPLETE_REGISTER + "/${sharedPrefManager.getLoginData()?.user?._id}",
                            data,
                            multipartPart
                        )
                    } else {
                        showInfoToast("Please upload photo")
                    }

                }
                // choose from gallery button button click
                R.id.btnChooseFromLibrary -> {
                    avatarBottomSheet()
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

                        Glide.with(requireActivity())
                            .load(imageUri)
                            .centerCrop()
                            .into(binding.ivPerson)
                        //binding.ivPerson.setImageURI(imageUri)
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
                           // binding.ivPerson.setImageURI(imageUri)
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
                    for (i in avatarAdapter.list) {
                        i.check = i.image == m.image
                    }
                    avatarAdapter.notifyDataSetChanged()
                    Handler(Looper.getMainLooper()).postDelayed({
                        val bitmap = BitmapFactory.decodeResource(requireContext().resources, m.image as Int)
                        val imageFile = bitmapToFile(requireContext(), bitmap, "profilePicture")
                        multipartPart = fileToMultipart(imageFile)
                        binding.ivPerson.setImageResource(m.image as Int)
                        avatarBottomSheet?.dismiss()
                    }, 300)

                }
            }
        }
        avatarAdapter.list = DummyList.addAvtarList()
        avatarBottomSheet?.binding?.rvAvatar?.adapter = avatarAdapter
    }

    private fun bitmapToFile(context: Context, bitmap: Bitmap, fileName: String): File {
        val file = File(context.cacheDir, "$fileName.jpg")
        val out = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        out.flush()
        out.close()
        return file
    }


    private fun fileToMultipart(file: File, partName: String = "avatar"): MultipartBody.Part {
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(partName, file.name, requestFile)
    }


}