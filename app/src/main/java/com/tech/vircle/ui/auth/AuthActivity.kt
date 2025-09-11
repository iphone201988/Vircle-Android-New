package com.tech.vircle.ui.auth

import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.tech.vircle.App
import com.tech.vircle.R
import com.tech.vircle.base.BaseActivity
import com.tech.vircle.base.BaseViewModel
import com.tech.vircle.databinding.ActivityAuthBinding
import com.tech.vircle.ui.home.chat.ChatFragment
import com.tech.vircle.ui.home.friends.FriendsFragment
import com.tech.vircle.ui.home.settings.SettingsFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AuthActivity : BaseActivity<ActivityAuthBinding>() {
    private val viewModel: AuthCommonVM by viewModels()
    private var backPressedTime: Long = 0
    private val backPressInterval = 2000
    private val navController: NavController by lazy {
        (supportFragmentManager.findFragmentById(R.id.authNavigationHost) as NavHostFragment).navController
    }

    override fun getLayoutResource(): Int {
        return R.layout.activity_auth
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView() {
        if (sharedPrefManager.isDarkModeEnabled()) {
            (application as App).setDarkMode(true)
        }
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }



        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                navController.graph =
                    navController.navInflater.inflate(R.navigation.auth_navigation).apply {
                        if (sharedPrefManager.getLoginData() != null) {
                            if (sharedPrefManager.getLoginData()?.isOnboard == true) {
                                setStartDestination(R.id.fragmentAbout)
                            } else {
                                setStartDestination(R.id.fragmentFriends)
                            }
                        } else {
                            setStartDestination(R.id.fragmentMySplash)
                        }
                    }
            }
        }
        // add bottom sheet
        initView()
        // add bottom sheet backstack handel
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.fragmentFriends -> {
                    binding.bottomNavigation.visibility = View.VISIBLE
                    binding.bottomNavigation.menu.findItem(R.id.fragmentFriends).isChecked = true
                }

                R.id.chatFragment -> {
                    binding.bottomNavigation.visibility = View.VISIBLE
                    binding.bottomNavigation.menu.findItem(R.id.chatFragment).isChecked = true
                }

                R.id.settingsFragment -> {
                    binding.bottomNavigation.visibility = View.VISIBLE
                    binding.bottomNavigation.menu.findItem(R.id.settingsFragment).isChecked = true
                }

                else -> {
                    binding.bottomNavigation.visibility = View.GONE
                }
            }
        }

    }


    /** handle view **/
    private fun initView() {
        // Disable icon tint
        binding.bottomNavigation.itemIconTintList = null

        binding.bottomNavigation.setOnNavigationItemSelectedListener { item ->
            val currentDestinationId = navController.currentDestination?.id
            when (item.itemId) {
                R.id.fragmentFriends -> {
                    if (currentDestinationId != R.id.fragmentFriends) {
                        navController.popBackStack(R.id.fragmentFriends, true)
                        navController.navigate(R.id.fragmentFriends)
                    }
                    true
                }

                R.id.chatFragment -> {
                    if (currentDestinationId != R.id.chatFragment) {
                        navController.popBackStack(R.id.chatFragment, true)
                        navController.navigate(R.id.chatFragment)
                    }
                    true
                }

                R.id.settingsFragment -> {
                    if (currentDestinationId != R.id.settingsFragment) {
                        navController.popBackStack(R.id.settingsFragment, true)
                        navController.navigate(R.id.settingsFragment)
                    }
                    true
                }

                else -> false
            }
        }
    }


    override fun onBackPressed() {
        when (navController.currentDestination?.id) {
            R.id.fragmentFriends -> {
                if (backPressedTime + backPressInterval > System.currentTimeMillis()) {
                    navController.popBackStack(R.id.fragmentMySplash, true)
                    finishAffinity()
                } else {
                    showToastInfo("Press again to exit")
                    backPressedTime = System.currentTimeMillis()
                }
            }

            else -> {
                if (!navController.popBackStack()) {
                    super.onBackPressed()
                }
            }
        }
    }


}