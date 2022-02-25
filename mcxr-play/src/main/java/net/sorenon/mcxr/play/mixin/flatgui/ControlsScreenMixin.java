package net.sorenon.mcxr.play.mixin.flatgui;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.controls.ControlsScreen;
import net.minecraft.network.chat.Component;
import net.sorenon.mcxr.play.MCXRPlayClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ControlsScreen.class)
public class ControlsScreenMixin extends Screen {
    protected ControlsScreenMixin(Component component) {
        super(component);
    }

    @Inject(method = "init", at = @At("TAIL"))
    void init(CallbackInfo ci) {
        int l = this.width / 2 - 155;
        int m = l + 160;
        int n = this.height / 6 - 12;
        n += 72;
        this.addRenderableWidget(MCXRPlayClient.leftHanded.createButton(this.minecraft.options, m, n, 150));
    }
}
