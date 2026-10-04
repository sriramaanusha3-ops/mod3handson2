package com.example.mod3handson2.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.mod3handson2.R
import com.example.mod3handson2.databinding.ActivityMainBinding
import com.example.mod3handson2.ui.explore.ExploreFragment
import com.example.mod3handson2.ui.history.HistoryFragment
import com.example.mod3handson2.ui.search.SearchFragment
import com.example.mod3handson2.ui.settings.SettingsFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val searchFragment = SearchFragment()
    private val exploreFragment = ExploreFragment()
    private val historyFragment = HistoryFragment()
    private val settingsFragment = SettingsFragment()

    private var activeFragment: Fragment = searchFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupFragments()
        setupBottomNavigation()
    }

    private fun setupFragments() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, searchFragment)
            .commit()
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_search -> {
                    switchFragment(searchFragment)
                    true
                }
                R.id.navigation_explore -> {
                    switchFragment(exploreFragment)
                    true
                }
                R.id.navigation_history -> {
                    switchFragment(historyFragment)
                    true
                }
                R.id.navigation_settings -> {
                    switchFragment(settingsFragment)
                    true
                }
                else -> false
            }
        }
    }

    private fun switchFragment(target: Fragment) {
        if (activeFragment != target) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, target)
                .commit()
            activeFragment = target
        }
    }

    fun navigateToSearchAndQuery(query: String) {
        binding.bottomNavigation.selectedItemId = R.id.navigation_search
        switchFragment(searchFragment)
        searchFragment.executeSearchQuery(query)
    }
}
