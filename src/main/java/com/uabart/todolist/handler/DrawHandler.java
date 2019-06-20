package com.uabart.todolist.handler;

import com.uabart.todolist.manager.Manager;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;

import codechicken.nei.NEIClientConfig;
import codechicken.nei.Widget;
import codechicken.nei.guihook.IContainerDrawHandler;

public class DrawHandler implements IContainerDrawHandler {

    private static final int RECIPE_MODE = 0;
    public static boolean init = true;

    @Override
    public void onPreDraw(GuiContainer gui) {
        if (init) {
            Manager.init(gui);
            init = false;
        }
    }

    @Override
    public void renderObjects(final GuiContainer gui, final int mouseX, final int mouseY) {
        if (NEIClientConfig.getCheatMode() == RECIPE_MODE || !NEIClientConfig.isEnabled()) {
            for (Widget widget : Manager.getLayout().getToDraw()) {
                widget.draw(mouseX, mouseY);
            }
        }
    }

    @Override
    public void postRenderObjects(GuiContainer gui, int mousex, int mousey) {
    }

    @Override
    public void renderSlotUnderlay(GuiContainer gui, Slot slot) {
    }

    @Override
    public void renderSlotOverlay(GuiContainer gui, Slot slot) {
    }
}
