package com.uabart.todolist;

import com.uabart.todolist.handler.DrawHandler;
import com.uabart.todolist.handler.InputHandler;
import com.uabart.todolist.handler.TooltipHandler;

import codechicken.nei.api.IConfigureNEI;
import codechicken.nei.guihook.GuiContainerManager;

public class NEITodoConfig implements IConfigureNEI {

    @Override
    public void loadConfig() {
        GuiContainerManager.addDrawHandler(new DrawHandler());
        GuiContainerManager.addInputHandler(new InputHandler());
        GuiContainerManager.addTooltipHandler(new TooltipHandler());
    }

    @Override
    public String getName() {
        return "ToDo List";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

}
