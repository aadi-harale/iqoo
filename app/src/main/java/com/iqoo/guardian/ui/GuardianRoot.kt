package com.iqoo.guardian.ui

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.iqoo.guardian.data.repository.GuardianRepository
import com.iqoo.guardian.ui.navigation.BottomTab
import com.iqoo.guardian.ui.navigation.GuardianBottomBar
import com.iqoo.guardian.ui.navigation.Routes
import com.iqoo.guardian.ui.screens.about.AboutScreen
import com.iqoo.guardian.ui.screens.alerts.AlertsScreen
import com.iqoo.guardian.ui.screens.demo.AnalysisScreen
import com.iqoo.guardian.ui.screens.demo.AnalysisViewModel
import com.iqoo.guardian.ui.screens.demo.DemoLabScreen
import com.iqoo.guardian.ui.screens.device.DeviceScreen
import com.iqoo.guardian.ui.screens.home.HomeScreen
import com.iqoo.guardian.ui.screens.insight.InsightDetailScreen
import com.iqoo.guardian.ui.screens.privacy.PrivacyScreen
import com.iqoo.guardian.ui.screens.splash.SplashScreen
import com.iqoo.guardian.ui.theme.GBackground

/** Routes that show the bottom bar. Everything else is a full-screen surface. */
private val TAB_ROUTES = BottomTab.entries.map { it.route }.toSet()

@Composable
fun GuardianRoot(repository: GuardianRepository) {
    val navController = rememberNavController()
    val viewModel: GuardianViewModel = viewModel(
        factory = GuardianViewModel.Factory(repository)
    )

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = currentRoute in TAB_ROUTES

    Scaffold(
        containerColor = GBackground,
        bottomBar = {
            if (showBottomBar) {
                GuardianBottomBar(
                    currentRoute = currentRoute,
                    onSelect = { tab -> navController.navigateToTab(tab) }
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(GBackground)
                .padding(bottom = if (showBottomBar) padding.calculateBottomPadding() else 0.dp)
        ) {
            GuardianNavHost(
                navController = navController,
                viewModel = viewModel,
                repository = repository
            )
        }
    }
}

/**
 * Tab switching keeps a single instance of each tab and returns to Home rather
 * than exiting when back is pressed from another tab.
 */
private fun NavHostController.navigateToTab(tab: BottomTab) {
    navigate(tab.route) {
        popUpTo(Routes.HOME) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
private fun GuardianNavHost(
    navController: NavHostController,
    viewModel: GuardianViewModel,
    repository: GuardianRepository
) {
    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH,
        enterTransition = { fadeIn(tween(260)) },
        exitTransition = { fadeOut(tween(180)) },
        popEnterTransition = { fadeIn(tween(240)) },
        popExitTransition = { fadeOut(tween(180)) }
    ) {
        composable(Routes.SPLASH) {
            SplashScreen(
                onFinished = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Routes.HOME,
            deepLinks = listOf(androidx.navigation.navDeepLink { uriPattern = "iqoo://guardian/home" })
        ) {
            val coroutineScope = androidx.compose.runtime.rememberCoroutineScope()
            HomeScreen(
                viewModel = viewModel,
                onOpenHub = { navController.navigateToTab(BottomTab.DEVICE) },
                onOpenAlerts = { navController.navigateToTab(BottomTab.ALERTS) },
                onOpenDemoLab = { navController.navigate(Routes.DEMO_LAB) },
                onOpenInsight = { id -> navController.navigate(Routes.insight(id)) },
                onRunAutopilot = {
                    kotlinx.coroutines.GlobalScope.launch(kotlinx.coroutines.Dispatchers.Main) {
                        // 1. Go to Device Hub
                        navController.navigateToTab(BottomTab.DEVICE)
                        delay(2500)
                        
                        // 2. Deep dive into Battery
                        navController.navigate(Routes.BATTERY)
                        delay(2500)
                        navController.popBackStack()
                        delay(1000)
                        
                        // 3. Deep dive into Thermal
                        navController.navigate(Routes.THERMAL)
                        delay(2500)
                        navController.popBackStack()
                        delay(1000)

                        // 4. Deep dive into Storage
                        navController.navigate(Routes.STORAGE)
                        delay(2500)
                        navController.popBackStack()
                        delay(1000)
                        
                        // 5. Go to Privacy (Offline Proof Mode)
                        navController.navigateToTab(BottomTab.PRIVACY)
                        delay(3000)
                        
                        // 6. Go to Demo Lab and launch threat
                        navController.navigate(Routes.DEMO_LAB)
                        delay(1500)
                        navController.navigate(Routes.analysis("flashdeals_stealth_drain"))
                    }
                }
            )
        }

        composable(
            route = Routes.ALERTS,
            deepLinks = listOf(androidx.navigation.navDeepLink { uriPattern = "iqoo://guardian/alerts" })
        ) {
            AlertsScreen(
                viewModel = viewModel,
                onOpenInsight = { id -> navController.navigate(Routes.insight(id)) }
            )
        }

        composable(
            route = Routes.DEVICE,
            deepLinks = listOf(androidx.navigation.navDeepLink { uriPattern = "iqoo://guardian/device" })
        ) {
            DeviceScreen(
                viewModel = viewModel,
                onOpenInsight = { id -> navController.navigate(Routes.insight(id)) },
                onOpenPerformance = { navController.navigate(Routes.PERFORMANCE) },
                onOpenAppUsage = { navController.navigate(Routes.APP_USAGE) },
                onOpenMemory = { navController.navigate(Routes.MEMORY) },
                onOpenBattery = { navController.navigate(Routes.BATTERY) },
                onOpenThermal = { navController.navigate(Routes.THERMAL) },
                onOpenStorage = { navController.navigate(Routes.STORAGE) },
                onOpenCamera = { navController.navigate(Routes.CAMERA) },
                onOpenNetwork = { navController.navigate(Routes.NETWORK) },
                onOpenSensors = { navController.navigate(Routes.SENSORS) },
                onOpenHealth = { navController.navigate(Routes.HEALTH) }
            )
        }

        composable(Routes.BATTERY, deepLinks = listOf(androidx.navigation.navDeepLink { uriPattern = "iqoo://guardian/battery" })) {
            com.iqoo.guardian.ui.screens.device.BatteryScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.THERMAL, deepLinks = listOf(androidx.navigation.navDeepLink { uriPattern = "iqoo://guardian/thermal" })) {
            com.iqoo.guardian.ui.screens.device.ThermalScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.STORAGE, deepLinks = listOf(androidx.navigation.navDeepLink { uriPattern = "iqoo://guardian/storage" })) {
            com.iqoo.guardian.ui.screens.device.StorageScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.CAMERA, deepLinks = listOf(androidx.navigation.navDeepLink { uriPattern = "iqoo://guardian/camera" })) {
            com.iqoo.guardian.ui.screens.device.CameraScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.NETWORK, deepLinks = listOf(androidx.navigation.navDeepLink { uriPattern = "iqoo://guardian/network" })) {
            com.iqoo.guardian.ui.screens.device.NetworkScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.SENSORS, deepLinks = listOf(androidx.navigation.navDeepLink { uriPattern = "iqoo://guardian/sensors" })) {
            com.iqoo.guardian.ui.screens.device.SensorsScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.HEALTH, deepLinks = listOf(androidx.navigation.navDeepLink { uriPattern = "iqoo://guardian/health" })) {
            com.iqoo.guardian.ui.screens.device.HealthScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.PERFORMANCE, deepLinks = listOf(androidx.navigation.navDeepLink { uriPattern = "iqoo://guardian/performance" })) {
            com.iqoo.guardian.ui.screens.device.PerformanceScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.APP_USAGE, deepLinks = listOf(androidx.navigation.navDeepLink { uriPattern = "iqoo://guardian/appusage" })) {
            com.iqoo.guardian.ui.screens.device.AppUsageScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.MEMORY, deepLinks = listOf(androidx.navigation.navDeepLink { uriPattern = "iqoo://guardian/memory" })) {
            com.iqoo.guardian.ui.screens.device.MemoryScreen(onBack = { navController.popBackStack() })
        }

        composable(
            route = Routes.PRIVACY,
            deepLinks = listOf(androidx.navigation.navDeepLink { uriPattern = "iqoo://guardian/privacy" })
        ) {
            PrivacyScreen(
                viewModel = viewModel,
                onOpenAbout = { navController.navigate(Routes.ABOUT) }
            )
        }

        composable(
            route = Routes.CHAT,
            deepLinks = listOf(androidx.navigation.navDeepLink { uriPattern = "iqoo://guardian/chat" })
        ) {
            com.iqoo.guardian.ui.screens.chat.ChatbotScreen()
        }

        composable(
            route = Routes.DEMO_LAB,
            deepLinks = listOf(androidx.navigation.navDeepLink { uriPattern = "iqoo://guardian/demolab" })
        ) {
            DemoLabScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onRunScenario = { id -> navController.navigate(Routes.analysis(id)) }
            )
        }

        composable(Routes.ABOUT) {
            AboutScreen(onBack = { navController.popBackStack() })
        }

        composable(
            route = Routes.ANALYSIS,
            deepLinks = listOf(androidx.navigation.navDeepLink { uriPattern = "iqoo://guardian/analysis/{scenarioId}" })
        ) { entry ->
            val scenarioId = entry.arguments?.getString(Routes.ANALYSIS_ARG).orEmpty()
            val analysisViewModel: AnalysisViewModel = viewModel(
                factory = AnalysisViewModel.Factory(repository, scenarioId)
            )
            AnalysisScreen(
                viewModel = analysisViewModel,
                onBack = { navController.popBackStack() },
                onFinished = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Routes.INSIGHT,
            deepLinks = listOf(androidx.navigation.navDeepLink { uriPattern = "iqoo://guardian/insight/{eventId}" })
        ) { entry ->
            val eventId = entry.arguments?.getString(Routes.INSIGHT_ARG).orEmpty()
            InsightDetailScreen(
                viewModel = viewModel,
                eventId = eventId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
