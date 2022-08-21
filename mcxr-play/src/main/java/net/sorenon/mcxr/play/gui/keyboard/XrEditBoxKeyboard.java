package net.sorenon.mcxr.play.gui.keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.DirectJoinServerScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.network.chat.Component;
import net.sorenon.mcxr.core.bisect.BisectScreen;
import net.minecraft.network.chat.TranslatableComponent;
import net.sorenon.mcxr.play.gui.XrEditBoxScreen;

public class XrEditBoxKeyboard extends XrAbstract2DKeyboard {

    private final EditBox textField;
    private final XrEditBoxScreen editScreen;
    private final EditBox placeholderField;
    private final int buttonWidth;

    private boolean shift, caps;

    public XrEditBoxKeyboard(EditBox textField, XrEditBoxScreen editScreen, int buttonWidth) {
        super();
        this.textField = textField;
        this.editScreen = editScreen;

        placeholderField = new EditBox(Minecraft.getInstance().font,
                133, 22, textField.getWidth(), textField.getHeight(),
                textField, new TranslatableComponent(""));

        this.buttonWidth = buttonWidth;
    }

    @Override
    public void letterButton(Button instance) {
        String str = this.placeholderField.getValue() + instance.getMessage().getString();
        placeholderField.setValue(str);
    }

    @Override
    public void returnButton(Button instance) {
        this.textField.setValue(placeholderField.getValue());

        if (editScreen.getParentScreen().getClass() == DirectJoinServerScreen.class) {

            ServerData server = new ServerData(placeholderField.getValue(), "Server", false);
            ConnectScreen.startConnecting(editScreen.getParentScreen(), Minecraft.getInstance(), ServerAddress.parseString(placeholderField.getValue()), server);

        } else if (editScreen.getParentScreen().getClass() == JoinMultiplayerScreen.class) {

            ServerData server = new ServerData(placeholderField.getValue(), placeholderField.getValue(), false);
            editScreen.getServers().add(server);
            editScreen.getServers().save();
            Minecraft.getInstance().setScreen(editScreen.getParentScreen());


        } else if (editScreen.getParentScreen().getClass() == CreativeModeInventoryScreen.class) {

            char[] searchChars = placeholderField.getValue().toCharArray();
            Minecraft.getInstance().setScreen(editScreen.getParentScreen());
            for (char searchChar : searchChars) {
                Minecraft.getInstance().screen.charTyped(searchChar, 0);
            }

        } else if (editScreen.getParentScreen().getClass() == BisectScreen.class) {
            BisectScreen bisect = (BisectScreen) editScreen.getParentScreen();
            bisect.editBox.setValue(placeholderField.getValue());
            Minecraft.getInstance().setScreen(editScreen.getParentScreen());
        } else {

            Minecraft.getInstance().setScreen(editScreen.getParentScreen());

        }
    }

    @Override
    public void backSpaceButton(Button instance) {
        this.placeholderField.setValue(removeLastChar(this.placeholderField.getValue()));
    }

    @Override
    public void spaceButton(Button instance) {
        this.placeholderField.setValue(this.placeholderField.getValue() + ' ');
    }

    @Override
    public void tabButton(Button button) {
        this.placeholderField.setValue(this.placeholderField.getValue() + "    ");
    }

    @Override
    public void shiftButton(Button button) {
        shift = !shift;
        this.editScreen.clear();
        renderKeyboard(shift ? this.getShiftCharset() : caps ? this.getCapsCharset() : this.getDefaultCharset(), editScreen.width, editScreen.height, buttonWidth);
    }

    @Override
    public void capsButton(Button button) {
        caps = !caps;
        this.editScreen.clear();
        renderKeyboard(shift ? this.getShiftCharset() : caps ? this.getCapsCharset() : this.getDefaultCharset(), editScreen.width, editScreen.height, buttonWidth);
    }

    @Override
    public void renderKey(Button key) {
        editScreen.addRenderWidget(key);
    }

    @Override
    public void afterRender() {
        editScreen.addRenderWidget(placeholderField);
    }
}
