package net.sorenon.mcxr.play.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.sorenon.mcxr.play.gui.keyboard.XrSignKeyboard;
import org.jetbrains.annotations.NotNull;

public class XrSignEditScreen extends Screen {

    private final SignBlockEntity sign;
    private final XrSignKeyboard keyboard;

    public XrSignEditScreen(Component title, SignBlockEntity sign) {

        super(title);
        this.sign = sign;

        this.width = Minecraft.getInstance().getWindow().getWidth();
        this.height = Minecraft.getInstance().getWindow().getHeight();

        keyboard = new XrSignKeyboard(this);

    }

    public void clear() {
        this.clearWidgets();
    }

    public SignBlockEntity getSign() {
        return sign;
    }

    public void addRenderWidget(AbstractWidget widget) {
        this.addRenderableWidget(widget);
    }

    @Override
    protected void init() {
        keyboard.renderKeyboard(keyboard.getDefaultCharset(), this.width, this.height, 30);
        super.init();
    }

    @Override
    public void render(@NotNull PoseStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);

        if (keyboard.getTextField1().isFocused()) {
            keyboard.setActiveTextField(keyboard.getTextField1());
        }

        if (keyboard.getTextField2().isFocused()) {
            keyboard.setActiveTextField(keyboard.getTextField2());
        }

        if (keyboard.getTextField3().isFocused()) {
            keyboard.setActiveTextField(keyboard.getTextField3());
        }

        if (keyboard.getTextField4().isFocused()) {
            keyboard.setActiveTextField(keyboard.getTextField4());
        }

    }
}
