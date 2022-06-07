package net.sorenon.mcxr.play.compat.create.mixin;

import com.simibubi.create.foundation.gui.UIRenderHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(UIRenderHelper.CustomRenderTarget.class)
public class UIRenderHelper$CustomRenderTargetMixin {
    @Redirect(method = "create", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/foundation/gui/UIRenderHelper$CustomRenderTarget;enableStencil()V"), remap = false)
    private static void disableStencil(UIRenderHelper.CustomRenderTarget instance) {

    }
}
