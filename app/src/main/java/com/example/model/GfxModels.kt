package com.example.model

enum class GameVersion(
    val title: String,
    val packageName: String,
    val description: String
) {
    GLOBAL("Global", "com.tencent.ig", "International PUBG Mobile"),
    BGMI("BGMI", "com.pubg.imobile", "Battlegrounds Mobile India"),
    KOREA_JAPAN("KR / JP", "com.pubg.krmobile", "Korean & Japanese Edition"),
    VIETNAM("VN", "com.vng.pubgmobile", "VNG Games Edition"),
    TAIWAN("TW", "com.rekoo.pubgm", "Taiwan Edition"),
    LITE("Lite", "com.tencent.iglite", "PUBG Mobile Lite")
}

enum class ResolutionSetting(
    val title: String,
    val dimensions: String,
    val scaleFactor: Float,
    val description: String
) {
    QHD_540P("540p (qHD)", "960x540", 0.50f, "Max FPS for low-end / Potato phones"),
    HD_720P("720p (HD)", "1280x720", 0.67f, "Optimal balance of clarity and frame rates"),
    FHD_1080P("1080p (FHD)", "1920x1080", 1.00f, "Standard high definition crisp rendering"),
    QHD_1440P("1440p (2K)", "2560x1440", 1.33f, "Ultra sharp resolution for tablets & flagships"),
    NATIVE("Native Display", "Auto", 1.00f, "Matches physical device screen resolution")
}

enum class FrameRateSetting(
    val label: String,
    val fps: Int,
    val description: String,
    val isHighTier: Boolean = false
) {
    LOW("Low", 20, "20 FPS - Minimal battery drain"),
    MEDIUM("Medium", 25, "25 FPS - Standard baseline"),
    HIGH("High", 30, "30 FPS - Classic smooth"),
    ULTRA("Ultra", 40, "40 FPS - Fluid gameplay"),
    EXTREME("Extreme", 60, "60 FPS - Esports standard competitive"),
    FPS_90("90 FPS", 90, "90 FPS - High refresh rate smooth", true),
    FPS_120("120 FPS", 120, "120 FPS - Maximum competitive fluidity", true)
}

enum class GraphicsQuality(
    val title: String,
    val level: Int,
    val description: String
) {
    SO_SMOOTH("So Smooth", 0, "No visual lag, lowest polygon and texture stress"),
    SMOOTH("Smooth", 1, "Optimal competitive standard"),
    BALANCED("Balanced", 2, "Good shadows with stable performance"),
    HD("HD", 3, "High definition dynamic lighting and textures"),
    HDR("HDR", 4, "High dynamic range with realistic reflections"),
    ULTRA_HD("Ultra HD", 5, "Maximum graphical realism and effects")
}

enum class TextureQuality(
    val title: String,
    val value: Int,
    val description: String
) {
    LOW("Low", 0, "Lightest memory load, fastest level loading"),
    MEDIUM("Medium", 1, "Recommended for 3GB - 4GB RAM devices"),
    HIGH("High", 2, "Detailed weapons, scopes, and player outfits"),
    ULTRA("Ultra", 3, "Full 4K uncompressed texture map assets")
}

enum class ShadowQuality(
    val title: String,
    val value: Int,
    val description: String
) {
    DISABLED("Disable", 0, "Massive FPS boost and easiest enemy spotting"),
    LOW("Low", 1, "Basic entity shadows without terrain cast"),
    MEDIUM("Medium", 2, "Standard distance shadows"),
    HIGH("High", 3, "Soft dynamic shadows for foliage and buildings"),
    EXTREME("Extreme", 4, "Ultra high resolution long-distance shadows")
}

enum class AntiAliasingSetting(
    val title: String,
    val value: Int,
    val description: String
) {
    DISABLED("Disabled", 0, "Sharper pixels, highest FPS"),
    MSAA_2X("2x MSAA", 2, "Subtle smoothing on edges with low overhead"),
    MSAA_4X("4x MSAA", 4, "Silky smooth edges without jagged lines")
}

enum class ColorStyle(
    val title: String,
    val styleCode: Int,
    val description: String
) {
    CLASSIC("Classic", 1, "Original neutral battle-royale color palette"),
    COLORFUL("Colorful", 2, "Vibrant saturation, highest enemy visibility"),
    REALISTIC("Realistic", 3, "Natural lighting with photorealistic shadows"),
    SOFT("Soft", 4, "Warm color temperature, reduced eye fatigue"),
    MOVIE("Movie", 5, "Cinematic contrast curves and deep blacks")
}

enum class RenderingApi(
    val title: String,
    val apiCode: Int,
    val description: String
) {
    DEFAULT("Default", 0, "Automatically chosen by the game"),
    OPENGL("OpenGL 3.1", 1, "Stable legacy graphics pipeline"),
    VULKAN("Vulkan API", 2, "Modern low-overhead GPU pipeline for Android 10+")
}

enum class SoundQuality(
    val title: String,
    val soundCode: Int,
    val description: String
) {
    LOW("Low", 1, "Reduced memory footprint, basic audio"),
    HIGH("High", 2, "Crisp stereo positioning for footsteps and gunshots"),
    ULTRA("Ultra 3D", 3, "High-fidelity spatial audio and acoustic reverberation")
}

data class GfxConfigState(
    val gameVersion: GameVersion = GameVersion.GLOBAL,
    val resolution: ResolutionSetting = ResolutionSetting.HD_720P,
    val frameRate: FrameRateSetting = FrameRateSetting.EXTREME,
    val graphicsQuality: GraphicsQuality = GraphicsQuality.SMOOTH,
    val textureQuality: TextureQuality = TextureQuality.MEDIUM,
    val shadowQuality: ShadowQuality = ShadowQuality.DISABLED,
    val antiAliasing: AntiAliasingSetting = AntiAliasingSetting.DISABLED,
    val colorStyle: ColorStyle = ColorStyle.COLORFUL,
    val renderingApi: RenderingApi = RenderingApi.VULKAN,
    val soundQuality: SoundQuality = SoundQuality.HIGH,
    val gpuOptimization: Boolean = true,
    val potatoGraphics: Boolean = false,
    val saveControls: Boolean = true,
    val lightEffects: Boolean = false,
    val detailedRendering: Boolean = true
)

data class QuickPreset(
    val id: String,
    val title: String,
    val subtitle: String,
    val iconName: String,
    val badge: String,
    val config: GfxConfigState
)

data class DeviceHardwareInfo(
    val deviceModel: String,
    val manufacturer: String,
    val board: String,
    val cpuCores: Int,
    val totalRamMb: Long,
    val availableRamMb: Long,
    val ramUsedPercent: Int,
    val maxRefreshRateHz: Int,
    val androidVersion: String,
    val batteryPct: Int,
    val batteryTempC: Float,
    val isCharging: Boolean
)

data class ServerPingResult(
    val serverName: String,
    val location: String,
    val ipOrHost: String,
    val latencyMs: Int,
    val isTesting: Boolean = false
)
