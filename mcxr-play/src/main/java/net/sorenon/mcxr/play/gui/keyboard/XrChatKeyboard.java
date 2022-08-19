package net.sorenon.mcxr.play.gui.keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.sorenon.mcxr.play.gui.XrChatScreen;

public class XrChatKeyboard extends XrAbstract2DKeyboard {

    private final EditBox chatBox;
    private final XrChatScreen chatScreen;
    private final int buttonWidth;

    private boolean shift, caps;

    public XrChatKeyboard(EditBox chatBox, XrChatScreen chatScreen, int buttonWidth) {
        this.chatBox = chatBox;
        this.chatScreen = chatScreen;
        this.buttonWidth = buttonWidth;
    }

    @Override
    public void shiftButton(Button button) {
        shift = !shift;
        this.chatScreen.clear();
        renderKeyboard(shift ? this.getShiftCharset() : caps ? this.getCapsCharset() : this.getDefaultCharset(), chatScreen.width, chatScreen.height, buttonWidth);
    }

    @Override
    public void capsButton(Button button) {
        caps = !caps;
        this.chatScreen.clear();
        renderKeyboard(shift ? this.getShiftCharset() : caps ? this.getCapsCharset() : this.getDefaultCharset(), chatScreen.width, chatScreen.height, buttonWidth);
    }

    @Override
    public void tabButton(Button button) {
        chatBox.setValue(chatBox.getValue() + "    ");
    }

    @Override
    public void spaceButton(Button button) {
        chatBox.setValue(chatBox.getValue() + " ");
    }

    @Override
    public void backSpaceButton(Button instance) {
        chatBox.setValue(removeLastChar(chatBox.getValue()));
    }

    @Override
    public void returnButton(Button instance) {
        if (chatBox.getValue().equals("")) {
            chatScreen.onClose();
        } else {
            Minecraft.getInstance().player.chat(chatBox.getValue());
            chatScreen.onClose();
        }
    }

    @Override
    public void letterButton(Button instance) {
        String stringText = chatBox.getValue() + instance.getMessage().getString();
        chatBox.setValue(stringText);
    }

    @Override
    public void renderKey(Button key) {
        chatScreen.addRenderWidget(key);
    }

//    @Override
//    public void afterRender() {
//        //_chatScreen.addRenderWidget(_chatBox);
//    }
}
