package com.kids123.presentation

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.kids123.ads.AdManager
import com.kids123.analytics.AnalyticsManager
import com.kids123.domain.model.UserPreferences
import com.kids123.domain.repository.PreferencesRepository
import com.kids123.presentation.navigation.Kids123NavHost
import com.kids123.presentation.navigation.Screen
import com.kids123.presentation.ui.theme.Kids123Theme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject lateinit var adManager: AdManager
    @Inject lateinit var analyticsManager: AnalyticsManager
    @Inject lateinit var preferencesRepository: PreferencesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        adManager.initialize()

        setContent {
            val prefs by preferencesRepository.getUserPreferences()
                .collectAsStateWithLifecycle(initialValue = null)

            if (prefs == null) {
                LoadingShell()
                return@setContent
            }

            Kids123Root(
                prefs = prefs!!,
                adManager = adManager,
                analyticsManager = analyticsManager
            )
        }
    }
}

@Composable
private fun LoadingShell() {
    Kids123Theme {
        Box(Modifier.fillMaxSize())
    }
}

@Composable
private fun Kids123Root(
    prefs: UserPreferences,
    adManager: AdManager,
    analyticsManager: AnalyticsManager
) {
    LaunchedEffect(prefs.analyticsEnabled) {
        analyticsManager.setCollectionEnabled(prefs.analyticsEnabled)
    }

    LaunchedEffect(prefs.adsEnabled, prefs.personalizedAds) {
        adManager.updateAdPolicy(prefs.adsEnabled, prefs.personalizedAds)
    }

    val startDestination = when {
        !prefs.consentGiven -> Screen.Consent.route
        !prefs.onboardingCompleted -> Screen.Onboarding.route
        else -> Screen.Home.route
    }

    Kids123Theme(
        appTheme = prefs.appTheme,
        highContrast = prefs.highContrastMode,
        colorBlindMode = prefs.colorBlindMode,
        fontScale = prefs.fontScale
    ) {
        val navController = rememberNavController()
        Kids123NavHost(
            navController = navController,
            adManager = adManager,
            analyticsManager = analyticsManager,
            preferences = prefs,
            startDestination = startDestination
        )
    }
}
