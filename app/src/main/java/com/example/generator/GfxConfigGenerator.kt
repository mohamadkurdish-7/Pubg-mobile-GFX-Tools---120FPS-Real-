package com.example.generator

import com.example.model.GfxConfigState
import com.example.model.ResolutionSetting

object GfxConfigGenerator {

    /**
     * Generates standard Unreal Engine 4 UserCustom.ini configuration
     * parsed by the mobile battle royale graphics engine.
     */
    fun generateUserCustomIni(config: GfxConfigState): String {
        val sb = StringBuilder()
        sb.append("; ================================================\n")
        sb.append("; BattleGFX Pro Configuration Profile\n")
        sb.append("; Target Version: ${config.gameVersion.title} (${config.gameVersion.packageName})\n")
        sb.append("; Resolution: ${config.resolution.title} (${config.resolution.dimensions})\n")
        sb.append("; Target Framerate: ${config.frameRate.fps} FPS\n")
        sb.append("; Texture Quality: ${config.textureQuality.title}\n")
        sb.append("; ================================================\n\n")

        sb.append("[UserCustom DeviceProfile]\n")
        val scale = when (config.resolution) {
            ResolutionSetting.QHD_540P -> "0.500000"
            ResolutionSetting.HD_720P -> "0.670000"
            ResolutionSetting.FHD_1080P -> "1.000000"
            ResolutionSetting.QHD_1440P -> "1.330000"
            ResolutionSetting.NATIVE -> "1.000000"
        }
        sb.append("+CVars=r.MobileContentScaleFactor=$scale\n")
        sb.append("+CVars=r.PUBGDeviceFPS=${config.frameRate.fps}\n")
        sb.append("+CVars=r.PUBGQualityLevel=${config.graphicsQuality.level}\n")
        sb.append("+CVars=r.ShadowQuality=${config.shadowQuality.value}\n")
        sb.append("+CVars=r.DefaultFeature.AntiAliasing=${config.antiAliasing.value}\n")
        sb.append("+CVars=r.PostProcessAAQuality=${if (config.antiAliasing.value > 0) 2 else 0}\n")
        sb.append("+CVars=r.MaterialQualityLevel=${config.textureQuality.value}\n")
        sb.append("+CVars=r.TextureQuality=${config.textureQuality.value}\n")
        sb.append("+CVars=r.PUBGColorStyle=${config.colorStyle.styleCode}\n")
        sb.append("+CVars=r.PUBGRenderingAPI=${config.renderingApi.apiCode}\n")
        sb.append("+CVars=r.PUBGSoundQuality=${config.soundQuality.soundCode}\n")

        if (config.potatoGraphics) {
            sb.append("+CVars=r.FoliageDensityScale=0.000000\n")
            sb.append("+CVars=r.ViewDistanceScale=0.600000\n")
            sb.append("+CVars=r.DetailMode=0\n")
        } else if (config.detailedRendering) {
            sb.append("+CVars=r.ViewDistanceScale=1.100000\n")
            sb.append("+CVars=r.DetailMode=1\n")
        }

        if (config.lightEffects) {
            sb.append("+CVars=r.BloomQuality=1\n")
            sb.append("+CVars=r.LightShaftQuality=1\n")
        } else {
            sb.append("+CVars=r.BloomQuality=0\n")
            sb.append("+CVars=r.LightShaftQuality=0\n")
        }

        if (config.gpuOptimization) {
            sb.append("+CVars=r.GPUOptimization=1\n")
            sb.append("+CVars=r.VulkanEnablePipelines=1\n")
            sb.append("+CVars=r.ShaderPipelineCache=1\n")
        }

        if (config.saveControls) {
            sb.append("+CVars=r.SaveSensitivityLayout=1\n")
        }

        sb.append("\n[BackUp Config]\n")
        sb.append("bActiveBackup=True\n")
        sb.append("VersionHash=${System.currentTimeMillis().toString(16)}\n")

        return sb.toString()
    }

    /**
     * Generates the directory path where the config file is placed for the selected game version.
     */
    fun getConfigDestinationPath(config: GfxConfigState): String {
        return "/sdcard/Android/data/${config.gameVersion.packageName}/files/UE4Game/ShadowTrackerExtra/ShadowTrackerExtra/Saved/Config/Android/UserCustom.ini"
    }
}
