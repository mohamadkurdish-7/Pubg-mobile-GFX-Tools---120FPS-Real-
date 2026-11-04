package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GameVersion
import com.example.model.ServerPingResult
import com.example.ui.BattleGfxUiState
import com.example.ui.GfxViewModel
import com.example.ui.components.TacticalHeader
import com.example.ui.theme.*

@Composable
fun PingBoosterScreen(
    viewModel: GfxViewModel,
    uiState: BattleGfxUiState,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // RAM Booster HUD
        item {
            TacticalHeader(
                title = "RAM & Gaming Booster",
                subtitle = "Free background memory before launching",
                icon = Icons.Default.RocketLaunch
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("ram_booster_card"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DarkNavyCard),
                border = BorderStroke(1.dp, DarkNavyBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "RAM OPTIMIZER",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimary
                            )
                            uiState.hardwareInfo?.let { hw ->
                                Text(
                                    text = "Available: ${hw.availableRamMb} MB / ${hw.totalRamMb} MB",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextMuted
                                )
                            }
                        }

                        Button(
                            onClick = { viewModel.optimizeRam() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NeonCyan,
                                contentColor = Color(0xFF00363D)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("boost_memory_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("BOOST", fontWeight = FontWeight.Bold)
                        }
                    }

                    AnimatedVisibility(visible = uiState.ramBoostResult != null) {
                        Column {
                            Spacer(modifier = Modifier.height(12.dp))
                            Surface(
                                color = ToxicGreen.copy(alpha = 0.15f),
                                border = BorderStroke(1.dp, ToxicGreen.copy(alpha = 0.4f)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = ToxicGreen,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = uiState.ramBoostResult.orEmpty(),
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                        color = TextPrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Server Latency Ping Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TacticalHeader(
                    title = "Server Latency",
                    subtitle = "Real ping to global game datacenters",
                    icon = Icons.Default.NetworkCheck,
                    modifier = Modifier.weight(1f)
                )

                Button(
                    onClick = { viewModel.runPingTests() },
                    enabled = !uiState.isTestingPing,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyberAmber,
                        contentColor = Color(0xFF332000)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("test_ping_button")
                ) {
                    if (uiState.isTestingPing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(14.dp),
                            color = Color(0xFF332000),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("TEST", fontWeight = FontWeight.Bold)
                }
            }
        }

        items(uiState.serverPings) { ping ->
            ServerPingRow(ping = ping)
        }

        // Game Launch Hub
        item {
            TacticalHeader(
                title = "Game Launcher Hub",
                subtitle = "Direct client launch & detection status",
                icon = Icons.Default.Gamepad
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DarkNavyCard),
                border = BorderStroke(1.dp, DarkNavyBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    GameVersion.values().forEach { ver ->
                        val isTarget = ver == uiState.config.gameVersion
                        val isInstalled = if (isTarget) uiState.isGameInstalled else false

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isTarget) NeonCyan.copy(alpha = 0.08f) else DarkNavySurface)
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.SportsEsports,
                                contentDescription = null,
                                tint = if (isTarget) NeonCyan else TextMuted,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = ver.title,
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = TextPrimary
                                    )
                                    if (isTarget) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            color = NeonCyan.copy(alpha = 0.2f),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = "ACTIVE",
                                                color = NeonCyan,
                                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = ver.description,
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = TextMuted
                                )
                            }

                            Button(
                                onClick = {
                                    if (isTarget) {
                                        val launched = viewModel.launchGame()
                                        if (!launched) {
                                            val marketIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${ver.packageName}"))
                                            marketIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                            try {
                                                context.startActivity(marketIntent)
                                            } catch (e: Exception) {
                                                val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=${ver.packageName}"))
                                                webIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                context.startActivity(webIntent)
                                            }
                                        }
                                    } else {
                                        viewModel.setGameVersion(ver)
                                    }
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isTarget) NeonCyan else DarkNavyBorder,
                                    contentColor = if (isTarget) Color(0xFF00363D) else TextPrimary
                                ),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = if (isTarget) "LAUNCH" else "SELECT",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ServerPingRow(ping: ServerPingResult) {
    val pingColor = when {
        ping.latencyMs < 50 -> ToxicGreen
        ping.latencyMs < 100 -> CyberAmber
        else -> DangerRed
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = DarkNavyCard),
        border = BorderStroke(1.dp, DarkNavyBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(pingColor)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = ping.serverName,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = TextPrimary
                )
                Text(
                    text = "${ping.location} • ${ping.ipOrHost}",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = TextMuted
                )
            }

            Surface(
                color = pingColor.copy(alpha = 0.15f),
                border = BorderStroke(1.dp, pingColor.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = "${ping.latencyMs} ms",
                    color = pingColor,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}
