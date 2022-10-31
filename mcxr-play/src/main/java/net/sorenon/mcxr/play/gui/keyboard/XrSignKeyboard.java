package net.sorenon.mcxr.play.gui.keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundSignUpdatePacket;
import net.sorenon.mcxr.play.gui.XrSignEditScreen;

public class XrSignKeyboard extends XrAbstract2DKeyboard {

    private final EditBox textField1;
    private final EditBox textField2;
    private final EditBox textField3;
    private final EditBox textField4;

    private EditBox activeTextField;

    private final XrSignEditScreen signEditScreen;

    private boolean shift, caps;

    public XrSignKeyboard(XrSignEditScreen signEditScreen) {

        this.signEditScreen = signEditScreen;
        textField1 = new EditBox(Minecraft.getInstance().font,
               (signEditScreen.width /3) - 160, 0, 160, 10, Component.translatable(""));
        textField2 = new EditBox(Minecraft.getInstance().font,
               (signEditScreen.width /3) - 160, 10, 160, 10, Component.translatable(""));
        textField3 = new EditBox(Minecraft.getInstance().font,
               (signEditScreen.width /3) - 160, 20, 160, 10, Component.translatable(""));
        textField4 = new EditBox(Minecraft.getInstance().font,
                (signEditScreen.width /3) - 160, 30, 160, 10, Component.translatable(""));

        activeTextField = textField1;
    }

    @Override
    public void returnButton(Button instance) {
        signEditScreen.getSign().setMessage(0, Component.translatable(textField1.getValue()));
        signEditScreen.getSign().setMessage(1, Component.translatable(textField2.getValue()));
        signEditScreen.getSign().setMessage(2, Component.translatable(textField3.getValue()));
        signEditScreen.getSign().setMessage(3, Component.translatable(textField4.getValue()));
        ClientPacketListener clientPacketListener = Minecraft.getInstance().getConnection();
        if (clientPacketListener != null) {
            clientPacketListener.send(new ServerboundSignUpdatePacket(this.signEditScreen.getSign().getBlockPos(), textField1.getValue(), textField2.getValue(), textField3.getValue(), textField4.getValue()));
        }
        signEditScreen.onClose();
    }

    public EditBox getTextField1() {
        return textField1;
    }

    public EditBox getTextField2() {
        return textField2;
    }

    public EditBox getTextField3() {
        return textField3;
    }

    public EditBox getTextField4() {
        return textField4;
    }

    public EditBox getActiveTextField() {
        return activeTextField;
    }

    public void setActiveTextField(EditBox _activeTextField) {
        this.activeTextField = _activeTextField;
    }

    @Override
    public void backSpaceButton(Button instance) {
        activeTextField.setValue(removeLastChar(activeTextField.getValue()));
    }

    @Override
    public void spaceButton(Button button) {
        activeTextField.setValue(activeTextField.getValue() + " ");
    }

    @Override
    public void tabButton(Button button) {
        activeTextField.setValue(activeTextField.getValue() + "    ");
    }

    @Override
    public void shiftButton(Button button) {
        shift = !shift;
        this.signEditScreen.clear();
        renderKeyboard(shift ? this.getShiftCharset() : caps ? this.getCapsCharset() : this.getDefaultCharset(), signEditScreen.width, signEditScreen.height, 30);
    }

    @Override
    public void capsButton(Button button) {
        caps = !caps;
        this.signEditScreen.clear();
        renderKeyboard(shift ? this.getShiftCharset() : caps ? this.getCapsCharset() : this.getDefaultCharset(), signEditScreen.width, signEditScreen.height, 30);
    }

    @Override
    public void letterButton(Button instance) {
        String stringText = activeTextField.getValue() + instance.getMessage().getString();
        activeTextField.setValue(stringText);
    }

    @Override
    public void renderKey(Button key) {
        signEditScreen.addRenderWidget(key);
    }

    @Override
    public void afterRender() {
        signEditScreen.addRenderWidget(textField1);
        signEditScreen.addRenderWidget(textField2);
        signEditScreen.addRenderWidget(textField3);
        signEditScreen.addRenderWidget(textField4);
    }
}
