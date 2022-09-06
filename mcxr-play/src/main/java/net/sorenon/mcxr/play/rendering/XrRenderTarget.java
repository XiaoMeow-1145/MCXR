package net.sorenon.mcxr.play.rendering;

import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.sorenon.mcxr.play.MCXRNativeLoad;
import net.sorenon.mcxr.play.mixin.accessor.RenderTargetAcc;
import org.lwjgl.opengl.GL30;

import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL30.*;

public class XrRenderTarget extends TextureTarget {

    public XrRenderTarget(int width, int height, int color, int index) {
        super(width, height, true, Minecraft.ON_OSX);
        this.setClearColor(sRGBToLinear(239 / 255f), sRGBToLinear(50 / 255f), sRGBToLinear(61 / 255f), 255 / 255f);
    }

    private float sRGBToLinear(float f) {
        if (f < 0.04045f) {
            return f / 12.92f;
        } else {
            return (float) Math.pow((f + 0.055f) / 1.055f, 2.4f);
        }
    }
}
