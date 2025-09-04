package com.tech.vircle.ui.splash.welcome

import android.view.View
import androidx.fragment.app.viewModels
import com.tech.vircle.R
import com.tech.vircle.base.BaseFragment
import com.tech.vircle.base.BaseViewModel
import com.tech.vircle.databinding.FragmentWelcomeFirstBinding
import com.tech.vircle.ui.auth.AuthCommonVM
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WelcomeFirstFragment : BaseFragment<FragmentWelcomeFirstBinding>() {
    private val viewModel: AuthCommonVM by viewModels()
    override fun getLayoutResource(): Int {
        return R.layout.fragment_welcome_first
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {

    }

}