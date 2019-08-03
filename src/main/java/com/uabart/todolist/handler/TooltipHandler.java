package com.uabart.todolist.handler;

import com.uabart.todolist.manager.Manager;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

import codechicken.nei.NEIClientConfig;
import codechicken.nei.Widget;
import codechicken.nei.guihook.IContainerTooltipHandler;

public class TooltipHandler implements IContainerTooltipHandler {
    private static final int RECIPE_MODE = 0;

    @Override
    public List<String> handleTooltip(final GuiContainer guiContainer,
        final int mouseX, final int mouseY, final List<String> list) {
        List<String> finalList = new ArrayList<String>();
        if (NEIClientConfig.getCheatMode() == RECIPE_MODE || !NEIClientConfig.isEnabled() || NEIClientConfig.isHidden()) {
            for (Widget widget : Manager.getLayout().getToDraw()) {
                finalList = widget.handleTooltip(mouseX, mouseY, list);
            }
        }
        return finalList;
    }

    @Override
    public List<String> handleItemDisplayName(GuiContainer guiContainer, ItemStack itemStack, List<String> list) {
        return list;
    }

    @Override
    public List<String> handleItemTooltip(GuiContainer guiContainer, ItemStack itemStack, int i, int i1, List<String> list) {
        return list;
    }
}
