package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.generator.GfxConfigGenerator
import com.example.model.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("BattleGFX", appName)
    }

    @Test
    fun `generate 90 fps config profile`() {
        val config = GfxConfigState(
            gameVersion = GameVersion.GLOBAL,
            resolution = ResolutionSetting.HD_720P,
            frameRate = FrameRateSetting.FPS_90,
            textureQuality = TextureQuality.HIGH
        )
        val ini = GfxConfigGenerator.generateUserCustomIni(config)

        assertTrue(ini.contains("+CVars=r.PUBGDeviceFPS=90"))
        assertTrue(ini.contains("+CVars=r.MobileContentScaleFactor=0.670000"))
        assertTrue(ini.contains("+CVars=r.TextureQuality=2"))
    }

    @Test
    fun `generate 120 fps flagship config profile`() {
        val config = GfxConfigState(
            gameVersion = GameVersion.BGMI,
            resolution = ResolutionSetting.FHD_1080P,
            frameRate = FrameRateSetting.FPS_120,
            textureQuality = TextureQuality.ULTRA
        )
        val ini = GfxConfigGenerator.generateUserCustomIni(config)

        assertTrue(ini.contains("+CVars=r.PUBGDeviceFPS=120"))
        assertTrue(ini.contains("+CVars=r.MobileContentScaleFactor=1.000000"))
        assertTrue(ini.contains("+CVars=r.TextureQuality=3"))
        assertTrue(ini.contains("Target Version: BGMI"))
    }
}
