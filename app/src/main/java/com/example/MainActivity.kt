package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.AppDatabase
import com.example.data.repository.TraderLifeRepository
import com.example.ui.components.AppDrawerContent
import com.example.ui.components.AppTopBar
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.TraderLifeViewModel
import com.example.ui.viewmodel.TraderLifeViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getDatabase(applicationContext)
        val repository = TraderLifeRepository(
            tradeDao = database.tradeDao(),
            salahDao = database.salahDao(),
            habitDao = database.habitDao(),
            dailyReviewDao = database.dailyReviewDao(),
            psychologyDao = database.psychologyDao(),
            monthlyGoalDao = database.monthlyGoalDao(),
            userSettingsDao = database.userSettingsDao()
        )
        val viewModelFactory = TraderLifeViewModelFactory(repository)
        val viewModel = ViewModelProvider(this, viewModelFactory)[TraderLifeViewModel::class.java]

        setContent {
            TraderLifeTheme {
                TraderLifeApp(viewModel)
            }
        }
    }
}

@Composable
fun TraderLifeApp(viewModel: TraderLifeViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val settings by viewModel.userSettings.collectAsStateWithLifecycle()
    val snackbarMsg by viewModel.snackbarMessage.collectAsStateWithLifecycle()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle Snackbar messages
    LaunchedEffect(snackbarMsg) {
        snackbarMsg?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }

    // Handle Back Press to return to Dashboard if on subscreen
    BackHandler(enabled = currentScreen != AppScreen.DASHBOARD || drawerState.isOpen) {
        if (drawerState.isOpen) {
            scope.launch { drawerState.close() }
        } else if (currentScreen != AppScreen.DASHBOARD) {
            viewModel.setScreen(AppScreen.DASHBOARD)
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawerContent(
                currentScreen = currentScreen,
                onScreenSelected = { screen ->
                    viewModel.setScreen(screen)
                    scope.launch { drawerState.close() }
                },
                traderName = settings.traderName,
                lang = settings.language
            )
        }
    ) {
        Scaffold(
            topBar = {
                AppTopBar(
                    currentScreen = currentScreen,
                    onMenuClick = {
                        scope.launch { drawerState.open() }
                    },
                    onSettingsClick = {
                        viewModel.setScreen(AppScreen.SETTINGS)
                    },
                    lang = settings.language
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = SurfaceDark,
                    tonalElevation = 8.dp
                ) {
                    val bottomNavItems = listOf(
                        Triple(AppScreen.DASHBOARD, Icons.Default.Dashboard, "Home"),
                        Triple(AppScreen.JOURNAL, Icons.Default.EditCalendar, "Journal"),
                        Triple(AppScreen.RISK_MANAGER, Icons.Default.Shield, "Risk"),
                        Triple(AppScreen.ROUTINE, Icons.Default.CheckCircleOutline, "Routine"),
                        Triple(AppScreen.SALAH, Icons.Default.Mosque, "Salah")
                    )

                    bottomNavItems.forEach { (screen, icon, label) ->
                        val selected = currentScreen == screen
                        NavigationBarItem(
                            selected = selected,
                            onClick = { viewModel.setScreen(screen) },
                            icon = {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = label,
                                    tint = if (selected) GoldAccent else TextSecondary
                                )
                            },
                            label = {
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    color = if (selected) GoldAccentLight else TextSecondary
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = SurfaceCardHighlight
                            )
                        )
                    }
                }
            },
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
            containerColor = BackgroundDark,
            contentWindowInsets = WindowInsets.safeDrawing
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(BackgroundDark)
            ) {
                when (currentScreen) {
                    AppScreen.DASHBOARD -> DashboardScreen(viewModel = viewModel)
                    AppScreen.JOURNAL -> JournalScreen(viewModel = viewModel)
                    AppScreen.RISK_MANAGER -> RiskManagerScreen(viewModel = viewModel)
                    AppScreen.PSYCHOLOGY -> PsychologyScreen(viewModel = viewModel)
                    AppScreen.ROUTINE -> RoutineScreen(viewModel = viewModel)
                    AppScreen.SALAH -> SalahScreen(viewModel = viewModel)
                    AppScreen.GOALS -> GoalsScreen(viewModel = viewModel)
                    AppScreen.ANALYTICS -> AnalyticsScreen(viewModel = viewModel)
                    AppScreen.DAILY_REVIEW -> DailyReviewScreen(viewModel = viewModel)
                    AppScreen.SETTINGS -> SettingsScreen(viewModel = viewModel)
                }
            }
        }
    }
}
