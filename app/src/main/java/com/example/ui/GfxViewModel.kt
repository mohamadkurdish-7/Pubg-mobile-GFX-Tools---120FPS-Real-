package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.GfxRepository
import com.example.generator.GfxConfigGenerator
import com.example.model.*
import com.example.telemetry.DeviceTelemetryManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BattleGfxUiState(
    val config: GfxConfigState = GfxConfigState(),
    val hardwareInfo: DeviceHardwareInfo? = null,
    val serverPings: List<ServerPingResult> = emptyList(),
    val isTestingPing: Boolean = false,
    val defaultPresets: List<QuickPreset> = emptyList(),
    val userPresets: List<QuickPreset> = emptyList(),
    val isGameInstalled: Boolean = false,
    val lastAppliedTimeMs: Long = 0L,
    val applySuccessMessage: String? = null,
    val ramBoostResult: String? = null,
    val generatedIni: String = ""
)

class GfxViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = GfxRepository(application)
    private val telemetryManager = DeviceTelemetryManager(application)

    private val _uiState = MutableStateFlow(
        BattleGfxUiState(
            config = repository.currentConfig.value,
            defaultPresets = repository.defaultPresets,
            userPresets = repository.savedPresets.value,
            lastAppliedTimeMs = repository.getLastAppliedTimestamp(),
            generatedIni = GfxConfigGenerator.generateUserCustomIni(repository.currentConfig.value)
        )
    )
    val uiState: StateFlow<BattleGfxUiState> = _uiState.asStateFlow()

    init {
        refreshHardwareInfo()
        checkGameInstalled()
        runPingTests()

        viewModelScope.launch {
            repository.savedPresets.collect { list ->
                _uiState.update { it.copy(userPresets = list) }
            }
        }
    }

    fun refreshHardwareInfo() {
        val info = telemetryManager.getDeviceHardwareInfo()
        _uiState.update { it.copy(hardwareInfo = info) }
    }

    fun checkGameInstalled() {
        val installed = telemetryManager.isGameInstalled(_uiState.value.config.gameVersion)
        _uiState.update { it.copy(isGameInstalled = installed) }
    }

    private fun updateConfigInternal(updater: (GfxConfigState) -> GfxConfigState) {
        val current = _uiState.value.config
        val updated = updater(current)
        repository.updateConfig(updated)
        val ini = GfxConfigGenerator.generateUserCustomIni(updated)
        _uiState.update {
            it.copy(
                config = updated,
                generatedIni = ini
            )
        }
        checkGameInstalled()
    }

    fun setGameVersion(version: GameVersion) {
        updateConfigInternal { it.copy(gameVersion = version) }
    }

    fun setResolution(res: ResolutionSetting) {
        updateConfigInternal { it.copy(resolution = res) }
    }

    fun setFrameRate(fps: FrameRateSetting) {
        updateConfigInternal { it.copy(frameRate = fps) }
    }

    fun setGraphicsQuality(gfx: GraphicsQuality) {
        updateConfigInternal { it.copy(graphicsQuality = gfx) }
    }

    fun setTextureQuality(tex: TextureQuality) {
        updateConfigInternal { it.copy(textureQuality = tex) }
    }

    fun setShadowQuality(shadow: ShadowQuality) {
        updateConfigInternal { it.copy(shadowQuality = shadow) }
    }

    fun setAntiAliasing(aa: AntiAliasingSetting) {
        updateConfigInternal { it.copy(antiAliasing = aa) }
    }

    fun setColorStyle(style: ColorStyle) {
        updateConfigInternal { it.copy(colorStyle = style) }
    }

    fun setRenderingApi(api: RenderingApi) {
        updateConfigInternal { it.copy(renderingApi = api) }
    }

    fun setSoundQuality(sound: SoundQuality) {
        updateConfigInternal { it.copy(soundQuality = sound) }
    }

    fun toggleGpuOptimization() {
        updateConfigInternal { it.copy(gpuOptimization = !it.gpuOptimization) }
    }

    fun togglePotatoGraphics() {
        updateConfigInternal {
            val next = !it.potatoGraphics
            if (next) {
                it.copy(
                    potatoGraphics = true,
                    resolution = ResolutionSetting.QHD_540P,
                    graphicsQuality = GraphicsQuality.SO_SMOOTH,
                    textureQuality = TextureQuality.LOW,
                    shadowQuality = ShadowQuality.DISABLED,
                    antiAliasing = AntiAliasingSetting.DISABLED
                )
            } else {
                it.copy(potatoGraphics = false)
            }
        }
    }

    fun toggleSaveControls() {
        updateConfigInternal { it.copy(saveControls = !it.saveControls) }
    }

    fun toggleLightEffects() {
        updateConfigInternal { it.copy(lightEffects = !it.lightEffects) }
    }

    fun toggleDetailedRendering() {
        updateConfigInternal { it.copy(detailedRendering = !it.detailedRendering) }
    }

    fun applyPreset(preset: QuickPreset) {
        repository.applyPreset(preset)
        val updated = preset.config.copy(gameVersion = _uiState.value.config.gameVersion)
        val ini = GfxConfigGenerator.generateUserCustomIni(updated)
        _uiState.update {
            it.copy(
                config = updated,
                generatedIni = ini,
                applySuccessMessage = "Preset '${preset.title}' applied to active config!"
            )
        }
    }

    fun saveCurrentAsPreset(name: String) {
        if (name.isBlank()) return
        repository.saveUserPreset(name.trim())
    }

    fun deleteUserPreset(id: String) {
        repository.deleteUserPreset(id)
    }

    fun applyActiveSettings() {
        repository.markSettingsApplied()
        val timestamp = System.currentTimeMillis()
        val path = GfxConfigGenerator.getConfigDestinationPath(_uiState.value.config)
        _uiState.update {
            it.copy(
                lastAppliedTimeMs = timestamp,
                applySuccessMessage = "Configuration successfully generated for ${_uiState.value.config.gameVersion.title} (${_uiState.value.config.resolution.title}, ${_uiState.value.config.frameRate.label})!"
            )
        }
    }

    fun clearFeedback() {
        _uiState.update { it.copy(applySuccessMessage = null, ramBoostResult = null) }
    }

    fun runPingTests() {
        viewModelScope.launch {
            _uiState.update { it.copy(isTestingPing = true) }
            val pings = telemetryManager.testServerPings()
            _uiState.update { it.copy(serverPings = pings, isTestingPing = false) }
        }
    }

    fun optimizeRam() {
        val (availableMb, freedMb) = telemetryManager.optimizeRam()
        refreshHardwareInfo()
        _uiState.update {
            it.copy(ramBoostResult = "Freed ~${freedMb}MB of memory! Available RAM: ${availableMb}MB")
        }
    }

    fun launchGame(): Boolean {
        return telemetryManager.launchGame(_uiState.value.config.gameVersion)
    }
}
