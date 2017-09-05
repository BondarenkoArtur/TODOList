package com.uabart.todolist.manager;

import com.uabart.todolist.entity.TaskHolder;
import com.uabart.todolist.gui.Layout;
import com.uabart.todolist.logic.Logic;

import net.minecraft.client.gui.inventory.GuiContainer;

public class Manager {

    private static Layout layout;
    private static TaskHolder holder;
    private static Logic logic;

    public static void newInstances() {
        layout = new Layout();
        holder = new TaskHolder();
        logic = new Logic();
    }

    public static void init(GuiContainer gui) {
        holder.init();
        layout.init(gui, holder);
        logic.init(layout, holder);
    }

    public static void finalizeInstances() {
    }

    public static TaskHolder getHolder() {
        return holder;
    }

    public static Layout getLayout() {
        return layout;
    }

    public static Logic getLogic() {
        return logic;
    }

}
