package com.uabart.todolist.handler;

import com.uabart.todolist.entity.Task;
import com.uabart.todolist.gui.components.FieldIcon;
import com.uabart.todolist.manager.Manager;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;

import codechicken.nei.api.INEIGuiAdapter;

public class NEIToDoGuiHandler extends INEIGuiAdapter {
    @Override
    public boolean handleDragNDrop(GuiContainer gui, int mousex, int mousey, ItemStack draggedStack, int button) {
        for (FieldIcon icon : Manager.getLayout().getFieldIcons()) {
            if (mousex >= icon.x && mousex <= icon.x + icon.w){
                if (mousey >= icon.y && mousey <= icon.y + icon.h){
                    Task task = icon.getTask();
                    draggedStack.stackSize = 1;
                    task.setReference(draggedStack);
                    if (task.getName().isEmpty() || task.getName().equals("Empty"))
                        task.setName(draggedStack.getDisplayName());
                    icon.update(task);
                    return false;
                }
            }
        }
        return true;
    }
}
