package net.sorenon.mcxr.play.compat.create.mixin;

import com.jozufozu.flywheel.config.BackendType;
import com.jozufozu.flywheel.config.FlwConfig;
import com.jozufozu.flywheel.config.Option;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FlwConfig.class)
public abstract class FlwConfigMixin {
    @Mutable
    @Final
    @Shadow(remap = false)
    public Option.EnumOption<BackendType> backend;

    @Shadow protected abstract <T extends Option<?>> T addOption(T option);

    @Redirect(method = "<init>", at = @At(value = "FIELD", target = "Lcom/jozufozu/flywheel/config/FlwConfig;backend:Lcom/jozufozu/flywheel/config/Option$EnumOption;"), remap = false)
    public void FlwConfig(FlwConfig instance, Option.EnumOption<BackendType> value) {
        this.backend = this.addOption(new Option.EnumOption<>("backend", BackendType.OFF));
    }
}
