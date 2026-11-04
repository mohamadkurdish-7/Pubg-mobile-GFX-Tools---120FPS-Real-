package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.GfxViewModel
import com.example.ui.screens.DevicePresetScreen
import com.example.ui.screens.GfxConfigScreen
import com.example.ui.screens.PingBoosterScreen
import com.example.ui.screens.RawConfigScreen
import com.example.ui.theme.*

enum class GfxTab(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val tag: String
) {
    CONFIG("GFX", Icons.Filled.Tune, Icons.Outlined.Tune, "tab_config"),
    PRESETS("Presets", Icons.Filled.AutoAwesome, Icons.Outlined.AutoAwesome, "tab_presets"),
    CODE("Code", Icons.Filled.Code, Icons.Outlined.Code, "tab_code"),
    BOOSTER("Booster", Icons.Filled.RocketLaunch, Icons.Outlined.RocketLaunch, "tab_booster")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BattleGfxTheme {
                val viewModel: GfxViewModel = viewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                var currentTab by remember { mutableStateOf(GfxTab.CONFIG) }

                // Handle back button to return to main GFX tab
                BackHandler(enabled = currentTab != GfxTab.CONFIG) {
                    currentTab = GfxTab.CONFIG
                }

                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("main_scaffold"),
                    contentWindowInsets = WindowInsets.safeDrawing,
                    containerColor = BackgroundDark,
                    topBar = {
                        BattleGfxTopBar(
                            gameVersionName = uiState.config.gameVersion.title,
                            fpsLabel = uiState.config.frameRate.label
                        )
                    },
                    bottomBar = {
                        BattleGfxBottomNavigation(
                            currentTab = currentTab,
                            onTabSelected = { currentTab = it }
                        )
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (currentTab) {
                            GfxTab.CONFIG -> GfxConfigScreen(
                                viewModel = viewModel,
                                uiState = uiState,
                                onNavigateToRaw = { currentTab = GfxTab.CODE }
                            )
                            GfxTab.PRESETS -> DevicePresetScreen(
                                viewModel = viewModel,
                                uiState = uiState
                            )
                            GfxTab.CODE -> RawConfigScreen(
                                uiState = uiState
                            )
                            GfxTab.BOOSTER -> PingBoosterScreen(
                                viewModel = viewModel,
                                uiState = uiState
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BattleGfxTopBar(
    gameVersionName: String,
    fpsLabel: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = DarkNavySurface,
        border = BorderStroke(1.dp, DarkNavyBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // High-Tech Crosshair Shield Icon
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(NeonCyan.copy(alpha = 0.15f))
                    .border(1.dp, NeonCyan.copy(alpha = 0.5f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = NeonCyan,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "BATTLE",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        ),
                        color = TextPrimary
                    )
                    Text(
                        text = "GFX",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        ),
                        color = NeonCyan
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        color = ToxicGreen.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "PRO",
                            color = ToxicGreen,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }
                Text(
                    text = "PUBG & BGMI Performance Optimizer",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = TextMuted
                )
            }

            // Current Active Badges
            Surface(
                color = DarkNavyCard,
                border = BorderStroke(1.dp, CyberAmber.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(CyberAmber)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "$gameVersionName • $fpsLabel",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = CyberAmber
                    )
                }
            }
        }
    }
}

@Composable
fun BattleGfxBottomNavigation(
    currentTab: GfxTab,
    onTabSelected: (GfxTab) -> Unit
) {
    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("bottom_navigation_bar"),
        containerColor = DarkNavySurface,
        tonalElevation = 6.dp,
        windowInsets = WindowInsets.navigationBars
    ) {
        GfxTab.values().forEach { tab ->
            val isSelected = currentTab == tab
            NavigationBarItem(
                modifier = Modifier.testTag(tab.tag),
                selected = isSelected,
                onClick = { onTabSelected(tab) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                        contentDescription = tab.title
                    )
                },
                label = {
                    Text(
                        text = tab.title,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF00363D),
                    selectedTextColor = NeonCyan,
                    indicatorColor = NeonCyan,
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextMuted
                )
            )
        }
    }
}
