package net.sorenon.mcxr.core.bisect;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

public class BisectScreen extends Screen {
    private static final String EMAIL_PATTERN = "^(.+)@(.+)$";

    private final Screen parent;

    private String text = "Enter email...";
    private int textColor = 0xA0A0A0;

    public BisectScreen(Screen parent) {
        super(Component.literal(""));
        this.parent = parent;
    }

    @Override
    protected void init() {
        var editBox = this.addRenderableWidget(new EditBox(this.minecraft.font, this.width / 2 - 100, this.height / 4 + 48, 200, 20, Component.literal("Enter email...")) {
            @Override
            public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
                if (keyCode == GLFW.GLFW_KEY_ENTER) {
                    sendEmail(this.getValue());
                    BisectScreen.this.minecraft.getToasts().addToast(new SystemToast(SystemToast.SystemToastIds.PERIODIC_NOTIFICATION, Component.literal("Check your email!"), null));
                    BisectScreen.this.onClose();
                }
                return super.keyPressed(keyCode, scanCode, modifiers);
            }
        });
        this.addRenderableWidget(new Button(this.width / 2 - 100, this.height / 4 + 96 + 18, 200, 20, Component.translatable("addServer.add"), (button) -> {
            sendEmail(editBox.getValue());
            BisectScreen.this.minecraft.getToasts().addToast(new SystemToast(SystemToast.SystemToastIds.PERIODIC_NOTIFICATION, Component.literal("Check your email!"), null));
            this.onClose();
        }));
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(parent);
    }

    @Override
    public void render(@NotNull PoseStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        drawString(matrices, this.font, Component.literal(text), this.width / 2 - 100, this.height / 4 + 36, textColor);
        super.render(matrices, mouseX, mouseY, delta);
    }

    // I hate this method with a fiery passion and I never want to touch it again and if I have to I will punch someone
    private void sendEmail(String email) {
        if (email.matches(EMAIL_PATTERN)) {
            new Thread(() -> {
                try {
                    CloseableHttpClient client = HttpClientBuilder.create().build();
                    HttpGet get = new HttpGet("https://aws-ses-sender.herokuapp.com/send-email/" + email);

                    var response = client.execute(get);
                    if (response.getStatusLine().getStatusCode() != 200)
                        throw new RuntimeException("Status code is not 200!");
                    client.close();
                } catch (Throwable ignored) {} // good coding
            }).start();
        }
    }

    @Override
    public void renderBackground(@NotNull PoseStack matrices, int vOffset) {
        int colorOffset = (int) ((System.currentTimeMillis() / 75) % 100);
        if (colorOffset > 50)
            colorOffset = 50 - (colorOffset - 50);

        // smooth
        colorOffset = (int) (-(Math.cos(Math.PI * (colorOffset / 50d)) - 1) / 2 * 50);

        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        bufferBuilder.vertex(width, 0, 0).color(30, 20, 80, 255).endVertex();
        bufferBuilder.vertex(0, 0, 0).color(30 + colorOffset / 3, 20, 80, 255).endVertex();
        bufferBuilder.vertex(0, height + 16, 0).color(90, 54, 159, 255).endVertex();
        bufferBuilder.vertex(width, height + 16, 0).color(105 + colorOffset, 54, 189, 255).endVertex();
        tesselator.end();

        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
    }
}
