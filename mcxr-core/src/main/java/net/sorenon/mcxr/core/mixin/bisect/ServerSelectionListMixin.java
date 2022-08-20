package net.sorenon.mcxr.core.mixin.bisect;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.multiplayer.ServerSelectionList;
import net.sorenon.mcxr.core.bisect.BisectServerEntry;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerSelectionList.class)
public class ServerSelectionListMixin extends ObjectSelectionList<ServerSelectionList.Entry> {
    @Shadow @Final private JoinMultiplayerScreen screen;

    public ServerSelectionListMixin(Minecraft minecraft, int i, int j, int k, int l, int m) {
        super(minecraft, i, j, k, l, m);
    }

    @Inject(method = "refreshEntries", at = @At(value = "INVOKE", target = "Ljava/util/List;forEach(Ljava/util/function/Consumer;)V", ordinal = 0, shift = At.Shift.BEFORE))
    private void addBisect(CallbackInfo ci) {
        this.addEntry(new BisectServerEntry(this.screen));
    }

    /**
     * @author flamgop
     * @reason because
     */
    @Overwrite
    public void moveSelection(AbstractSelectionList.@NotNull SelectionDirection direction) {
        this.moveSelection(direction, (entry) -> !(entry instanceof ServerSelectionList.LANHeader) && !(entry instanceof BisectServerEntry));
    }
}
