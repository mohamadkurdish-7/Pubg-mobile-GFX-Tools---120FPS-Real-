package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.BattleGfxUiState
import com.example.ui.GfxViewModel
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun GfxConfigScreen(
    viewModel: GfxViewModel,
    uiState: BattleGfxUiState,
    onNavigateToRaw: () -> Unit,
    modifier: Modifier = Modifier
) {
    val config = uiState.config

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Game Version Selector
            item {
                TacticalHeader(
                    title = "Target Game Version",
                    subtitle = "Select your installed game client",
                    icon = Icons.Default.SportsEsports,
                    badgeText = if (uiState.isGameInstalled) "INSTALLED" else "NOT DETECTED"
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    GameVersion.values().forEach { ver ->
                        val isSelected = ver == config.gameVersion
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { viewModel.setGameVersion(ver) }
                                .testTag("game_version_${ver.name.lowercase()}"),
                            color = if (isSelected) NeonCyan.copy(alpha = 0.2f) else DarkNavyCard,
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) NeonCyan else DarkNavyBorder
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = ver.title,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (isSelected) NeonCyan else TextPrimary
                                )
                                Text(
                                    text = ver.name,
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = TextMuted
                                )
                            }
                        }
                    }
                }
            }

            // Notice / Success Banner if applied
            item {
                AnimatedVisibility(visible = uiState.applySuccessMessage != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = ToxicGreen.copy(alpha = 0.15f)),
                        border = BorderStroke(1.dp, ToxicGreen.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = ToxicGreen,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = uiState.applySuccessMessage.orEmpty(),
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                color = TextPrimary,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = { viewModel.clearFeedback() },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Dismiss",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Core Graphics Header
            item {
                TacticalHeader(
                    title = "Display & Framerate",
                    subtitle = "Adjust pixel density and refresh rate cap",
                    icon = Icons.Default.Tv
                )
            }

            // Resolution Customizer (Explicitly requested)
            item {
                GfxSelectorCard(
                    title = "Resolution",
                    currentValueLabel = "${config.resolution.title} (${config.resolution.dimensions})",
                    currentDescription = config.resolution.description,
                    icon = Icons.Default.AspectRatio,
                    options = ResolutionSetting.values().toList(),
                    selectedOption = config.resolution,
                    onOptionSelected = { viewModel.setResolution(it) },
                    getOptionLabel = { it.title },
                    isHighlighted = true,
                    testTag = "resolution_selector"
                )
            }

            // Frame Rates Customizer (Explicitly requested)
            item {
                GfxSelectorCard(
                    title = "Frame Rate (FPS)",
                    currentValueLabel = "${config.frameRate.label} (${config.frameRate.fps} FPS)",
                    currentDescription = config.frameRate.description,
                    icon = Icons.Default.Speed,
                    options = FrameRateSetting.values().toList(),
                    selectedOption = config.frameRate,
                    onOptionSelected = { viewModel.setFrameRate(it) },
                    getOptionLabel = { if (it.isHighTier) "⚡ ${it.label}" else it.label },
                    isHighlighted = true,
                    testTag = "fps_selector"
                )
            }

            // Graphics Quality
            item {
                GfxSelectorCard(
                    title = "Graphics Quality",
                    currentValueLabel = config.graphicsQuality.title,
                    currentDescription = config.graphicsQuality.description,
                    icon = Icons.Default.Palette,
                    options = GraphicsQuality.values().toList(),
                    selectedOption = config.graphicsQuality,
                    onOptionSelected = { viewModel.setGraphicsQuality(it) },
                    getOptionLabel = { it.title },
                    testTag = "graphics_quality_selector"
                )
            }

            // Texture & Detail Header
            item {
                TacticalHeader(
                    title = "Textures & Rendering",
                    subtitle = "Configure asset sharpness and lighting",
                    icon = Icons.Default.Layers
                )
            }

            // Texture Quality (Explicitly requested)
            item {
                GfxSelectorCard(
                    title = "Texture Quality",
                    currentValueLabel = config.textureQuality.title,
                    currentDescription = config.textureQuality.description,
                    icon = Icons.Default.Texture,
                    options = TextureQuality.values().toList(),
                    selectedOption = config.textureQuality,
                    onOptionSelected = { viewModel.setTextureQuality(it) },
                    getOptionLabel = { it.title },
                    isHighlighted = true,
                    testTag = "texture_quality_selector"
                )
            }

            // Shadows
            item {
                GfxSelectorCard(
                    title = "Shadow Quality",
                    currentValueLabel = config.shadowQuality.title,
                    currentDescription = config.shadowQuality.description,
                    icon = Icons.Default.WbTwilight,
                    options = ShadowQuality.values().toList(),
                    selectedOption = config.shadowQuality,
                    onOptionSelected = { viewModel.setShadowQuality(it) },
                    getOptionLabel = { it.title },
                    testTag = "shadow_selector"
                )
            }

            // Anti-Aliasing (MSAA)
            item {
                GfxSelectorCard(
                    title = "Anti-Aliasing (MSAA)",
                    currentValueLabel = config.antiAliasing.title,
                    currentDescription = config.antiAliasing.description,
                    icon = Icons.Default.FilterVintage,
                    options = AntiAliasingSetting.values().toList(),
                    selectedOption = config.antiAliasing,
                    onOptionSelected = { viewModel.setAntiAliasing(it) },
                    getOptionLabel = { it.title },
                    testTag = "anti_aliasing_selector"
                )
            }

            // Style & Audio
            item {
                GfxSelectorCard(
                    title = "Color Style",
                    currentValueLabel = config.colorStyle.title,
                    currentDescription = config.colorStyle.description,
                    icon = Icons.Default.ColorLens,
                    options = ColorStyle.values().toList(),
                    selectedOption = config.colorStyle,
                    onOptionSelected = { viewModel.setColorStyle(it) },
                    getOptionLabel = { it.title },
                    testTag = "color_style_selector"
                )
            }

            // Rendering API
            item {
                GfxSelectorCard(
                    title = "Graphics API",
                    currentValueLabel = config.renderingApi.title,
                    currentDescription = config.renderingApi.description,
                    icon = Icons.Default.Memory,
                    options = RenderingApi.values().toList(),
                    selectedOption = config.renderingApi,
                    onOptionSelected = { viewModel.setRenderingApi(it) },
                    getOptionLabel = { it.title },
                    testTag = "rendering_api_selector"
                )
            }

            // Sound Quality
            item {
                GfxSelectorCard(
                    title = "Sound Quality",
                    currentValueLabel = config.soundQuality.title,
                    currentDescription = config.soundQuality.description,
                    icon = Icons.Default.VolumeUp,
                    options = SoundQuality.values().toList(),
                    selectedOption = config.soundQuality,
                    onOptionSelected = { viewModel.setSoundQuality(it) },
                    getOptionLabel = { it.title },
                    testTag = "sound_quality_selector"
                )
            }

            // Performance Boosters Header
            item {
                TacticalHeader(
                    title = "Performance Tweaks",
                    subtitle = "System & thermal optimization flags",
                    icon = Icons.Default.Bolt
                )
            }

            // Potato Graphics Mode Toggle
            item {
                GfxToggleCard(
                    title = "Potato Graphics Mode",
                    description = "Drastically reduces texture overhead & foliage for 2GB/3GB RAM devices",
                    icon = Icons.Default.EnergySavingsLeaf,
                    checked = config.potatoGraphics,
                    onCheckedChange = { viewModel.togglePotatoGraphics() },
                    accentColor = ToxicGreen,
                    badgeText = "MAX FPS",
                    testTag = "potato_graphics_toggle"
                )
            }

            // GPU Optimization Toggle
            item {
                GfxToggleCard(
                    title = "GPU & Shader Optimization",
                    description = "Pre-caches pipeline shaders to reduce stuttering and thermal throttling",
                    icon = Icons.Default.DeveloperBoard,
                    checked = config.gpuOptimization,
                    onCheckedChange = { viewModel.toggleGpuOptimization() },
                    accentColor = NeonCyan,
                    badgeText = "RECOMMENDED",
                    testTag = "gpu_opt_toggle"
                )
            }

            // Save Controls Toggle
            item {
                GfxToggleCard(
                    title = "Save Sensitivity & Controls",
                    description = "Locks and preserves your custom control layout from getting reset",
                    icon = Icons.Default.Lock,
                    checked = config.saveControls,
                    onCheckedChange = { viewModel.toggleSaveControls() },
                    accentColor = CyberAmber,
                    testTag = "save_controls_toggle"
                )
            }

            // Light Effects Toggle
            item {
                GfxToggleCard(
                    title = "Dynamic Light Effects",
                    description = "Enables sun flares, volumetric shafts, and flash illumination",
                    icon = Icons.Default.WbSunny,
                    checked = config.lightEffects,
                    onCheckedChange = { viewModel.toggleLightEffects() },
                    accentColor = NeonCyan,
                    testTag = "light_effects_toggle"
                )
            }
        }

        // Floating Bottom Action Bar
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding(),
            color = DarkNavySurface.copy(alpha = 0.95f),
            border = BorderStroke(1.dp, DarkNavyBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // View raw config button
                OutlinedButton(
                    onClick = onNavigateToRaw,
                    modifier = Modifier
                        .height(48.dp)
                        .testTag("view_ini_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = NeonCyan
                    ),
                    border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.6f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Code,
                        contentDescription = "View Config Code",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Code", fontWeight = FontWeight.SemiBold)
                }

                // Apply button
                Button(
                    onClick = { viewModel.applyActiveSettings() },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("apply_settings_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonCyan,
                        contentColor = Color(0xFF00363D)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "APPLY SETTINGS",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold)
                    )
                }

                // Quick Launch button
                IconButton(
                    onClick = { viewModel.launchGame() },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(CyberAmber.copy(alpha = 0.15f))
                        .border(1.dp, CyberAmber.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                        .testTag("launch_game_quick_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Launch Game",
                        tint = CyberAmber
                    )
                }
            }
        }
    }
}
