package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject

class GfxRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("battle_gfx_prefs", Context.MODE_PRIVATE)

    private val _currentConfig = MutableStateFlow(loadConfig())
    val currentConfig: StateFlow<GfxConfigState> = _currentConfig.asStateFlow()

    private val _savedPresets = MutableStateFlow<List<QuickPreset>>(loadSavedPresets())
    val savedPresets: StateFlow<List<QuickPreset>> = _savedPresets.asStateFlow()

    val defaultPresets: List<QuickPreset> = listOf(
        QuickPreset(
            id = "preset_potato",
            title = "Potato 60 FPS",
            subtitle = "540p | So Smooth | No Shadows | Vulkan",
            iconName = "speed",
            badge = "MAX FPS",
            config = GfxConfigState(
                gameVersion = GameVersion.GLOBAL,
                resolution = ResolutionSetting.QHD_540P,
                frameRate = FrameRateSetting.EXTREME,
                graphicsQuality = GraphicsQuality.SO_SMOOTH,
                textureQuality = TextureQuality.LOW,
                shadowQuality = ShadowQuality.DISABLED,
                antiAliasing = AntiAliasingSetting.DISABLED,
                colorStyle = ColorStyle.COLORFUL,
                renderingApi = RenderingApi.VULKAN,
                soundQuality = SoundQuality.LOW,
                gpuOptimization = true,
                potatoGraphics = true,
                saveControls = true
            )
        ),
        QuickPreset(
            id = "preset_esports_90",
            title = "Esports 90 FPS",
            subtitle = "720p | Smooth | High Refresh Rate | 2x AA",
            iconName = "sports_esports",
            badge = "90 FPS",
            config = GfxConfigState(
                gameVersion = GameVersion.GLOBAL,
                resolution = ResolutionSetting.HD_720P,
                frameRate = FrameRateSetting.FPS_90,
                graphicsQuality = GraphicsQuality.SMOOTH,
                textureQuality = TextureQuality.MEDIUM,
                shadowQuality = ShadowQuality.DISABLED,
                antiAliasing = AntiAliasingSetting.MSAA_2X,
                colorStyle = ColorStyle.COLORFUL,
                renderingApi = RenderingApi.VULKAN,
                soundQuality = SoundQuality.ULTRA,
                gpuOptimization = true,
                potatoGraphics = false,
                saveControls = true
            )
        ),
        QuickPreset(
            id = "preset_flagship_120",
            title = "Extreme 120 FPS",
            subtitle = "1080p | Balanced | 120 Hz Ultra Fluid",
            iconName = "flash_on",
            badge = "120 FPS",
            config = GfxConfigState(
                gameVersion = GameVersion.GLOBAL,
                resolution = ResolutionSetting.FHD_1080P,
                frameRate = FrameRateSetting.FPS_120,
                graphicsQuality = GraphicsQuality.BALANCED,
                textureQuality = TextureQuality.HIGH,
                shadowQuality = ShadowQuality.LOW,
                antiAliasing = AntiAliasingSetting.MSAA_2X,
                colorStyle = ColorStyle.COLORFUL,
                renderingApi = RenderingApi.VULKAN,
                soundQuality = SoundQuality.ULTRA,
                gpuOptimization = true,
                potatoGraphics = false,
                saveControls = true
            )
        ),
        QuickPreset(
            id = "preset_cinematic_hdr",
            title = "Cinematic HDR",
            subtitle = "1440p 2K | HDR | Ultra Textures | 4x MSAA",
            iconName = "hd",
            badge = "GRAPHICS",
            config = GfxConfigState(
                gameVersion = GameVersion.GLOBAL,
                resolution = ResolutionSetting.QHD_1440P,
                frameRate = FrameRateSetting.EXTREME,
                graphicsQuality = GraphicsQuality.HDR,
                textureQuality = TextureQuality.ULTRA,
                shadowQuality = ShadowQuality.HIGH,
                antiAliasing = AntiAliasingSetting.MSAA_4X,
                colorStyle = ColorStyle.REALISTIC,
                renderingApi = RenderingApi.VULKAN,
                soundQuality = SoundQuality.ULTRA,
                gpuOptimization = true,
                potatoGraphics = false,
                saveControls = true
            )
        ),
        QuickPreset(
            id = "preset_balanced_60",
            title = "Balanced Daily",
            subtitle = "720p | Balanced | 60 FPS Steady",
            iconName = "tune",
            badge = "BALANCED",
            config = GfxConfigState(
                gameVersion = GameVersion.GLOBAL,
                resolution = ResolutionSetting.HD_720P,
                frameRate = FrameRateSetting.EXTREME,
                graphicsQuality = GraphicsQuality.BALANCED,
                textureQuality = TextureQuality.MEDIUM,
                shadowQuality = ShadowQuality.LOW,
                antiAliasing = AntiAliasingSetting.DISABLED,
                colorStyle = ColorStyle.CLASSIC,
                renderingApi = RenderingApi.VULKAN,
                soundQuality = SoundQuality.HIGH,
                gpuOptimization = true,
                potatoGraphics = false,
                saveControls = true
            )
        )
    )

    fun updateConfig(config: GfxConfigState) {
        _currentConfig.value = config
        saveConfig(config)
    }

    fun applyPreset(preset: QuickPreset) {
        // Keep user's preferred game version while updating graphics parameters
        val updated = preset.config.copy(gameVersion = _currentConfig.value.gameVersion)
        updateConfig(updated)
    }

    fun saveUserPreset(name: String) {
        val newPreset = QuickPreset(
            id = "user_${System.currentTimeMillis()}",
            title = name,
            subtitle = "${_currentConfig.value.resolution.title} | ${_currentConfig.value.frameRate.label} | ${_currentConfig.value.textureQuality.title}",
            iconName = "bookmark",
            badge = "USER",
            config = _currentConfig.value
        )
        val list = _savedPresets.value.toMutableList().apply { add(0, newPreset) }
        _savedPresets.value = list
        persistSavedPresets(list)
    }

    fun deleteUserPreset(id: String) {
        val list = _savedPresets.value.filterNot { it.id == id }
        _savedPresets.value = list
        persistSavedPresets(list)
    }

    fun markSettingsApplied() {
        prefs.edit().putLong("last_applied_timestamp", System.currentTimeMillis()).apply()
    }

    fun getLastAppliedTimestamp(): Long {
        return prefs.getLong("last_applied_timestamp", 0L)
    }

    private fun saveConfig(config: GfxConfigState) {
        prefs.edit()
            .putString("gameVersion", config.gameVersion.name)
            .putString("resolution", config.resolution.name)
            .putString("frameRate", config.frameRate.name)
            .putString("graphicsQuality", config.graphicsQuality.name)
            .putString("textureQuality", config.textureQuality.name)
            .putString("shadowQuality", config.shadowQuality.name)
            .putString("antiAliasing", config.antiAliasing.name)
            .putString("colorStyle", config.colorStyle.name)
            .putString("renderingApi", config.renderingApi.name)
            .putString("soundQuality", config.soundQuality.name)
            .putBoolean("gpuOptimization", config.gpuOptimization)
            .putBoolean("potatoGraphics", config.potatoGraphics)
            .putBoolean("saveControls", config.saveControls)
            .putBoolean("lightEffects", config.lightEffects)
            .putBoolean("detailedRendering", config.detailedRendering)
            .apply()
    }

    private fun loadConfig(): GfxConfigState {
        val gameVer = safeEnum(prefs.getString("gameVersion", null), GameVersion.GLOBAL)
        val res = safeEnum(prefs.getString("resolution", null), ResolutionSetting.HD_720P)
        val fps = safeEnum(prefs.getString("frameRate", null), FrameRateSetting.EXTREME)
        val gfx = safeEnum(prefs.getString("graphicsQuality", null), GraphicsQuality.SMOOTH)
        val tex = safeEnum(prefs.getString("textureQuality", null), TextureQuality.MEDIUM)
        val shadow = safeEnum(prefs.getString("shadowQuality", null), ShadowQuality.DISABLED)
        val aa = safeEnum(prefs.getString("antiAliasing", null), AntiAliasingSetting.DISABLED)
        val col = safeEnum(prefs.getString("colorStyle", null), ColorStyle.COLORFUL)
        val api = safeEnum(prefs.getString("renderingApi", null), RenderingApi.VULKAN)
        val sound = safeEnum(prefs.getString("soundQuality", null), SoundQuality.HIGH)

        return GfxConfigState(
            gameVersion = gameVer,
            resolution = res,
            frameRate = fps,
            graphicsQuality = gfx,
            textureQuality = tex,
            shadowQuality = shadow,
            antiAliasing = aa,
            colorStyle = col,
            renderingApi = api,
            soundQuality = sound,
            gpuOptimization = prefs.getBoolean("gpuOptimization", true),
            potatoGraphics = prefs.getBoolean("potatoGraphics", false),
            saveControls = prefs.getBoolean("saveControls", true),
            lightEffects = prefs.getBoolean("lightEffects", false),
            detailedRendering = prefs.getBoolean("detailedRendering", true)
        )
    }

    private inline fun <reified T : Enum<T>> safeEnum(name: String?, default: T): T {
        if (name == null) return default
        return try {
            java.lang.Enum.valueOf(T::class.java, name)
        } catch (e: Exception) {
            default
        }
    }

    private fun persistSavedPresets(list: List<QuickPreset>) {
        val array = JSONArray()
        list.forEach { p ->
            val obj = JSONObject().apply {
                put("id", p.id)
                put("title", p.title)
                put("subtitle", p.subtitle)
                put("badge", p.badge)
                val c = p.config
                val cObj = JSONObject().apply {
                    put("gameVersion", c.gameVersion.name)
                    put("resolution", c.resolution.name)
                    put("frameRate", c.frameRate.name)
                    put("graphicsQuality", c.graphicsQuality.name)
                    put("textureQuality", c.textureQuality.name)
                    put("shadowQuality", c.shadowQuality.name)
                    put("antiAliasing", c.antiAliasing.name)
                    put("colorStyle", c.colorStyle.name)
                    put("renderingApi", c.renderingApi.name)
                    put("soundQuality", c.soundQuality.name)
                    put("gpuOptimization", c.gpuOptimization)
                    put("potatoGraphics", c.potatoGraphics)
                }
                put("config", cObj)
            }
            array.put(obj)
        }
        prefs.edit().putString("custom_user_presets", array.toString()).apply()
    }

    private fun loadSavedPresets(): List<QuickPreset> {
        val raw = prefs.getString("custom_user_presets", null) ?: return emptyList()
        val result = mutableListOf<QuickPreset>()
        try {
            val array = JSONArray(raw)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val cObj = obj.getJSONObject("config")
                val config = GfxConfigState(
                    gameVersion = safeEnum(cObj.optString("gameVersion"), GameVersion.GLOBAL),
                    resolution = safeEnum(cObj.optString("resolution"), ResolutionSetting.HD_720P),
                    frameRate = safeEnum(cObj.optString("frameRate"), FrameRateSetting.EXTREME),
                    graphicsQuality = safeEnum(cObj.optString("graphicsQuality"), GraphicsQuality.SMOOTH),
                    textureQuality = safeEnum(cObj.optString("textureQuality"), TextureQuality.MEDIUM),
                    shadowQuality = safeEnum(cObj.optString("shadowQuality"), ShadowQuality.DISABLED),
                    antiAliasing = safeEnum(cObj.optString("antiAliasing"), AntiAliasingSetting.DISABLED),
                    colorStyle = safeEnum(cObj.optString("colorStyle"), ColorStyle.COLORFUL),
                    renderingApi = safeEnum(cObj.optString("renderingApi"), RenderingApi.VULKAN),
                    soundQuality = safeEnum(cObj.optString("soundQuality"), SoundQuality.HIGH),
                    gpuOptimization = cObj.optBoolean("gpuOptimization", true),
                    potatoGraphics = cObj.optBoolean("potatoGraphics", false)
                )
                result.add(
                    QuickPreset(
                        id = obj.getString("id"),
                        title = obj.getString("title"),
                        subtitle = obj.optString("subtitle", ""),
                        iconName = "bookmark",
                        badge = obj.optString("badge", "CUSTOM"),
                        config = config
                    )
                )
            }
        } catch (e: Exception) {
            // Ignore corrupted presets
        }
        return result
    }
}
