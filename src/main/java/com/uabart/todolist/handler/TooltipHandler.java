package com.uabart.todolist.handler;

import com.uabart.todolist.manager.Manager;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;

import java.util.List;

import codechicken.nei.Widget;
import codechicken.nei.guihook.IContainerTooltipHandler;

public class TooltipHandler implements IContainerTooltipHandler {

    @Override
    public List<String> handleTooltip(GuiContainer guiContainer, int mousex, int mousey, List<String> list) {
        for (Widget widget : Manager.getLayout().getToDraw())
            list = widget.handleTooltip(mousex, mousey, list);
        return list;
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
