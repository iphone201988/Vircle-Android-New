package com.tech.vircle.ui.splash

import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.tech.vircle.R
import com.tech.vircle.base.BaseFragment
import com.tech.vircle.base.BaseViewModel
import com.tech.vircle.databinding.FragmentMySplashBinding
import com.tech.vircle.ui.auth.AuthCommonVM
import com.tech.vircle.utils.BindingUtils
import com.zhpan.indicator.enums.IndicatorSlideMode
import com.zhpan.indicator.enums.IndicatorStyle
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MySplashFragment : BaseFragment<FragmentMySplashBinding>() {
    private val viewModel: AuthCommonVM by viewModels()
    override fun getLayoutResource(): Int {
        return R.layout.fragment_my_splash
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // Set up ViewPager adapter
        val adapter = WelcomePagerAdapter(requireActivity())
        binding.viewPager.adapter = adapter
        binding.viewPager.isUserInputEnabled = true

        // view pager
        setupViewPager()
        // click
        initOnClick()
    }

    /**
     * Initialize onClick
     */
    private fun initOnClick() {
        viewModel.onClick.observe(viewLifecycleOwner) {
            when (it?.id) {
                R.id.btnNext -> {
                    val currentItem = binding.viewPager.currentItem
                    if (currentItem == 1) {
                        findNavController().popBackStack()
                        BindingUtils.navigateWithSlide(
                            findNavController(), R.id.navigateToLoginFragment, null
                        )

                    } else {
                        val nextItem = currentItem + 1
                        if (nextItem < binding.viewPager.adapter!!.itemCount) {
                            binding.viewPager.setCurrentItem(nextItem, true)
                        }
                    }
                }
            }
        }
    }


    /***
     * setup view pager
     ***/
    private fun setupViewPager() {
        binding.dotsIndicator.apply {
            setSliderColor(
                ContextCompat.getColor(requireContext(), R.color.color_ECECEC),
                ContextCompat.getColor(requireContext(), R.color.color_525252)
            )
            setSliderWidth(
                resources.getDimension(com.intuit.sdp.R.dimen._11sdp),
                resources.getDimension(com.intuit.sdp.R.dimen._11sdp)
            )

            setSliderHeight(
                resources.getDimension(com.intuit.sdp.R.dimen._8sdp)
            )
            setSlideMode(IndicatorSlideMode.SMOOTH)
            setIndicatorStyle(IndicatorStyle.CIRCLE)
            setPageSize(binding.viewPager.adapter!!.itemCount)
            notifyDataChanged()

            // Bind dots indicator with ViewPager2
            binding.viewPager.let {
                binding.dotsIndicator.setupWithViewPager(it)
            }
        }


        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == 1) {
                    binding.tvSubWelcome.text =
                        getString(R.string.create_your_own_ai_friends_Second)

                } else {
                    binding.tvSubWelcome.text = getString(R.string.create_your_own_ai_friends)
                }
            }
        })

    }


}