package com.tech.vircle.ui.splash.welcome

import android.view.View
import androidx.fragment.app.viewModels
import com.tech.vircle.R
import com.tech.vircle.base.BaseFragment
import com.tech.vircle.base.BaseViewModel
import com.tech.vircle.databinding.FragmentWelcomeSecondBinding
import com.tech.vircle.ui.auth.AuthCommonVM
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class WelcomeSecondFragment : BaseFragment<FragmentWelcomeSecondBinding>() {
    private val viewModel: AuthCommonVM by viewModels()
    override fun getLayoutResource(): Int {
        return R.layout.fragment_welcome_second
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {

    }

}