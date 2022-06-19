package net.sorenon.mcxr.play.gui;

import com.electronwill.nightconfig.core.Config;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.sorenon.mcxr.play.compat.svc.SimpleVoiceChatCompat;
import net.sorenon.mcxr.play.mixin.accessor.MouseHandlerAcc;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;

import static net.sorenon.mcxr.play.PlayOptions.jsonFileConfig;

public class QuickMenu extends Screen {
    public static ArrayList<Button> QuickMenuButtons = new ArrayList<>();

    public QuickMenu(Component component) {
        super(component);
    }

    private void renderMenuButtons() {

        QuickMenuButtons.add(new Button((this.width/2) - 25, this.height/2, 70, 20, Component.translatable("QuickChat"), (button ) -> {
            Minecraft.getInstance().setScreen(new net.sorenon.mcxr.play.gui.QuickChat("QuickChat"));
        }));
        if (FabricLoader.getInstance().isModLoaded("voicechat")) {
            SimpleVoiceChatCompat.createButton(QuickMenuButtons, this.width, this.height);
        }

        QuickMenuButtons.add(new Button((this.width/2) - 25, this.height/2, 70, 20, Component.translatable("Chat"), (button ) -> {
            Minecraft.getInstance().setScreen(new XrChatScreen(""));
        }));

        ArrayList<Config> c = jsonFileConfig.get("buttons");
        for(Config entry : c) {
            String name = entry.get("name");
            int key = entry.get("key");
            if(jsonFileConfig != null) {
                QuickMenuButtons.add(new Button((this.width / 2) - 25, this.height / 2 + 20, 70 + Minecraft.getInstance().font.width(name), 20, Component.literal(name), button -> {
                    MouseHandlerAcc mouseHandler = (MouseHandlerAcc) Minecraft.getInstance().mouseHandler;
                    mouseHandler.callOnPress(Minecraft.getInstance().getWindow().getWindow(),
                            key, GLFW.GLFW_PRESS, 0);
                }));
            }
        }

        for (int i = 0; i < QuickMenuButtons.size(); i++) {
            Button QuickMenuButton = QuickMenuButtons.get(i);

            QuickMenuButton.x = (this.width / 2) - (QuickMenuButton.getWidth()/2);
            QuickMenuButton.y = (this.height / 3) + (i*30);

            addRenderableWidget(QuickMenuButton);
        }

    }

    @Override
    protected void init() {
        super.init();
        renderMenuButtons();
    }

    @Override
    public void render(PoseStack poseStack, int i, int j, float f) {
        super.render(poseStack, i, j, f);
    }
}
