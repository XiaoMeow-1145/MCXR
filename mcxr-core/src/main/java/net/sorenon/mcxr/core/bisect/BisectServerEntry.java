package net.sorenon.mcxr.core.bisect;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
//import net.kyori.adventure.platform.fabric.FabricClientAudiences;
//import net.kyori.adventure.text.TextComponent;
//import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.multiplayer.ServerSelectionList;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
//import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

// Only used internally by the Bisect partner thingy at the top of the server list
@Environment(EnvType.CLIENT)
public class BisectServerEntry extends ServerSelectionList.Entry {
    private final JoinMultiplayerScreen screen;
    private final Minecraft minecraft;
    private final ResourceLocation iconLocation;
    private final List<Particle> particles = new ArrayList<>();
    private boolean hasBeenClicked = false;
    private float hue = 0;

    private static final int colorTop = 0xE003DDFF;
    private static final int colorBottom = 0xE003DDFF;
    private static final int fill = 0xC0121935;
    private static final int fillHovered = 0xC02A3A7A;

    private static final ResourceLocation LOCATION = new ResourceLocation("textures/gui/server_selection.png");

//    private final FabricClientAudiences audience = FabricClientAudiences.of();
//    private final MiniMessage miniMessage = MiniMessage.miniMessage();
//    private net.kyori.adventure.text.Component kyoriComponent = miniMessage.deserialize("<rainbow>whatever</rainbow>");

//    private long lastMillis = System.currentTimeMillis();
//    private int phase = 0;

    public BisectServerEntry(JoinMultiplayerScreen joinMultiplayerScreen) {
        this.screen = joinMultiplayerScreen;
        this.minecraft = Minecraft.getInstance();
        this.iconLocation = new ResourceLocation("mcxr-core", "bisect.png");
    }

    public void render(@NotNull PoseStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        List<Particle> tempParts = new ArrayList<>();
        hue += 0.01;
        if (hue >= 1) hue = 0;

        for (Particle p : particles) {
            p.updateParticles();
            if (p.isDead())
                tempParts.add(p);
        }

        particles.removeAll(tempParts);

        for (Particle p : particles) {
            for (int[] p1 : p.getParticles()) {
                GuiComponent.fill(matrices, p1[0], p1[1], p1[0] + 1, p1[1] + 1, p.getColor());
            }
        }

        int color = Color.getHSBColor(hue, 1, 1).getRGB();
        switch (ThreadLocalRandom.current().nextInt(4)) {
            case 0: particles.add(new Particle(x, y, color));
            case 1: particles.add(new Particle(x, y+entryHeight, color));
            case 2: particles.add(new Particle(x+entryWidth, y, color));
            case 3: particles.add(new Particle(x+entryWidth, y+entryHeight, color));
        }

        fill(matrices,
                x-1, y-1, x+entryWidth, y+entryHeight+1,
                colorTop, colorBottom,
                isMouseOver(mouseX, mouseY) ? fillHovered : fill);

        this.minecraft.font.draw(matrices, "Use BisectHosting for the best experience!", (float)(x + 32 + 3), (float)(y + 1), 16777215);
//        if (System.currentTimeMillis()-lastMillis>=50) {
//            phase++;
//            lastMillis = System.currentTimeMillis();
//        }
//        if (phase >= ((TextComponent)kyoriComponent).content().length()) phase = 0;
//        kyoriComponent = miniMessage.deserialize("<rainbow:"+phase+">whatever</rainbow>");
//
//        for(int i = 0; i < Math.min(((TextComponent)kyoriComponent).content().split("\n").length, 2); ++i) {
//            Objects.requireNonNull(this.minecraft.font);
//            this.minecraft.font.draw(matrices, audience.toNative(kyoriComponent), (float)(x + 32 + 3), (float)(y + 12 + 9 * i), 8421504);
//        }
        this.minecraft.font.draw(matrices, Component.literal("Click here to get a server from bisect 50% off!"), (float)(x + 32 + 3), (float)(y + 12), 0xAAAAAA);

        this.drawIcon(matrices, x, y, this.iconLocation);

        if ((this.minecraft.options.touchscreen().get() || hovered) && !hasBeenClicked) {
            RenderSystem.setShaderTexture(0, LOCATION);
            GuiComponent.fill(matrices, x, y, x + 32, y + 32, -1601138544);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            int o = mouseX - x;
            int p = mouseY - y;
            if (index > 0) {
                if (o < 16 && p < 16) {
                    GuiComponent.blit(matrices, x, y, 96.0F, 32.0F, 32, 32, 256, 256);
                } else {
                    GuiComponent.blit(matrices, x, y, 96.0F, 0.0F, 32, 32, 256, 256);
                }
            }

            if (index < this.screen.getServers().size() - 1) {
                if (o < 16 && p > 16) {
                    GuiComponent.blit(matrices, x, y, 64.0F, 32.0F, 32, 32, 256, 256);
                } else {
                    GuiComponent.blit(matrices, x, y, 64.0F, 0.0F, 32, 32, 256, 256);
                }
            }
        }
    }

    private static void fill(PoseStack matrices, int x1, int y1, int x2, int y2, int colTop, int colBot, int colFill) {
        GuiComponent.fill(matrices, x1, y1 + 1, x1 + 1, y2 - 1, colTop);
        GuiComponent.fill(matrices, x1 + 1, y1, x2 - 1, y1 + 1, colTop);
        GuiComponent.fill(matrices, x2 - 1, y1 + 1, x2, y2 - 1, colBot);
        GuiComponent.fill(matrices, x1 + 1, y2 - 1, x2 - 1, y2, colBot);
        GuiComponent.fill(matrices, x1 + 1, y1 + 1, x2 - 1, y2 - 1, colFill);
    }

    protected void drawIcon(PoseStack matrices, int x, int y, ResourceLocation textureId) {
        RenderSystem.setShaderTexture(0, textureId);
        RenderSystem.enableBlend();
        GuiComponent.blit(matrices, x, y, 0.0F, 0.0F, 32, 32, 32, 32);
        RenderSystem.disableBlend();
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (hasBeenClicked) return false;
        hasBeenClicked = true;

        this.minecraft.setScreen(new BisectScreen(this.screen));

        return true;
    }

    public Component getNarration() {
        return Component.translatable("narrator.select", "BisectHosting");
    }

    private static class Particle {
        private final Random rand = ThreadLocalRandom.current();
        private int tick = 0;
        private final int x;
        private final int y;
        private final int lifespan;
        private boolean dead = false;
        private long lastTick;
        private int color;

        private final List<int[]> particles = new ArrayList<>();

        public Particle(int x, int y, int color) {
            this.x = x;
            this.y = y;
            this.color = color;
            genParticles();
            lastTick = System.currentTimeMillis();
            lifespan = rand.nextInt(20);
        }

        public void genParticles() {
            for (int j = 0; j < rand.nextInt(10); j++) {
                particles.add(new int[] { x + rand.nextInt(5) - 2, y + rand.nextInt(5) - 2 });
            }
        }

        public int getColor() {
            return color;
        }

        public void updateParticles() {
            if (System.currentTimeMillis() < lastTick + 16)
                return;
            lastTick = System.currentTimeMillis();

            tick++;

            if (tick > lifespan) {
                dead = true;
                particles.clear();
            }

            for (int i = 0; i < particles.size() - 1; i++) {
                int[] pos = particles.get(i);
                int diffX = pos[0] - x;
                int diffY = pos[1] - y;

                particles.set(i, new int[] { pos[0] + (diffX / tick), pos[1] + (diffY / tick) });
            }
        }

        public List<int[]> getParticles() {
            return particles;
        }

        public boolean isDead() {
            return dead;
        }
    }
}
