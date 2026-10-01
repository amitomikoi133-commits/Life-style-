package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen
import com.example.util.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    currentScreen: AppScreen,
    onMenuClick: () -> Unit,
    onSettingsClick: () -> Unit,
    lang: String = "both"
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = if (lang == "bn") currentScreen.bnTitle else currentScreen.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = DateUtils.formatDisplayDate(DateUtils.getTodayString()),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu Navigation",
                    tint = GoldAccent
                )
            }
        },
        actions = {
            IconButton(onClick = onSettingsClick) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = "Settings",
                    tint = TextSecondary
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = SurfaceDark,
            titleContentColor = TextPrimary
        )
    )
}

data class NavDrawerItem(
    val screen: AppScreen,
    val icon: ImageVector,
    val enLabel: String,
    val bnLabel: String
)

@Composable
fun AppDrawerContent(
    currentScreen: AppScreen,
    onScreenSelected: (AppScreen) -> Unit,
    traderName: String,
    lang: String
) {
    val navItems = listOf(
        NavDrawerItem(AppScreen.DASHBOARD, Icons.Default.Dashboard, "Dashboard", "ড্যাশবোর্ড"),
        NavDrawerItem(AppScreen.JOURNAL, Icons.Default.EditCalendar, "Trading Journal", "ট্রেডিং জার্নাল"),
        NavDrawerItem(AppScreen.RISK_MANAGER, Icons.Default.Shield, "Risk Manager", "রিস্ক ম্যানেজার"),
        NavDrawerItem(AppScreen.PSYCHOLOGY, Icons.Default.Psychology, "Psychology & Emotions", "সাইকোলজি ও আবেগ"),
        NavDrawerItem(AppScreen.ROUTINE, Icons.Default.CheckCircleOutline, "Daily Routine", "দৈনিক রুটিন"),
        NavDrawerItem(AppScreen.SALAH, Icons.Default.Mosque, "Salah Tracker", "সালাত ট্র্যাকার"),
        NavDrawerItem(AppScreen.GOALS, Icons.Default.Flag, "Monthly Goals", "মাসিক লক্ষ্যসমূহ"),
        NavDrawerItem(AppScreen.ANALYTICS, Icons.Default.Insights, "Analytics & Reports", "অ্যানালিটিক্স"),
        NavDrawerItem(AppScreen.DAILY_REVIEW, Icons.Default.RateReview, "Daily Review", "দৈনিক পর্যালোচনা"),
        NavDrawerItem(AppScreen.SETTINGS, Icons.Default.Settings, "Settings", "সেটিংস")
    )

    ModalDrawerSheet(
        drawerContainerColor = BackgroundDark,
        drawerContentColor = TextPrimary,
        modifier = Modifier.width(300.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 16.dp)
        ) {
            // Profile & Brand Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, GoldAccent, CircleShape)
                        .background(SurfaceCardElevated),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CandlestickChart,
                        contentDescription = "Trader Life Logo",
                        tint = GoldAccent,
                        modifier = Modifier.size(26.dp)
                    )
                }
                Column {
                    Text(
                        text = "MY TRADER LIFE",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = GoldAccent,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = traderName,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                color = BorderSubtle
            )

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            ) {
                items(navItems) { item ->
                    val isSelected = currentScreen == item.screen
                    val label = when (lang) {
                        "bn" -> item.bnLabel
                        "en" -> item.enLabel
                        else -> "${item.enLabel} (${item.bnLabel})"
                    }

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onScreenSelected(item.screen) },
                        color = if (isSelected) SurfaceCardHighlight else Color.Transparent,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.enLabel,
                                tint = if (isSelected) GoldAccent else TextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) GoldAccentLight else TextPrimary,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            // Bottom quote / discipline motto
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceCardDark)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Text(
                    text = "“Discipline is choosing between what you want now, and what you want most.”",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 14.sp
                )
            }
        }
    }
}
