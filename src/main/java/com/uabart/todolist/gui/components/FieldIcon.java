package com.uabart.todolist.gui.components;

import com.uabart.todolist.entity.Task;
import com.uabart.todolist.logic.TaskListener;

import net.minecraft.item.ItemStack;

import org.lwjgl.input.Keyboard;

import java.util.List;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.NEIClientUtils;
import codechicken.nei.Widget;
import codechicken.nei.guihook.GuiContainerManager;
import codechicken.nei.recipe.GuiCraftingRecipe;
import codechicken.nei.recipe.GuiUsageRecipe;

public class FieldIcon extends Widget implements TaskListener {

    public boolean changing;
    public int x, y, offset;
    private ItemStack stack;
    private Task task;
    private boolean isBackgroundVisible = true;

    public FieldIcon(Task task) {
        super();
        this.task = task;
        this.task.setListener(this);
        this.stack = task.getReference();
    }

    @Override
    public FieldIcon clone() {
        FieldIcon cloneObject = new FieldIcon(this.task);
        cloneObject.x = this.x;
        cloneObject.y = this.y;
        cloneObject.w = this.w;
        cloneObject.h = this.h;
        return cloneObject;
    }

    @Override
    public void draw(int mousex, int mousey) {
        if (isBackgroundVisible) {
            drawBox();
        }
        if (stack != null && stack.getItem() != null) {
            GuiContainerManager.drawItem(x - offset, y - offset, stack);
        }
    }

    private void drawBox() {
        GuiDraw.drawRect(x - 2, y - 2, w + 2, h + 2, 0xffA0A0A0);

        if (changing)
            GuiDraw.drawRect(x - 1, y - 1, w, h, 0xee555555);
        else
            GuiDraw.drawRect(x - 1, y - 1, w, h, 0xee000000);
    }

    @Override
    public boolean contains(int posx, int posy) {
        if (posx >= x && posy >= y)
            if (posx <= (x + 15) && posy <= (y + 15))
                return true;
        return false;
    }

    @Override
    public boolean handleClick(int mousex, int mousey, int button) {
        ItemStack item = NEIClientUtils.getHeldItem();
        if (item != null) {
            ItemStack newStack = new ItemStack(item.getItem());
            newStack.stackSize = 1;
            newStack.setItemDamage(item.getItemDamage());
            task.setReference(newStack);
            if (task.getName().isEmpty() || task.getName().equals("Empty"))
                task.setName(newStack.getDisplayName());
            update(task);
            return super.handleClick(mousex, mousey, button);
        } else {
            switch (button) {
                case 0:
                    if (stack != null && stack.getItem() != null) {
                        GuiCraftingRecipe.openRecipeGui("item", stack);
                    }
                    changing = false;
                    return true;

                case 1:
                    if (stack != null && stack.getItem() != null) {
                        GuiUsageRecipe.openRecipeGui("item", stack);
                    }
                    changing = false;
                    return true;

                case 2:
                    changing = !changing;
                    return true;

                default:
                    return super.handleClick(mousex, mousey, button);
            }
        }
    }

    @Override
    public boolean handleKeyPress(int keyID, char keyChar) {
        if (changing) {
            if (keyID == Keyboard.KEY_ESCAPE) {
                changing = false;
                return true;
            }
        }
        return super.handleKeyPress(keyID, keyChar);
    }

    @Override
    public List<String> handleTooltip(int mx, int my, List<String> tooltip) {
        if (contains(mx, my)) {
            if (stack != null && stack.getItem() != null) {
                tooltip.add(stack.getDisplayName());
                tooltip.add("Middle click to change");
            } else
                tooltip.add("Middle click to select an item");

            if (changing)
                tooltip.add("ESC to cancel");
        }
        return tooltip;
    }

    public Task getTask() {
        return task;
    }

    @Override
    public void update(Task task) {
        this.stack = task.getReference();
    }

    public void setBackgroundVisible(boolean backgroundVisible) {
        isBackgroundVisible = backgroundVisible;
    }
}
