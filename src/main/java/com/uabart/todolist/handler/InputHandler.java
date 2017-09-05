package com.uabart.todolist.handler;

import com.uabart.todolist.manager.Manager;

import net.minecraft.client.gui.inventory.GuiContainer;

import codechicken.nei.Widget;
import codechicken.nei.guihook.IContainerInputHandler;

public class InputHandler implements IContainerInputHandler {

    @Override
    public boolean keyTyped(GuiContainer gui, char keyChar, int keyCode) {
        for (Widget widget : Manager.getLayout().getToDraw()) {
            if (widget.handleKeyPress(keyCode, keyChar))
                return true;
        }

        return false;
    }

    @Override
    public void onKeyTyped(GuiContainer gui, char keyChar, int keyID) {

    }

    @Override
    public boolean lastKeyTyped(GuiContainer gui, char keyChar, int keyID) {
        return false;
    }

    @Override
    public boolean mouseClicked(GuiContainer gui, int mousex, int mousey, int button) {
        for (Widget widget : Manager.getLayout().getToDraw()) {
            widget.onGuiClick(mousex, mousey);
            if (widget.contains(mousex, mousey) ? widget.handleClick(mousex, mousey, button) : widget.handleClickExt(mousex, mousey, button))
                return true;
        }

        return false;
    }

    @Override
    public void onMouseClicked(GuiContainer gui, int mousex, int mousey, int button) {
        for (Widget widget : Manager.getLayout().getToDraw()) {
            widget.onGuiClick(mousex, mousey);
        }
    }

    @Override
    public void onMouseUp(GuiContainer gui, int mousex, int mousey, int button) {
    }

    @Override
    public boolean mouseScrolled(GuiContainer gui, int mousex, int mousey, int scrolled) {
        for (Widget widget : Manager.getLayout().getToDraw()) {
            if (widget.onMouseWheel(scrolled, mousex, mousey))
                return true;
        }
        return false;
    }

    @Override
    public void onMouseScrolled(GuiContainer gui, int mousex, int mousey, int scrolled) {
    }

    @Override
    public void onMouseDragged(GuiContainer gui, int mousex, int mousey, int button, long heldTime) {
    }

}
