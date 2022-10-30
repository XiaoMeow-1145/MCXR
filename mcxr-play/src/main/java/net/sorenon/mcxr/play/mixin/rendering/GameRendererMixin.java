package net.sorenon.mcxr.play.mixin.rendering;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.phys.Vec3;
import net.sorenon.mcxr.play.MCXRPlayClient;
import net.sorenon.mcxr.play.accessor.Matrix4fExt;
import net.sorenon.mcxr.play.openxr.MCXRGameRenderer;
import net.sorenon.mcxr.play.rendering.MCXRCamera;
import net.sorenon.mcxr.play.rendering.MCXRMainTarget;
import net.sorenon.mcxr.play.rendering.RenderPass;
import org.joml.Quaternionf;
import org.lwjgl.opengl.GL11;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = GameRenderer.class, priority = 10_000)
public abstract class GameRendererMixin{

    @Unique
    private static final MCXRGameRenderer XR_RENDERER = MCXRPlayClient.MCXR_GAME_RENDERER;

    @Shadow
    public abstract float getRenderDistance();

    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    private boolean renderHand;

    /**
     * Replace the default camera with an MCXRCamera
     */
    @Redirect(method = "<init>", at = @At(value = "NEW", target = "net/minecraft/client/Camera"))
    Camera replaceCamera() {
        return new MCXRCamera();
    }

    /**
     * Update the framebuffer dimensions in MCXRMainTarget so we know if framebuffers need resizing
     */
    @Inject(method = "resize", at = @At("HEAD"))
    void onResized(int i, int j, CallbackInfo ci) {
        MCXRMainTarget MCXRMainTarget = (MCXRMainTarget) minecraft.getMainRenderTarget();
        MCXRMainTarget.minecraftFramebufferWidth = i;
        MCXRMainTarget.minecraftFramebufferHeight = j;
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    void cancelVanillaRendering(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
        if(XR_RENDERER.renderPass == RenderPass.VANILLA && !(Minecraft.getInstance().screen instanceof TitleScreen)) {
            ci.cancel();
        }
    }

    /**
     * Cancels both vanilla and Iris hand rendering. Also cancels ScreenEffectRenderer call.
     */
    @Inject(method = "renderLevel", at = @At("HEAD"))
    void cancelRenderHand(CallbackInfo ci) {
        this.renderHand = XR_RENDERER.renderPass == RenderPass.VANILLA;
    }

    @Inject(method = "renderConfusionOverlay", at = @At("HEAD"), cancellable = true)
    void cancelRenderConfusion(float f, CallbackInfo ci) {
        if (XR_RENDERER.renderPass != RenderPass.VANILLA) {
            ci.cancel();
        }
    }

    @Inject(method = "bobView", at = @At("HEAD"), cancellable = true)
    void cancelBobView(PoseStack matrixStack, float f, CallbackInfo ci) {
        if (XR_RENDERER.renderPass != RenderPass.VANILLA) {
            ci.cancel();
        }
    }

    /**
     * Replace the vanilla projection matrix
     */
    @Inject(method = "getProjectionMatrix", at = @At("HEAD"), cancellable = true)
    void getXrProjectionMatrix(double d, CallbackInfoReturnable<Matrix4f> cir) {
        if (XR_RENDERER.renderPass instanceof RenderPass.XrWorld renderPass) {
            Matrix4f proj = new Matrix4f();
            ((Matrix4fExt) (Object) proj).setXrProjection(renderPass.fov, 0.05F, this.getRenderDistance() * 4);
            cir.setReturnValue(proj);
        }
    }

    /**
     * Rotate the matrix stack using a quaternion rather than pitch and yaw
     */
    @Redirect(at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;mulPose(Lcom/mojang/math/Quaternion;)V", ordinal = 2), method = "renderLevel")
    void multiplyPitch(PoseStack matrixStack, Quaternion pitchQuat) {
        if (XR_RENDERER.renderPass == RenderPass.VANILLA) {
            matrixStack.mulPose(pitchQuat);
        }
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;mulPose(Lcom/mojang/math/Quaternion;)V", ordinal = 3), method = "renderLevel")
    void multiplyYaw(PoseStack matrixStack, Quaternion yawQuat) {
        if (XR_RENDERER.renderPass instanceof RenderPass.XrWorld xrWorldPass) {
            var inv = xrWorldPass.eyePoses.getMinecraftPose().getOrientation().invert(new Quaternionf());
            matrixStack.mulPose(new Quaternion(inv.x, inv.y, inv.z, inv.w));
        } else {
            matrixStack.mulPose(yawQuat);
        }
    }

    /**
     * If we are doing a gui render pass => return null to skip rendering the world
     */
    @Redirect(at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;level:Lnet/minecraft/client/multiplayer/ClientLevel;", opcode = Opcodes.GETFIELD, ordinal = 0), method = "render")
    public ClientLevel getWorld(Minecraft client) {
        if (XR_RENDERER.renderPass == RenderPass.GUI) {
            return null;
        } else {
            return client.level;
        }
    }

    /**
     * If we are doing a world render pass => return early to skip rendering the gui
     */
    @Inject(at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;clear(IZ)V", ordinal = 0, shift = At.Shift.BEFORE), method = "render", cancellable = true)
    public void guiRenderStart(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
        if (XR_RENDERER.renderPass instanceof RenderPass.XrWorld) {
            ci.cancel();
        }
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;getWindow()Lcom/mojang/blaze3d/platform/Window;", shift = At.Shift.BEFORE, ordinal = 6), method = "Lnet/minecraft/client/renderer/GameRenderer;render(FJZ)V", cancellable = true)
    public void mainMenu(float partialTicks, long nanoTime, boolean renderWorldIn, CallbackInfo info) {
        if (renderWorldIn && this.minecraft.level != null) {

        }
        else {
            this.minecraft.getProfiler().push("MainMenu");
            GL11.glDisable(GL11.GL_STENCIL_TEST);

            PoseStack pMatrixStack = new PoseStack();
            this.renderJrbuddasAwesomeMainMenuRoomNew(pMatrixStack);

        }
    }
        public void renderJrbuddasAwesomeMainMenuRoomNew(PoseStack pMatrixStack) {
            int i = 4;
            float f = 2.5F;
            float f1 = 1.3F;
            float[] afloat = null;
            if (afloat == null)
                afloat = new float[] { 2, 2 };

            BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
            RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
            RenderSystem.depthFunc(519);
            RenderSystem.depthMask(true); //TODO temp fix
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableTexture();
            RenderSystem.setShaderTexture(0, Screen.BACKGROUND_LOCATION);
            RenderSystem.setShaderColor(1, 1, 1, 1);
            pMatrixStack.pushPose();
            float f2 = afloat[0] + f1;
            float f3 = afloat[1] + f1;
            pMatrixStack.translate(-f2 / 2.0F, 0.0F, -f3 / 2.0F);

            Matrix4f matrix4f = pMatrixStack.last().pose();
            bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL);

            float a, b, c, d;
            a = b = c = d = 0.8f;

            bufferbuilder.vertex(matrix4f, 0, 0, 0).uv(0, 0).color(a, b, c, d).normal(0, 1, 0).endVertex();
            bufferbuilder.vertex(matrix4f, 0, 0, f3).uv(0, i * f3).color(a, b, c, d).normal(0, 1, 0).endVertex();
            bufferbuilder.vertex(matrix4f, f2, 0, f3).uv(i * f2, i * f3).color(a, b, c, d).normal(0, 1, 0).endVertex();
            bufferbuilder.vertex(matrix4f, f2, 0, 0).uv(i * f2, 0).color(a, b, c, d).normal(0, 1, 0).endVertex();

            bufferbuilder.vertex(matrix4f, 0, f, f3).uv(0, 0).color(a, b, c, d).normal(0, -1, 0).endVertex();
            bufferbuilder.vertex(matrix4f, 0, f, 0).uv(0, i * f3).color(a, b, c, d).normal(0, -1, 0).endVertex();
            bufferbuilder.vertex(matrix4f, f2, f, 0).uv(i * f2, i * f3).color(a, b, c, d).normal(0, -1, 0).endVertex();
            bufferbuilder.vertex(matrix4f, f2, f, f3).uv(i * f2, 0).color(a, b, c, d).normal(0, -1, 0).endVertex();

            bufferbuilder.vertex(matrix4f, 0, 0, 0).uv(0, 0).color(a, b, c, d).normal(1, 0, 0).endVertex();
            bufferbuilder.vertex(matrix4f, 0, f, 0).uv(0, i * f).color(a, b, c, d).normal(1, 0, 0).endVertex();
            bufferbuilder.vertex(matrix4f, 0, f, f3).uv(i * f3, i * f).color(a, b, c, d).normal(1, 0, 0).endVertex();
            bufferbuilder.vertex(matrix4f, 0, 0, f3).uv(i * f3, 0).color(a, b, c, d).normal(1, 0, 0).endVertex();

            bufferbuilder.vertex(matrix4f, f2, 0, 0).uv(0, 0).color(a, b, c, d).normal(-1, 0, 0).endVertex();
            bufferbuilder.vertex(matrix4f, f2, 0, f3).uv(i * f3, 0).color(a, b, c, d).normal(-1, 0, 0).endVertex();
            bufferbuilder.vertex(matrix4f, f2, f, f3).uv(i * f3, i * f).color(a, b, c, d).normal(-1, 0, 0).endVertex();
            bufferbuilder.vertex(matrix4f, f2, f, 0).uv(0, i * f).color(a, b, c, d).normal(-1, 0, 0).endVertex();

            bufferbuilder.vertex(matrix4f, 0, 0, 0).uv(0, 0).color(a, b, c, d).normal(0, 0, 1).endVertex();
            bufferbuilder.vertex(matrix4f, f2, 0, 0).uv(i * f2, 0).color(a, b, c, d).normal(0, 0, 1).endVertex();
            bufferbuilder.vertex(matrix4f, f2, f, 0).uv(i * f2, i * f).color(a, b, c, d).normal(0, 0, 1).endVertex();
            bufferbuilder.vertex(matrix4f, 0, f, 0).uv(0, i * f).color(a, b, c, d).normal(0, 0, 1).endVertex();

            bufferbuilder.vertex(matrix4f, 0, 0, f3).uv(0, 0).color(a, b, c, d).normal(0, 0, -1).endVertex();
            bufferbuilder.vertex(matrix4f, 0, f, f3).uv(0, i * f).color(a, b, c, d).normal(0, 0, -1).endVertex();
            bufferbuilder.vertex(matrix4f, f2, f, f3).uv(i * f2, i * f).color(a, b, c, d).normal(0, 0, -1).endVertex();
            bufferbuilder.vertex(matrix4f, f2, 0, f3).uv(i * f2, 0).color(a, b, c, d).normal(0, 0, -1).endVertex();

            bufferbuilder.end();
            BufferUploader.end(bufferbuilder);
            pMatrixStack.popPose();

        }
    }