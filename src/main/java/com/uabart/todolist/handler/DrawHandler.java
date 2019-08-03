package com.uabart.todolist.handler;

import com.uabart.todolist.manager.Manager;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;

import codechicken.nei.NEIClientConfig;
import codechicken.nei.Widget;
import codechicken.nei.guihook.IContainerDrawHandler;

public class DrawHandler implements IContainerDrawHandler {

    private static final int RECIPE_MODE = 0;

    @Override
    public void onPreDraw(final GuiContainer gui) {
    }

    @Override
    public void renderObjects(final GuiContainer gui, final int mouseX, final int mouseY) {
        if (NEIClientConfig.getCheatMode() == RECIPE_MODE || !NEIClientConfig.isEnabled() || NEIClientConfig.isHidden()) {
            for (Widget widget : Manager.getLayout().getToDraw()) {
                widget.draw(mouseX, mouseY);
            }
        }
    }

    @Override
    public void postRenderObjects(final GuiContainer gui, final int mousex, final int mousey) {
    }

    @Override
    public void renderSlotUnderlay(final GuiContainer gui, final Slot slot) {
    }

    @Override
    public void renderSlotOverlay(final GuiContainer gui, final Slot slot) {
    }
}
